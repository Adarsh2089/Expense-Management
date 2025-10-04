import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../services/api';
import { useNavigate, Link } from 'react-router-dom';
import { useToast } from '../../components/ToastProvider';

export default function AdminSignupPage(){
  const { signup } = useAuth();
  const { push } = useToast();
  const nav = useNavigate();
  const [countries,setCountries]=useState([]);
  const [form,setForm]=useState({ fullName:'', companyName:'', email:'', password:'', country:'' });
  const [loading,setLoading]=useState(false);

  useEffect(()=> { api.countries().then(res => {
    const entries = Object.entries(res.countries||{}).map(([country,currency])=>({country,currency}));
    setCountries(entries.sort((a,b)=> a.country.localeCompare(b.country)));
  }).catch(()=>{}); },[]);

  const submit=async e=>{
    e.preventDefault(); setLoading(true);
    try { await signup(form); nav('/'); } catch { push('Signup failed','error'); }
    finally { setLoading(false); }
  };

  return (
    <>
      <h2 className="text-lg font-semibold mb-4">Admin Signup</h2>
      <form onSubmit={submit} className="space-y-4">
        <div className="grid sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-medium text-gray-600">Full Name</label>
            <input className="input mt-1" required value={form.fullName} onChange={e=>setForm(f=>({...f,fullName:e.target.value}))} />
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Company Name</label>
            <input className="input mt-1" required value={form.companyName} onChange={e=>setForm(f=>({...f,companyName:e.target.value}))} />
          </div>
        </div>
        <div className="grid sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-medium text-gray-600">Email</label>
            <input className="input mt-1" required type="email" value={form.email} onChange={e=>setForm(f=>({...f,email:e.target.value}))} />
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Password</label>
            <input className="input mt-1" required type="password" value={form.password} onChange={e=>setForm(f=>({...f,password:e.target.value}))} />
          </div>
        </div>
        <div>
          <label className="text-xs font-medium text-gray-600">Country</label>
          <select className="input mt-1" required value={form.country} onChange={e=>setForm(f=>({...f,country:e.target.value}))}>
            <option value="">{countries.length? 'Select country':'Loading...'}</option>
            {countries.map(c=> <option key={c.country} value={c.country}>{c.country} ({c.currency})</option>)}
          </select>
        </div>
        <button disabled={loading} className="btn-primary w-full">{loading?'Creating...':'Create Company & Admin'}</button>
      </form>
      <p className="mt-4 text-xs text-center text-gray-500">Have an account? <Link to="/login" className="text-primary-600 hover:underline">Login</Link></p>
    </>
  );
}
