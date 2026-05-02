import React, { useState, useEffect, useCallback } from 'react';
import { getAllTasks, getMyTasks, createTask, updateTask, deleteTask, updateTaskStatus, getAllProjects, getAllUsers } from '../api';
import { useAuth } from '../context/AuthContext';
import { validators, validateForm } from '../utils/validators';

const STATUS_OPTIONS = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];
const PRIORITY_OPTIONS = ['LOW', 'MEDIUM', 'HIGH'];

const Badge = ({ val, type }) => <span className={`badge badge-${val.toLowerCase().replace(/ /g, '_')}`}>{val.replace('_', ' ')}</span>;

function TaskModal({ task, projects, users, onClose, onSave }) {
  const tomorrow = new Date(); tomorrow.setDate(tomorrow.getDate() + 1);
  const minDate = tomorrow.toISOString().split('T')[0];

  const [form, setForm] = useState({
    title: task?.title || '',
    description: task?.description || '',
    dueDate: task?.dueDate || '',
    priority: task?.priority || 'MEDIUM',
    projectId: task?.project?.id || '',
    assignedToId: task?.assignedTo?.id || '',
  });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [apiError, setApiError] = useState('');

  const rules = {
    title: validators.required('Title'),
    projectId: validators.required('Project'),
    dueDate: validators.dueDate,
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
    setSaving(true); setApiError('');
    try {
      const payload = { ...form, projectId: Number(form.projectId), assignedToId: form.assignedToId ? Number(form.assignedToId) : null };
      if (task) await updateTask(task.id, payload);
      else await createTask(payload);
      onSave();
    } catch (err) {
      const data = err.response?.data;
      if (data?.errors) setErrors(data.errors);
      else setApiError(data?.message || 'Failed to save task');
    } finally { setSaving(false); }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <h2 className="modal-title">{task ? 'Edit Task' : 'Create New Task'}</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        {apiError && <div className="alert alert-error">{apiError}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Task Title *</label>
            <input className={`form-control${errors.title ? ' error' : ''}`} name="title" value={form.title} onChange={handleChange} placeholder="Enter task title" />
            {errors.title && <div className="form-error">{errors.title}</div>}
          </div>
          <div className="form-group">
            <label className="form-label">Description</label>
            <textarea className="form-control" name="description" value={form.description} onChange={handleChange} rows={3} placeholder="Task description" style={{ resize: 'vertical' }} />
          </div>
          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Project *</label>
              <select className={`form-control${errors.projectId ? ' error' : ''}`} name="projectId" value={form.projectId} onChange={handleChange}>
                <option value="">Select project</option>
                {projects.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
              </select>
              {errors.projectId && <div className="form-error">{errors.projectId}</div>}
            </div>
            <div className="form-group">
              <label className="form-label">Assign To</label>
              <select className="form-control" name="assignedToId" value={form.assignedToId} onChange={handleChange}>
                <option value="">Unassigned</option>
                {users.map(u => <option key={u.id} value={u.id}>{u.fullName}</option>)}
              </select>
            </div>
          </div>
          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Priority</label>
              <select className="form-control" name="priority" value={form.priority} onChange={handleChange}>
                {PRIORITY_OPTIONS.map(p => <option key={p} value={p}>{p}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Due Date *</label>
              <input className={`form-control${errors.dueDate ? ' error' : ''}`} type="date" name="dueDate" value={form.dueDate} onChange={handleChange} min={minDate} />
              {errors.dueDate && <div className="form-error">{errors.dueDate}</div>}
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : (task ? 'Update Task' : 'Create Task')}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function Tasks() {
  const { isAdmin } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null); // null | 'create' | task object
  const [filterStatus, setFilterStatus] = useState('');
  const [filterPriority, setFilterPriority] = useState('');
  const [search, setSearch] = useState('');

  const fetchTasks = useCallback(async () => {
    try {
      const res = await (isAdmin ? getAllTasks() : getMyTasks());
      setTasks(res.data);
    } catch (err) { console.error(err); }
  }, [isAdmin]);

  useEffect(() => {
    const init = async () => {
      setLoading(true);
      await fetchTasks();
      if (isAdmin) {
        const [projRes, usersRes] = await Promise.all([getAllProjects(), getAllUsers()]);
        setProjects(projRes.data);
        setUsers(usersRes.data);
      }
      setLoading(false);
    };
    init();
  }, [isAdmin, fetchTasks]);

  const handleStatusChange = async (taskId, status) => {
    try {
      await updateTaskStatus(taskId, status);
      setTasks(ts => ts.map(t => t.id === taskId ? { ...t, status } : t));
    } catch (err) { alert(err.response?.data?.message || 'Failed to update status'); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this task?')) return;
    try { await deleteTask(id); setTasks(ts => ts.filter(t => t.id !== id)); }
    catch (err) { alert('Failed to delete task'); }
  };

  const filtered = tasks.filter(t => {
    if (filterStatus && t.status !== filterStatus) return false;
    if (filterPriority && t.priority !== filterPriority) return false;
    if (search && !t.title.toLowerCase().includes(search.toLowerCase())) return false;
    return true;
  });

  if (loading) return <div className="page"><div className="loading"><div className="spinner" /></div></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Tasks</h1>
          <p className="page-subtitle">{isAdmin ? 'All team tasks' : 'Your assigned tasks'}</p>
        </div>
        {isAdmin && (
          <button className="btn btn-primary" onClick={() => setModal('create')}>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" style={{ width: 16, height: 16 }}><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
            New Task
          </button>
        )}
      </div>

      <div className="filter-bar">
        <input className="form-control" placeholder="Search tasks…" value={search} onChange={e => setSearch(e.target.value)} />
        <select className="form-control" value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
          <option value="">All Status</option>
          {STATUS_OPTIONS.map(s => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
        </select>
        <select className="form-control" value={filterPriority} onChange={e => setFilterPriority(e.target.value)}>
          <option value="">All Priority</option>
          {PRIORITY_OPTIONS.map(p => <option key={p} value={p}>{p}</option>)}
        </select>
      </div>

      {filtered.length === 0 ? (
        <div className="card"><div className="empty-state"><div className="empty-state-icon">📋</div><h3>No tasks found</h3><p>Adjust filters or create a new task</p></div></div>
      ) : (
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Title</th>
                <th>Project</th>
                {isAdmin && <th>Assigned To</th>}
                <th>Priority</th>
                <th>Due Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(task => (
                <tr key={task.id}>
                  <td>
                    <div style={{ fontWeight: 500 }}>{task.title}</div>
                    {task.description && <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: 2 }}>{task.description.slice(0, 60)}{task.description.length > 60 ? '…' : ''}</div>}
                    {task.overdue && <span className="badge badge-overdue" style={{ marginTop: 4 }}>OVERDUE</span>}
                  </td>
                  <td className="text-muted">{task.project?.name}</td>
                  {isAdmin && <td>{task.assignedTo?.fullName || <span className="text-muted">—</span>}</td>}
                  <td><span className={`badge badge-${task.priority.toLowerCase()}`}>{task.priority}</span></td>
                  <td className="text-muted">{task.dueDate}</td>
                  <td>
                    <select
                      className="form-control"
                      style={{ padding: '4px 8px', fontSize: '0.8rem', width: 'auto' }}
                      value={task.status}
                      onChange={e => handleStatusChange(task.id, e.target.value)}
                    >
                      {STATUS_OPTIONS.map(s => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
                    </select>
                  </td>
                  <td>
                    <div className="actions">
                      {isAdmin && (
                        <>
                          <button className="btn btn-secondary btn-sm" onClick={() => setModal(task)}>Edit</button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDelete(task.id)}>Delete</button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {modal && (
        <TaskModal
          task={modal === 'create' ? null : modal}
          projects={projects}
          users={users}
          onClose={() => setModal(null)}
          onSave={() => { setModal(null); fetchTasks(); }}
        />
      )}
    </div>
  );
}
