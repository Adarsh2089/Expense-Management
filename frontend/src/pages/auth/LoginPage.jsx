import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../components/ToastProvider';

export default function LoginPage(){
  const { login } = useAuth();
  const { push } = useToast();
  const nav = useNavigate();
  const [form,setForm]=useState({ email:'', password:'' });
  const [loading,setLoading]=useState(false);
  const [showForgot,setShowForgot]=useState(false);

  const submit=async e=>{
    e.preventDefault();
    setLoading(true);
    try { await login(form.email, form.password); nav('/'); } catch { push('Invalid credentials','error'); }
    finally { setLoading(false); }
  };

  return (
    <>
      <h2 className="text-lg font-semibold mb-4">Login</h2>
      <form onSubmit={submit} className="space-y-4">
        <div>
          <label className="text-xs font-medium text-gray-600">Email</label>
          <input className="input mt-1" required type="email" value={form.email} onChange={e=>setForm(f=>({...f,email:e.target.value}))} />
        </div>
        <div>
          <label className="text-xs font-medium text-gray-600">Password</label>
          <input className="input mt-1" required type="password" value={form.password} onChange={e=>setForm(f=>({...f,password:e.target.value}))} />
          <button type="button" onClick={()=>setShowForgot(s=>!s)} className="mt-1 text-[11px] text-primary-600 hover:underline">Forgot Password?</button>
          {showForgot && <p className="text-[11px] text-gray-500 mt-1">Contact your admin to reset your password.</p>}
        </div>
        <button disabled={loading} className="btn-primary w-full">{loading?'Signing in...':'Login'}</button>
      </form>
      <p className="mt-4 text-xs text-center text-gray-500">Admin onboarding? <Link to="/admin-signup" className="text-primary-600 hover:underline">Create company</Link></p>
    </>
  );
}
