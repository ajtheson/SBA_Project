import { useState, useEffect, useRef, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../api/axios';

export default function TakeExam() {
  const { examId } = useParams();
  const navigate = useNavigate();
  const [examData, setExamData] = useState(null);
  const [answers, setAnswers] = useState({});
  const [timeLeft, setTimeLeft] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [result, setResult] = useState(null);

  const startTimeRef = useRef(null);
  const timerRef = useRef(null);
  const forceCheckRef = useRef(null);
  const submittedRef = useRef(false);

  // Submit function
  const handleSubmit = useCallback(async (isAuto = false) => {
    if (submittedRef.current) return;
    submittedRef.current = true;
    setSubmitted(true);

    // Exit fullscreen
    if (document.fullscreenElement) {
      document.exitFullscreen().catch(() => {});
    }

    // Clean up intervals
    if (timerRef.current) clearInterval(timerRef.current);
    if (forceCheckRef.current) clearInterval(forceCheckRef.current);

    // Calculate duration in seconds
    const duration = startTimeRef.current
      ? Math.round((Date.now() - startTimeRef.current) / 1000)
      : 0;

    try {
      const res = await api.post(`/api/student/exams/submit/${examData.submissionId}`, {
        duration,
        answers,
      });
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Submit failed');
      submittedRef.current = false;
      setSubmitted(false);
    }
  }, [examData, answers]);

  // Start exam
  useEffect(() => {
    const startExam = async () => {
      try {
        const res = await api.post(`/api/student/exams/${examId}/start`);
        setExamData(res.data);
        setTimeLeft(res.data.duration * 60); // Convert minutes to seconds
        startTimeRef.current = Date.now();
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to start exam');
      } finally {
        setLoading(false);
      }
    };
    startExam();
  }, [examId]);

  // Timer countdown
  useEffect(() => {
    if (!examData || submitted) return;

    timerRef.current = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          handleSubmit(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [examData, submitted, handleSubmit]);

  // Force submit check (every 2 seconds)
  useEffect(() => {
    if (!examData || submitted) return;

    forceCheckRef.current = setInterval(async () => {
      try {
        const res = await api.get(`/api/student/exams/check-submission/${examData.submissionId}`);
        if (res.data.isSubmit) {
          handleSubmit(true);
        }
      } catch {
        // Ignore errors during polling
      }
    }, 2000);

    return () => {
      if (forceCheckRef.current) clearInterval(forceCheckRef.current);
    };
  }, [examData, submitted, handleSubmit]);

  // Fullscreen + tab detection
  useEffect(() => {
    if (!examData || submitted) return;

    // Request fullscreen
    const enterFullscreen = () => {
      const elem = document.documentElement;
      if (elem.requestFullscreen) elem.requestFullscreen();
      else if (elem.webkitRequestFullscreen) elem.webkitRequestFullscreen();
      else if (elem.msRequestFullscreen) elem.msRequestFullscreen();
    };

    const handleClick = () => {
      if (!document.fullscreenElement && !submittedRef.current) {
        enterFullscreen();
      }
    };

    const handleFullscreenChange = () => {
      if (!document.fullscreenElement && !submittedRef.current) {
        alert('You exited fullscreen! Your exam will be submitted.');
        handleSubmit(true);
      }
    };

    const handleVisibilityChange = () => {
      if (document.hidden && !submittedRef.current) {
        alert('You left the page! Your exam will be submitted.');
        handleSubmit(true);
      }
    };

    const handleKeyDown = (e) => {
      if ((e.key === 'F11' || e.key === 'Escape') && !submittedRef.current) {
        e.preventDefault();
        alert('You exited fullscreen! Your exam will be submitted.');
        handleSubmit(true);
      }
    };

    // Enter fullscreen on first click
    document.addEventListener('click', handleClick, { once: true });
    document.addEventListener('fullscreenchange', handleFullscreenChange);
    document.addEventListener('visibilitychange', handleVisibilityChange);
    document.addEventListener('keydown', handleKeyDown);

    // Try to enter fullscreen immediately
    enterFullscreen();

    return () => {
      document.removeEventListener('click', handleClick);
      document.removeEventListener('fullscreenchange', handleFullscreenChange);
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [examData, submitted, handleSubmit]);

  // Handle answer selection
  const handleAnswer = (questionId, choiceId, isMultipleChoice) => {
    const qKey = String(questionId);
    if (isMultipleChoice) {
      // Toggle checkbox
      const current = answers[qKey] ? answers[qKey].split(' ') : [];
      const choiceStr = String(choiceId);
      const updated = current.includes(choiceStr)
        ? current.filter(c => c !== choiceStr)
        : [...current, choiceStr];
      setAnswers(prev => ({ ...prev, [qKey]: updated.join(' ') }));
    } else {
      // Radio
      setAnswers(prev => ({ ...prev, [qKey]: String(choiceId) }));
    }
  };

  // Format time
  const formatTime = (seconds) => {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;
    if (h > 0) return `${h}:${m < 10 ? '0' : ''}${m}:${s < 10 ? '0' : ''}${s}`;
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  // Loading state
  if (loading) return <div className="exam-taking-container"><p>Loading exam...</p></div>;
  if (error && !examData) return <div className="exam-taking-container"><div className="error-message">{error}</div></div>;

  // Result state
  if (result) {
    return (
      <div className="page-container">
        <div className="page-header">
          <h2>Exam Submitted!</h2>
        </div>
        <div className="exam-result-card">
          <h3>{examData.examName}</h3>
          <div className="stats-row">
            <div className="stat-card">
              <div className="stat-label">Score</div>
              <div className="stat-value">{result.score}/10</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Correct Answers</div>
              <div className="stat-value">{result.correctAnswers}/{result.totalQuestions}</div>
            </div>
          </div>
          <button onClick={() => navigate('/student/join')} className="btn btn-primary">
            Back to Join Exam
          </button>
        </div>
      </div>
    );
  }

  // Exam taking state
  return (
    <div className="exam-taking-container">
      <div className="exam-taking-header">
        <div className="exam-taking-title">{examData.examName}</div>
        <div className="exam-taking-warning">If you exit fullscreen or change tab, your exam will be submitted automatically.</div>
        <div className="exam-taking-timer-area">
          <span className={`exam-timer ${timeLeft <= 60 ? 'timer-warning' : ''}`}>
            {formatTime(timeLeft)}
          </span>
          <button
            onClick={() => {
              if (window.confirm('Are you sure you want to submit?')) handleSubmit(false);
            }}
            className="btn btn-primary"
            disabled={submitted}
          >
            Submit
          </button>
        </div>
      </div>

      <div className="exam-taking-body">
        {examData.questions.map((q, qIdx) => (
          <div key={q.questionId} className="exam-question-card">
            <div className="exam-question-header">
              <span className="question-number">Question {qIdx + 1}</span>
              {q.isMultipleChoice && <span className="badge">Multiple Choice</span>}
            </div>
            <p className="exam-question-content">{q.content}</p>
            <div className="exam-choices">
              {q.choices.map(c => {
                const qKey = String(q.questionId);
                const selected = answers[qKey]
                  ? answers[qKey].split(' ').includes(String(c.choiceId))
                  : false;

                return (
                  <label key={c.choiceId} className={`exam-choice-label ${selected ? 'selected' : ''}`}>
                    <input
                      type={q.isMultipleChoice ? 'checkbox' : 'radio'}
                      name={`q_${q.questionId}`}
                      checked={selected}
                      onChange={() => handleAnswer(q.questionId, c.choiceId, q.isMultipleChoice)}
                    />
                    <span>{c.choiceContent}</span>
                  </label>
                );
              })}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
