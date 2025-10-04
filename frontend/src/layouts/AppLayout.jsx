import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function AppLayout(){
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const nav = ({isActive})=> `px-3 py-2 rounded-md text-sm font-medium ${isActive?'bg-primary-600 text-white':'text-gray-600 hover:bg-gray-100'}`;
  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex h-14 items-center justify-between">
          <div className="flex items-center gap-6">
            <span onClick={()=>navigate('/')} className="text-primary-600 font-bold cursor-pointer">ExpenseMgmt</span>
            <nav className="hidden md:flex items-center gap-1">
              <NavLink to="/" className={nav} end>Dashboard</NavLink>
              <NavLink to="/submit-expense" className={nav}>Submit</NavLink>
              <NavLink to="/my-expenses" className={nav}>My Expenses</NavLink>
              {(user?.role==='MANAGER'||user?.role==='ADMIN') && <NavLink to="/approvals" className={nav}>Approvals</NavLink>}
              {user?.role==='ADMIN' && <>
                <NavLink to="/admin/users" className={nav}>Users</NavLink>
                <NavLink to="/admin/rules" className={nav}>Rules</NavLink>
              </>}
            </nav>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-600">{user?.fullName} <span className="ml-1 text-xs px-2 py-0.5 rounded bg-primary-100 text-primary-700">{user?.role}</span></span>
            <button onClick={logout} className="btn-secondary !py-1.5">Logout</button>
          </div>
        </div>
      </header>
      <main className="flex-1 bg-gray-50">
        <div className="max-w-7xl mx-auto p-4 sm:p-6">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
