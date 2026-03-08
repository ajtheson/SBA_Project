import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/axios';

export default function SubmissionDetail() {
  const { submissionId } = useParams();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const res = await api.get(`/api/student/exams/submissions/${submissionId}`);
        setDetail(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load submission detail');
      } finally {
        setLoading(false);
      }
    };
    fetchDetail();
  }, [submissionId]);

  const formatDateTime = (dt) => {
    if (!dt) return '-';
    return new Date(dt).toLocaleString('vi-VN');
  };

  const formatDuration = (seconds) => {
    if (!seconds) return '-';
    const min = Math.floor(seconds / 60);
    const sec = seconds % 60;
    return `${min}m ${sec}s`;
  };

  const getChoiceClass = (choice) => {
    if (!detail.isReview) {
      return choice.isSelected ? 'choice-selected' : '';
    }
    // Review mode: show correct/incorrect
    if (choice.isSelected && choice.isCorrectChoice) return 'choice-correct';
    if (choice.isSelected && !choice.isCorrectChoice) return 'choice-wrong';
    if (!choice.isSelected && choice.isCorrectChoice) return 'choice-missed';
    return '';
  };

  if (loading) return <div className="page-container"><p>Loading...</p></div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Submission Detail</h2>
        <Link to="/student/submissions" className="btn btn-secondary">Back to History</Link>
      </div>

      {error && <div className="error-message">{error}</div>}

      {detail && (
        <>
          {/* Summary card */}
          <div className="submission-summary">
            <div className="stats-row">
              <div className="stat-card">
                <div className="stat-label">Exam</div>
                <div className="stat-value">{detail.examName}</div>
              </div>
              <div className="stat-card">
                <div className="stat-label">Score</div>
                <div className="stat-value">{detail.score}</div>
              </div>
              <div className="stat-card">
                <div className="stat-label">Correct</div>
                <div className="stat-value">{detail.correctAnswers}/{detail.totalQuestions}</div>
              </div>
              <div className="stat-card">
                <div className="stat-label">Duration</div>
                <div className="stat-value">{formatDuration(detail.duration)}</div>
              </div>
              <div className="stat-card">
                <div className="stat-label">Submitted</div>
                <div className="stat-value" style={{ fontSize: '0.9rem' }}>{formatDateTime(detail.submitTime)}</div>
              </div>
            </div>
          </div>

          {/* Review legend */}
          {detail.isReview && (
            <div className="review-legend">
              <span className="legend-item"><span className="legend-dot correct"></span> Correct answer</span>
              <span className="legend-item"><span className="legend-dot wrong"></span> Your wrong choice</span>
              <span className="legend-item"><span className="legend-dot missed"></span> Missed correct answer</span>
            </div>
          )}

          {!detail.isReview && (
            <div className="review-notice">
              <p>Review is not enabled for this exam. You can only see which choices you selected.</p>
            </div>
          )}

          {/* Questions & Answers */}
          <div className="review-questions">
            {detail.answers.map((answer, index) => (
              <div key={answer.questionId} className={`review-question-card ${detail.isReview ? (answer.isCorrect ? 'card-correct' : 'card-wrong') : ''}`}>
                <div className="review-question-header">
                  <span className="question-number">Question {index + 1}</span>
                  {detail.isReview && (
                    <span className={`question-status ${answer.isCorrect ? 'status-correct' : 'status-wrong'}`}>
                      {answer.isCorrect ? '✓ Correct' : '✗ Incorrect'}
                    </span>
                  )}
                  {answer.isMultipleChoice && <span className="badge-multiple">Multiple Choice</span>}
                </div>
                <p className="review-question-content">{answer.questionContent}</p>
                <div className="review-choices">
                  {answer.choices.map(choice => (
                    <div key={choice.choiceId} className={`review-choice ${getChoiceClass(choice)}`}>
                      <span className="choice-indicator">
                        {choice.isSelected ? '●' : '○'}
                      </span>
                      <span className="choice-text">{choice.choiceContent}</span>
                      {detail.isReview && choice.isCorrectChoice && (
                        <span className="correct-badge">✓</span>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}
