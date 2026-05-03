import React, { useState, useEffect } from 'react';
import { getAllUsers } from '../api';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    getAllUsers()
      .then(res => {
        // ✅ FIX: always ensure array
        setUsers(Array.isArray(res.data) ? res.data : []);
      })
      .catch(err => {
        console.error(err);
        setUsers([]); // ✅ FIX: reset on error
      })
      .finally(() => setLoading(false));
  }, []);

  // ✅ FIX: safe filter — guard against null fullName or email
  const filtered = users.filter(u =>
    (u.fullName ?? '').toLowerCase().includes(search.toLowerCase()) ||
    (u.email ?? '').toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <div className="page"><div className="loading"><div className="spinner" /></div></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Team</h1>
          <p className="page-subtitle">{users.length} members</p>
        </div>
      </div>

      <div className="filter-bar">
        <input className="form-control" placeholder="Search members…" value={search} onChange={e => setSearch(e.target.value)} />
      </div>

      {filtered.length === 0 ? (
        <div className="card"><div className="empty-state"><div className="empty-state-icon">👥</div><h3>No members found</h3><p>Try a different search</p></div></div>
      ) : (
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Member</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(u => (
                <tr key={u.id}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                      <div style={{
                        width: 32, height: 32, borderRadius: '50%', background: 'var(--accent)',
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontWeight: 700, fontSize: '0.8rem', flexShrink: 0, color: 'white'
                      }}>
                        {/* ✅ FIX: guard against null fullName */}
                        {(u.fullName ?? '?').charAt(0).toUpperCase()}
                      </div>
                      <span style={{ fontWeight: 500 }}>{u.fullName}</span>
                    </div>
                  </td>
                  <td className="text-muted">{u.email}</td>
                  <td className="text-muted">{u.phone ?? '—'}</td>
                  <td><span className={`badge badge-${(u.role ?? '').toLowerCase()}`}>{u.role}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}