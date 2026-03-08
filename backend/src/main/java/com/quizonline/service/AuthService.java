package com.quizonline.service;

import com.quizonline.config.JwtUtil;
import com.quizonline.dto.*;
import com.quizonline.entity.Student;
import com.quizonline.entity.Teacher;
import com.quizonline.repository.StudentRepository;
import com.quizonline.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    // OTP storage: email -> {otp, mode, expiry, tempData}
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    // --- LOGIN (reuse logic from old AccountController.login) ---
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String role = request.getRole();

        if ("teacher".equals(role)) {
            Optional<Teacher> opt = teacherRepository.findByEmail(email);
            if (opt.isEmpty()) {
                throw new RuntimeException("Email does not exist!");
            }
            Teacher t = opt.get();
            if (!passwordEncoder.matches(password, t.getPassword())) {
                throw new RuntimeException("Wrong password!");
            }
            String token = jwtUtil.generateToken(email, "teacher", t.getTeacherId());
            return new LoginResponse(token, "teacher", email, t.getFullname(), t.getTeacherId());
        } else {
            Optional<Student> opt = studentRepository.findByEmail(email);
            if (opt.isEmpty()) {
                throw new RuntimeException("Email does not exist!");
            }
            Student s = opt.get();
            if (!passwordEncoder.matches(password, s.getPassword())) {
                throw new RuntimeException("Wrong password!");
            }
            String token = jwtUtil.generateToken(email, "student", s.getStudentId());
            return new LoginResponse(token, "student", email, s.getFullname(), s.getStudentId());
        }
    }

    // --- REGISTER STUDENT (reuse logic from old AccountController.registerStudent) ---
    public ApiResponse registerStudent(RegisterStudentRequest request) {
        // Check email exists
        if (studentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email has been existed!");
        }

        // Password validation (same regex as old project)
        String regex = "^[a-zA-Z0-9]{8,}$";
        if (!request.getPassword().matches(regex)) {
            throw new RuntimeException("Password must contain at least 8 alphanumeric characters.");
        }
        if (!request.getPassword().equals(request.getRepassword())) {
            throw new RuntimeException("Confirm password is not the same as password!");
        }

        // Generate OTP and send email
        int otp = 1000 + new Random().nextInt(9000);
        String message = "Your activating OTP number is " + otp + ". Note that this OTP is active only 5 minutes!";
        emailService.sendEmail(request.getEmail(), "Activating Account", message);

        // Store OTP + temp student data
        OtpData otpData = new OtpData();
        otpData.otp = String.valueOf(otp);
        otpData.mode = "student_register";
        otpData.expiry = System.currentTimeMillis() + 300000; // 5 minutes
        otpData.email = request.getEmail();
        otpData.password = passwordEncoder.encode(request.getPassword());
        otpData.fullname = request.getFullname();
        otpData.className = request.getClassName();
        otpData.school = request.getSchool();
        otpStore.put(request.getEmail(), otpData);

        return new ApiResponse(true, "An OTP code has been sent to your email. Note that this OTP is active for 5 minutes only!");
    }

    // --- REGISTER TEACHER (reuse logic from old AccountController.registerTeacher) ---
    public ApiResponse registerTeacher(RegisterTeacherRequest request) {
        if (teacherRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email has been existed!");
        }

        String regex = "^[a-zA-Z0-9]{8,}$";
        if (!request.getPassword().matches(regex)) {
            throw new RuntimeException("Password must contain at least 8 alphanumeric characters.");
        }
        if (!request.getPassword().equals(request.getRepassword())) {
            throw new RuntimeException("Confirm password is not the same as password!");
        }

        int otp = 1000 + new Random().nextInt(9000);
        String message = "Your activating OTP number is " + otp + ". Note that this OTP is only active for 5 minutes!";
        emailService.sendEmail(request.getEmail(), "Activating Account", message);

        OtpData otpData = new OtpData();
        otpData.otp = String.valueOf(otp);
        otpData.mode = "teacher_register";
        otpData.expiry = System.currentTimeMillis() + 300000;
        otpData.email = request.getEmail();
        otpData.password = passwordEncoder.encode(request.getPassword());
        otpData.fullname = request.getFullname();
        otpData.school = request.getSchool();
        otpStore.put(request.getEmail(), otpData);

        return new ApiResponse(true, "An OTP code has been sent to your email. Note: OTP is valid for 5 minutes only!");
    }

    // --- VERIFY OTP (reuse logic from old AccountController.verifyOTP) ---
    public ApiResponse verifyOtp(OtpVerifyRequest request) {
        OtpData otpData = otpStore.get(request.getEmail());

        if (otpData == null) {
            throw new RuntimeException("OTP session expired. Please try again.");
        }
        if (System.currentTimeMillis() > otpData.expiry) {
            otpStore.remove(request.getEmail());
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }
        if (!request.getOtp().equals(otpData.otp)) {
            throw new RuntimeException("OTP number is not correct");
        }

        switch (otpData.mode) {
            case "student_register": {
                Student student = new Student();
                student.setEmail(otpData.email);
                student.setPassword(otpData.password);
                student.setFullname(otpData.fullname);
                student.setClassName(otpData.className);
                student.setSchool(otpData.school);
                studentRepository.save(student);
                otpStore.remove(request.getEmail());
                return new ApiResponse(true, "Your account has been successfully activated. Please login.");
            }
            case "teacher_register": {
                Teacher teacher = new Teacher();
                teacher.setEmail(otpData.email);
                teacher.setPassword(otpData.password);
                teacher.setFullname(otpData.fullname);
                teacher.setSchool(otpData.school);
                teacherRepository.save(teacher);
                otpStore.remove(request.getEmail());
                return new ApiResponse(true, "Your account has been successfully activated. Please login.");
            }
            case "student_forgot":
            case "teacher_forgot": {
                // OTP verified, allow password reset (don't remove OTP yet, needed for reset)
                otpData.otpVerified = true;
                return new ApiResponse(true, "Verified successfully. Please change password.");
            }
            default:
                throw new RuntimeException("Invalid mode");
        }
    }

    // --- FORGOT PASSWORD (reuse logic from old AccountController.handleForgotPassword) ---
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        String role = request.getRole();

        boolean found = false;
        String mode = "";
        if ("teacher".equals(role) && teacherRepository.findByEmail(email).isPresent()) {
            found = true;
            mode = "teacher_forgot";
        } else if ("student".equals(role) && studentRepository.findByEmail(email).isPresent()) {
            found = true;
            mode = "student_forgot";
        }

        if (!found) {
            throw new RuntimeException("Email does not exist!");
        }

        int otp = 1000 + new Random().nextInt(9000);
        String message = "Your OTP number is " + otp + ". Note that this OTP is active in only 5 minutes!";
        emailService.sendEmail(email, "Change Account Password", message);

        OtpData otpData = new OtpData();
        otpData.otp = String.valueOf(otp);
        otpData.mode = mode;
        otpData.expiry = System.currentTimeMillis() + 300000;
        otpData.email = email;
        otpStore.put(email, otpData);

        return new ApiResponse(true, "An OTP code has been sent to your email. This code is valid for 5 minutes only.");
    }

    // --- RESET PASSWORD (reuse logic from old AccountController.changePassword) ---
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        OtpData otpData = otpStore.get(request.getEmail());
        if (otpData == null || !otpData.otpVerified) {
            throw new RuntimeException("Please verify OTP first.");
        }

        String regex = "^[a-zA-Z0-9]{8,}$";
        if (!request.getPassword().matches(regex)) {
            throw new RuntimeException("Password must contain at least 8 alphanumeric characters.");
        }
        if (!request.getPassword().equals(request.getRepassword())) {
            throw new RuntimeException("Confirm password is not the same as password!");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        int success;
        if ("student".equals(request.getRole())) {
            success = studentRepository.updatePasswordByEmail(request.getEmail(), encodedPassword);
        } else {
            success = teacherRepository.updatePasswordByEmail(request.getEmail(), encodedPassword);
        }

        if (success == 0) {
            throw new RuntimeException("Error occurred when changing your password. Please try later.");
        }

        otpStore.remove(request.getEmail());
        return new ApiResponse(true, "Your password has been changed successfully, please login!");
    }

    // --- RESEND OTP (reuse logic from old AccountController.resendOtp) ---
    public ApiResponse resendOtp(String email) {
        OtpData otpData = otpStore.get(email);
        if (otpData == null) {
            throw new RuntimeException("No OTP session found. Please register/request again.");
        }

        int otp = 1000 + new Random().nextInt(9000);
        otpData.otp = String.valueOf(otp);
        otpData.expiry = System.currentTimeMillis() + 300000;

        String message = "Your OTP number is " + otp + ". Note that this OTP is active in only 5 minutes!";
        emailService.sendEmail(email, "Resend OTP Number", message);

        return new ApiResponse(true, "An OTP code has been resent to your email.");
    }

    // Inner class to hold OTP data (replaces session attributes from old project)
    private static class OtpData {
        String otp;
        String mode;
        long expiry;
        String email;
        String password;
        String fullname;
        String className;
        String school;
        boolean otpVerified = false;
    }
}
