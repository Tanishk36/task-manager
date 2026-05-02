import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { signup } from '../api';
import { useAuth } from '../context/AuthContext';
import { validators, validateForm } from '../utils/validators';

export default function Signup() {
  const navigate = useNavigate();
  const { loginUser } = useAuth();
  const [form, setForm] = useState({ fullName: '', email: '', password: '', phone: '', role: 'MEMBER' });
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');
  const [loading, setLoading] = useState(false);

  const rules = {
    fullName: validators.fullName,
    email: validators.email,
    password: validators.password,
    phone: validators.phone,
  };

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
      const res = await signup(form);
      loginUser(res.data);
      navigate('/dashboard');
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data?.errors;
      setApiError(typeof msg === 'object' ? Object.values(msg).join(', ') : msg || 'Registration failed');
    } finally { setLoading(false); }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="auth-logo">
          <span style={{ width: 10, height: 10, borderRadius: '50%', background: 'var(--accent)', boxShadow: '0 0 12px var(--accent)', display: 'inline-block' }} />
          TaskFlow
        </div>
        <p className="auth-subtitle">Create your account</p>

        {apiError && <div className="alert alert-error">{apiError}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input
              className={`form-control${errors.fullName ? ' error' : ''}`}
              type="text" name="fullName" value={form.fullName}
              onChange={handleChange} placeholder="John Doe"
            />
            {errors.fullName && <div className="form-error">{errors.fullName}</div>}
          </div>
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
            <label className="form-label">Phone Number</label>
            <input
              className={`form-control${errors.phone ? ' error' : ''}`}
              type="tel" name="phone" value={form.phone}
              onChange={handleChange} placeholder="+91 9876543210"
            />
            {errors.phone && <div className="form-error">{errors.phone}</div>}
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              className={`form-control${errors.password ? ' error' : ''}`}
              type="password" name="password" value={form.password}
              onChange={handleChange} placeholder="Min 6 characters"
            />
            {errors.password && <div className="form-error">{errors.password}</div>}
          </div>
          <div className="form-group">
            <label className="form-label">Role</label>
            <select className="form-control" name="role" value={form.role} onChange={handleChange}>
              <option value="MEMBER">Member</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }} disabled={loading}>
            {loading ? 'Creating Account…' : 'Create Account'}
          </button>
        </form>

        <div className="auth-switch">
          Already have an account? <a onClick={() => navigate('/login')}>Sign in</a>
        </div>
      </div>
    </div>
  );
}
