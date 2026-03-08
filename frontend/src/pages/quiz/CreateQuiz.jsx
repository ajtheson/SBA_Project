import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axios';

function generateId() {
  return Math.random().toString(36).substr(2, 9);
}

export default function CreateQuiz() {
  const [quizName, setQuizName] = useState('');
  const [questions, setQuestions] = useState([
    { id: generateId(), content: '', choices: [
      { id: generateId(), choiceContent: '', isCorrectChoice: false },
      { id: generateId(), choiceContent: '', isCorrectChoice: false },
    ]}
  ]);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const addQuestion = () => {
    setQuestions([...questions, {
      id: generateId(), content: '', choices: [
        { id: generateId(), choiceContent: '', isCorrectChoice: false },
        { id: generateId(), choiceContent: '', isCorrectChoice: false },
      ]
    }]);
  };

  const removeQuestion = (qId) => {
    if (questions.length <= 1) return;
    setQuestions(questions.filter(q => q.id !== qId));
  };

  const updateQuestion = (qId, content) => {
    setQuestions(questions.map(q => q.id === qId ? { ...q, content } : q));
  };

  const addChoice = (qId) => {
    setQuestions(questions.map(q => q.id === qId ? {
      ...q, choices: [...q.choices, { id: generateId(), choiceContent: '', isCorrectChoice: false }]
    } : q));
  };

  const removeChoice = (qId, cId) => {
    setQuestions(questions.map(q => q.id === qId ? {
      ...q, choices: q.choices.filter(c => c.id !== cId)
    } : q));
  };

  const updateChoice = (qId, cId, field, value) => {
    setQuestions(questions.map(q => q.id === qId ? {
      ...q, choices: q.choices.map(c => c.id === cId ? { ...c, [field]: value } : c)
    } : q));
  };

  const validate = () => {
    if (!quizName.trim()) return 'Quiz name is required';
    if (questions.length === 0) return 'At least 1 question is required';
    for (let i = 0; i < questions.length; i++) {
      if (!questions[i].content.trim()) return `Question ${i + 1} content is empty`;
      if (questions[i].choices.length < 2) return `Question ${i + 1} needs at least 2 choices`;
      const hasCorrect = questions[i].choices.some(c => c.isCorrectChoice);
      if (!hasCorrect) return `Question ${i + 1} needs at least 1 correct answer`;
      for (let j = 0; j < questions[i].choices.length; j++) {
        if (!questions[i].choices[j].choiceContent.trim()) return `Question ${i + 1}, Choice ${j + 1} is empty`;
      }
    }
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const err = validate();
    if (err) { setError(err); return; }
    setError('');
    setSubmitting(true);

    try {
      const payload = {
        quizName,
        questions: questions.map(q => ({
          content: q.content,
          choices: q.choices.map(c => ({
            choiceContent: c.choiceContent,
            isCorrectChoice: c.isCorrectChoice,
          }))
        }))
      };
      await api.post('/api/teacher/quizzes', payload);
      navigate('/teacher/quizzes');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create quiz');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Create Quiz</h2>
        <Link to="/teacher/quizzes" className="btn btn-secondary">Back to List</Link>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Quiz Name</label>
          <input type="text" value={quizName} onChange={(e) => setQuizName(e.target.value)} placeholder="Enter quiz name" />
        </div>

        <div className="questions-container">
          {questions.map((q, qIdx) => (
            <div key={q.id} className="question-card">
              <div className="question-header">
                <span className="question-number">Question {qIdx + 1}</span>
                {questions.length > 1 && (
                  <button type="button" onClick={() => removeQuestion(q.id)} className="btn btn-sm btn-danger">Remove</button>
                )}
              </div>
              <div className="form-group">
                <input type="text" value={q.content} onChange={(e) => updateQuestion(q.id, e.target.value)} placeholder="Enter question content" />
              </div>

              <div className="choices-container">
                {q.choices.map((c, cIdx) => (
                  <div key={c.id} className="choice-row">
                    <label className="checkbox-label">
                      <input
                        type="checkbox"
                        checked={c.isCorrectChoice}
                        onChange={(e) => updateChoice(q.id, c.id, 'isCorrectChoice', e.target.checked)}
                      />
                    </label>
                    <input
                      type="text"
                      value={c.choiceContent}
                      onChange={(e) => updateChoice(q.id, c.id, 'choiceContent', e.target.value)}
                      placeholder={`Choice ${cIdx + 1}`}
                      className="choice-input"
                    />
                    {q.choices.length > 2 && (
                      <button type="button" onClick={() => removeChoice(q.id, c.id)} className="btn btn-sm btn-danger">×</button>
                    )}
                  </div>
                ))}
                <button type="button" onClick={() => addChoice(q.id)} className="btn btn-sm btn-secondary">+ Add Choice</button>
              </div>
            </div>
          ))}
        </div>

        <div className="form-actions">
          <button type="button" onClick={addQuestion} className="btn btn-secondary">+ Add Question</button>
          <button type="submit" disabled={submitting} className="btn btn-primary">
            {submitting ? 'Creating...' : 'Create Quiz'}
          </button>
        </div>
      </form>
    </div>
  );
}
