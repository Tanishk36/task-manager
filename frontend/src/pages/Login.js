import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../api';
import { useAuth } from '../context/AuthContext';
import { validators, validateForm } from '../utils/validators';

export default function Login() {
  const navigate = useNavigate();
  const { loginUser } = useAuth();
  const [form, setForm] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');
  const [loading, setLoading] = useState(false);

  const rules = { email: validators.email, password: validators.required('Password') };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
    if (errors[name]) setErrors(err => ({ ...err, [name]: '' }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { errors: errs, valid } = validateForm(form, rules);
    if (!valid) { setErrors(errs); return; }
    setLoading(true); setApiError('');
    try {
      const res = await login(form);
      loginUser(res.data);
      navigate('/dashboard');
    } catch (err) {
      setApiError(err.response?.data?.message || 'Invalid email or password');
    } finally { setLoading(false); }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="auth-logo">
          <span style={{ width: 10, height: 10, borderRadius: '50%', background: 'var(--accent)', boxShadow: '0 0 12px var(--accent)', display: 'inline-block' }} />
          TaskFlow
        </div>
        <p className="auth-subtitle">Sign in to your workspace</p>

        {apiError && <div className="alert alert-error">{apiError}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              className={`form-control${errors.email ? ' error' : ''}`}
              type="email" name="email" value={form.email}
              onChange={handleChange} placeholder="you@example.com"
            />
            {errors.email && <div className="form-error">{errors.email}</div>}
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              className={`form-control${errors.password ? ' error' : ''}`}
              type="password" name="password" value={form.password}
              onChange={handleChange} placeholder="••••••"
            />
            {errors.password && <div className="form-error">{errors.password}</div>}
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }} disabled={loading}>
            {loading ? 'Signing in…' : 'Sign In'}
          </button>
        </form>

        <div className="auth-switch">
          Don't have an account? <a onClick={() => navigate('/signup')}>Sign up</a>
        </div>

        <div style={{ marginTop: 20, padding: 12, background: 'var(--surface2)', borderRadius: 8, fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          <strong style={{ color: 'var(--text)' }}>Demo credentials:</strong><br />
          Admin: admin@taskmanager.com / admin123<br />
          Member: alice@taskmanager.com / alice123
        </div>
      </div>
    </div>
  );
}
