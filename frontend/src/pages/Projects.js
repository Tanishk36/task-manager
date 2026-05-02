import React, { useState, useEffect } from 'react';
import { getAllProjects, createProject, updateProject, deleteProject } from '../api';
import { validators, validateForm } from '../utils/validators';

function ProjectModal({ project, onClose, onSave }) {
  const [form, setForm] = useState({ name: project?.name || '', description: project?.description || '' });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const rules = {
    name: (v) => !v?.trim() ? 'Project name is required' : v.trim().length < 3 ? 'Name must be at least 3 characters' : '',
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
    setSaving(true);
    try {
      if (project) await updateProject(project.id, form);
      else await createProject(form);
      onSave();
    } catch (err) {
      const data = err.response?.data;
      if (data?.errors) setErrors(data.errors);
    } finally { setSaving(false); }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <h2 className="modal-title">{project ? 'Edit Project' : 'Create Project'}</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Project Name *</label>
            <input className={`form-control${errors.name ? ' error' : ''}`} name="name" value={form.name} onChange={handleChange} placeholder="e.g. Website Redesign" />
            {errors.name && <div className="form-error">{errors.name}</div>}
          </div>
          <div className="form-group">
            <label className="form-label">Description</label>
            <textarea className="form-control" name="description" value={form.description} onChange={handleChange} rows={3} placeholder="Brief project description" style={{ resize: 'vertical' }} />
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : (project ? 'Update' : 'Create')}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function Projects() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null);

  const fetch = async () => {
    try { const res = await getAllProjects(); setProjects(res.data); }
    catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetch(); }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this project and all its tasks?')) return;
    try { await deleteProject(id); setProjects(ps => ps.filter(p => p.id !== id)); }
    catch { alert('Failed to delete project'); }
  };

  if (loading) return <div className="page"><div className="loading"><div className="spinner" /></div></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Projects</h1>
          <p className="page-subtitle">{projects.length} active projects</p>
        </div>
        <button className="btn btn-primary" onClick={() => setModal('create')}>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" style={{ width: 16, height: 16 }}><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          New Project
        </button>
      </div>

      {projects.length === 0 ? (
        <div className="card"><div className="empty-state"><div className="empty-state-icon">📁</div><h3>No projects yet</h3><p>Create your first project to get started</p></div></div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 }}>
          {projects.map(p => (
            <div key={p.id} className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
                <h3 style={{ fontFamily: 'var(--font-display)', fontSize: '1rem' }}>{p.name}</h3>
                <div className="actions">
                  <button className="btn btn-secondary btn-sm" onClick={() => setModal(p)}>Edit</button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(p.id)}>Delete</button>
                </div>
              </div>
              {p.description && <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: 12 }}>{p.description}</p>}
              <div style={{ display: 'flex', gap: 16, fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                <span>📋 {p.taskCount} tasks</span>
                <span>👤 {p.createdBy?.fullName}</span>
                <span>🗓 {new Date(p.createdAt).toLocaleDateString()}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {modal && (
        <ProjectModal
          project={modal === 'create' ? null : modal}
          onClose={() => setModal(null)}
          onSave={() => { setModal(null); fetch(); }}
        />
      )}
    </div>
  );
}
