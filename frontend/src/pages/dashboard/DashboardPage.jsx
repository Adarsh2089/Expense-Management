import React, { useEffect, useState, useCallback } from 'react';
import { api } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import Loading from '../../components/Loading';
import { Link } from 'react-router-dom';
import dayjs from 'dayjs';
import { useInterval } from '../../hooks/useInterval.js';

export default function DashboardPage(){
  const { user } = useAuth();
  const [loading,setLoading]=useState(true);
  const [stats,setStats]=useState({ myRecent:[], pendingApprovals:[], pendingCompany:0, totals:{ submitted:0, approvedAmount:0, drafts:0, categories:{} } });

  const load = useCallback(async ()=> {
    try {
      const my = await api.myExpenses();
      const submitted = my.length;
      const approved = my.filter(e=> e.status==='APPROVED');
      const drafts = my.filter(e=> e.status==='PENDING').length;
      const approvedAmount = approved.reduce((s,e)=> s + Number(e.amount),0);
      const categories = my.reduce((m,e)=> { m[e.category]=(m[e.category]||0)+Number(e.amount); return m; },{});
      let pendingApprovals = [];
      let pendingCompany = 0;
      if (user.role==='MANAGER' || user.role==='ADMIN') {
        pendingApprovals = await api.pendingApprovals().catch(()=>[]);
        const pendExp = await api.pendingCompanyExpenses().catch(()=>[]);
        pendingCompany = pendExp.length;
      }
      setStats({ myRecent: my.slice(0,5), pendingApprovals: pendingApprovals.slice(0,5), pendingCompany, totals:{ submitted, approvedAmount, drafts, categories } });
    } finally { setLoading(false); }
  },[user.role]);

  useEffect(()=> { load(); },[load]);
  useInterval(()=> { load(); }, 20000);

  if (loading) return <Loading />;

  return (
    <div className="space-y-8">
      <div className="grid md:grid-cols-3 lg:grid-cols-4 gap-6">
        <div className="card"><div className="text-xs uppercase text-gray-500">Role</div><div className="mt-2 text-lg font-semibold">{user.role}</div></div>
        <div className="card"><div className="text-xs uppercase text-gray-500">Expenses Submitted</div><div className="mt-2 text-2xl font-semibold">{stats.totals.submitted}</div></div>
        <div className="card"><div className="text-xs uppercase text-gray-500">Approved Amount</div><div className="mt-2 text-2xl font-semibold">{stats.totals.approvedAmount.toFixed(2)}</div></div>
        <div className="card"><div className="text-xs uppercase text-gray-500">Draft / Pending</div><div className="mt-2 text-2xl font-semibold">{stats.totals.drafts}</div></div>
        {(user.role==='MANAGER'||user.role==='ADMIN') && (
          <div className="card"><div className="text-xs uppercase text-gray-500">My Pending Approvals</div><div className="mt-2 text-2xl font-semibold">{stats.pendingApprovals.length}</div></div>
        )}
        {(user.role==='MANAGER'||user.role==='ADMIN') && (
          <div className="card"><div className="text-xs uppercase text-gray-500">Company Pending Expenses</div><div className="mt-2 text-2xl font-semibold">{stats.pendingCompany}</div></div>
        )}
      </div>
      <div className="grid md:grid-cols-2 gap-6">
        <div className="card">
          <div className="flex items-center justify-between mb-3">
            <h3 className="font-medium">Latest Expenses</h3>
            <Link to="/my-expenses" className="text-xs text-primary-600 hover:underline">View all</Link>
          </div>
          <ul className="divide-y text-sm">
            {stats.myRecent.map(e=> (
              <li key={e.id} className="py-2 flex justify-between items-center">
                <span className="truncate">{e.category} - {e.description}</span>
                <span className="text-gray-600 text-xs">{e.amount} {e.currency}</span>
              </li>
            ))}
            {!stats.myRecent.length && <li className="py-4 text-center text-xs text-gray-400">No expenses yet</li>}
          </ul>
        </div>
        {(user.role==='MANAGER'||user.role==='ADMIN') && (
          <div className="card">
            <div className="flex items-center justify-between mb-3">
              <h3 className="font-medium">Awaiting Your Approval</h3>
              <Link to="/approvals" className="text-xs text-primary-600 hover:underline">View all</Link>
            </div>
            <ul className="divide-y text-sm">
              {stats.pendingApprovals.map(s=> (
                <li key={s.id} className="py-2 flex justify-between items-center">
                  <span>Expense #{s.expenseId} - Seq {s.sequence}</span>
                  <span className="text-xs text-yellow-700 bg-yellow-100 px-2 py-0.5 rounded">Pending</span>
                </li>
              ))}
              {!stats.pendingApprovals.length && <li className="py-4 text-center text-xs text-gray-400">Nothing to approve</li>}
            </ul>
          </div>
        )}
      </div>
      <div className="card">
        <h3 className="font-medium mb-3">Top Categories</h3>
        <div className="grid sm:grid-cols-3 gap-4 text-xs">
          {Object.entries(stats.totals.categories).sort((a,b)=> b[1]-a[1]).slice(0,6).map(([cat,amt])=> (
            <div key={cat} className="p-3 border rounded bg-gray-50 flex flex-col">
              <span className="font-medium text-gray-700">{cat}</span>
              <span className="mt-1 text-gray-500">{amt.toFixed(2)}</span>
            </div>
          ))}
          {!Object.keys(stats.totals.categories).length && <p className="text-gray-500">No data</p>}
        </div>
      </div>
    </div>
  );
}
