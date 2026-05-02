import React, { useState, useEffect } from 'react';
import { getDashboardStats, getAllTasks, getMyTasks } from '../api';
import { useAuth } from '../context/AuthContext';

const Badge = ({ status }) => <span className={`badge badge-${status.toLowerCase().replace('_', '_')}`}>{status.replace('_', ' ')}</span>;

export default function Dashboard() {
  const { user, isAdmin } = useAuth();
  const [stats, setStats] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, tasksRes] = await Promise.all([
          getDashboardStats(),
          isAdmin ? getAllTasks() : getMyTasks()
        ]);
        setStats(statsRes.data);
        setTasks(tasksRes.data.slice(0, 5));
      } catch (err) { console.error(err); }
      finally { setLoading(false); }
    };
    fetchData();
  }, [isAdmin]);

  if (loading) return <div className="page"><div className="loading"><div className="spinner" /></div></div>;

  const statCards = [
    { label: 'Total Tasks', value: stats?.totalTasks ?? 0, className: '' },
    { label: 'Pending', value: stats?.pendingTasks ?? 0, className: 'warning' },
    { label: 'In Progress', value: stats?.inProgressTasks ?? 0, className: '' },
    { label: 'Completed', value: stats?.completedTasks ?? 0, className: 'success' },
    { label: 'Overdue', value: stats?.overdueTasks ?? 0, className: 'danger' },
    ...(isAdmin ? [
      { label: 'Projects', value: stats?.totalProjects ?? 0, className: '' },
      { label: 'Team Members', value: stats?.totalUsers ?? 0, className: '' },
    ] : []),
  ];

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="page-subtitle">Welcome back, {user?.fullName}</p>
        </div>
      </div>

      <div className="stat-grid">
        {statCards.map((s, i) => (
          <div key={i} className={`stat-card ${s.className}`}>
            <div className="stat-label">{s.label}</div>
            <div className={`stat-value ${s.className || 'accent'}`}>{s.value}</div>
          </div>
        ))}
      </div>

      <div className="card">
        <h2 style={{ fontFamily: 'var(--font-display)', marginBottom: 16, fontSize: '1rem' }}>
          Recent Tasks
        </h2>
        {tasks.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">📋</div>
            <h3>No tasks yet</h3>
            <p>Tasks assigned to you will appear here</p>
          </div>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Task</th>
                  <th>Project</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Due Date</th>
                </tr>
              </thead>
              <tbody>
                {tasks.map(task => (
                  <tr key={task.id}>
                    <td>
                      <div style={{ fontWeight: 500 }}>{task.title}</div>
                      {task.overdue && <span className="badge badge-overdue" style={{ marginTop: 2 }}>OVERDUE</span>}
                    </td>
                    <td className="text-muted">{task.project?.name}</td>
                    <td><span className={`badge badge-${task.priority.toLowerCase()}`}>{task.priority}</span></td>
                    <td><Badge status={task.status} /></td>
                    <td className="text-muted">{task.dueDate}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
