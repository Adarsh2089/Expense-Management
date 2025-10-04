import React from 'react';
import { Outlet, Link } from 'react-router-dom';
export default function AuthLayout(){
  return (
    <div className="min-h-screen flex items-center justify-center p-6 bg-gradient-to-br from-primary-50 to-white">
      <div className="w-full max-w-md space-y-6">
        <div className="text-center">
          <Link to="/" className="text-2xl font-bold text-primary-600">ExpenseMgmt</Link>
          <p className="mt-1 text-xs text-gray-500">Streamlined Expense Approvals</p>
        </div>
        <div className="card">
          <Outlet />
        </div>
        <p className="text-center text-[11px] text-gray-400">© {new Date().getFullYear()} ExpenseMgmt</p>
      </div>
    </div>
  );
}
