import React, { useEffect, useState } from 'react';
import { api } from '../../services/api';
import { useToast } from '../../components/ToastProvider';
import { useAuth } from '../../context/AuthContext';

const roleOptions = ['ADMIN','MANAGER','EMPLOYEE'];

export default function UsersPage(){
  const { push } = useToast();
  const { user } = useAuth();
  const [users,setUsers]=useState([]);
  const [managers,setManagers]=useState([]);
  const [form,setForm]=useState({ fullName:'', email:'', password:'', role:'EMPLOYEE', managerId:'' });
  const [loading,setLoading]=useState(false);
  const [creating,setCreating]=useState(false);
  const [initialLoaded,setInitialLoaded]=useState(false);

  const load=async()=> {
    setLoading(true);
    try {
      const u = await api.listUsers();
      console.debug('[UsersPage] listUsers response', u);
      setUsers(u);
      const m = await api.listManagers().catch(()=>[]);
      console.debug('[UsersPage] listManagers response', m);
      setManagers(m);
      setInitialLoaded(true);
    } catch (e) { console.error('[UsersPage] load error', e); push('Failed to load users','error'); }
    finally { setLoading(false); }
  };
  useEffect(()=> { if(user) load(); },[user]);

  const submit=async e=>{
    e.preventDefault(); setCreating(true);
    try {
      await api.createUser({ ...form, companyId: user.companyId, managerId: form.role==='EMPLOYEE' && form.managerId? Number(form.managerId): null });
      push('User created','success');
      setForm({ fullName:'', email:'', password:'', role:'EMPLOYEE', managerId:'' });
      load();
    } catch { push('Failed','error'); }
    finally { setCreating(false); }
  };

  return (
    <div className="space-y-8">
      <div className="card">
        <h3 className="font-medium mb-4">Create User</h3>
        <form onSubmit={submit} className="grid md:grid-cols-6 gap-4 text-sm">
          <div className="md:col-span-2">
            <label className="text-xs font-medium text-gray-600">Full Name</label>
            <input className="input mt-1" required value={form.fullName} onChange={e=>setForm(f=>({...f,fullName:e.target.value}))} />
          </div>
          <div className="md:col-span-2">
            <label className="text-xs font-medium text-gray-600">Email</label>
            <input className="input mt-1" type="email" required value={form.email} onChange={e=>setForm(f=>({...f,email:e.target.value}))} />
          </div>
          <div className="md:col-span-2">
            <label className="text-xs font-medium text-gray-600">Password</label>
            <input className="input mt-1" type="text" required value={form.password} onChange={e=>setForm(f=>({...f,password:e.target.value}))} />
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Role</label>
            <select className="input mt-1" value={form.role} onChange={e=>setForm(f=>({...f,role:e.target.value, managerId:''}))}>
              {roleOptions.map(r=> <option key={r}>{r}</option>)}
            </select>
          </div>
          {form.role==='EMPLOYEE' && (
            <div>
              <label className="text-xs font-medium text-gray-600">Manager</label>
              <select className="input mt-1" value={form.managerId} onChange={e=>setForm(f=>({...f,managerId:e.target.value}))}>
                <option value="">Select manager</option>
                {managers.map(m=> <option key={m.id} value={m.id}>{m.fullName}</option>)}
              </select>
            </div>
          )}
          <div className="md:col-span-6 flex justify-end items-end">
            <button disabled={creating} className="btn-primary">{creating?'Creating...':'Create User'}</button>
          </div>
        </form>
      </div>

      <div className="card">
        <h3 className="font-medium mb-4">Users</h3>
        <div className="overflow-auto">
          <table className="min-w-full text-sm">
            <thead className="table-head">
              <tr>
                <th className="text-left px-3 py-2">Name</th>
                <th className="text-left px-3 py-2">Role</th>
                <th className="text-left px-3 py-2">Managed By</th>
                <th className="text-left px-3 py-2">Email</th>
                <th className="text-left px-3 py-2">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {users.map(u=> (
                <tr key={u.id} className="hover:bg-gray-50">
                  <td className="px-3 py-2">{u.fullName}</td>
                  <td className="px-3 py-2"><span className="text-xs px-2 py-0.5 rounded bg-primary-100 text-primary-700">{u.role}</span></td>
                  <td className="px-3 py-2 text-xs text-gray-600">{u.managerName||'-'}</td>
                  <td className="px-3 py-2 text-xs">{u.email}</td>
                  <td className="px-3 py-2 text-xs"><button className="btn-secondary !px-2 !py-1 text-[11px]">Send Password</button></td>
                </tr>
              ))}
              {!loading && initialLoaded && !users.length && <tr><td colSpan={5} className="text-center py-6 text-xs text-gray-400">No users</td></tr>}
              {loading && <tr><td colSpan={5} className="text-center py-6 text-xs text-gray-400">Loading...</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
