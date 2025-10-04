import React, { useEffect, useState, useCallback } from 'react';
import { api } from '../../services/api';
import { useToast } from '../../components/ToastProvider';
import { useInterval } from '../../hooks/useInterval.js';

export default function ApprovalsPage(){
  const { push } = useToast();
  const [rows,setRows]=useState([]);
  const [loading,setLoading]=useState(true);
  const load = useCallback(()=> api.pendingApprovals().then(r=> setRows(r)).finally(()=> setLoading(false)),[]);
  useEffect(()=> { load(); },[load]);
  useInterval(()=> { load(); }, 10000); // refresh every 10s

  const act=async (id,decision)=>{
    try { await api.decide(id, decision, ''); push('Updated','success'); load(); } catch { push('Failed','error'); }
  };

  return (
    <div className="card">
      <h3 className="font-medium mb-4">Pending Approvals</h3>
      <div className="overflow-auto">
        <table className="min-w-full text-sm">
          <thead className="table-head">
            <tr>
              <th className="text-left px-3 py-2">Step ID</th>
              <th className="text-left px-3 py-2">Expense ID</th>
              <th className="text-left px-3 py-2">Sequence</th>
              <th className="text-left px-3 py-2">Approver</th>
              <th className="text-left px-3 py-2">Decision</th>
              <th className="text-left px-3 py-2">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {rows.map(r=> (
              <tr key={r.id} className="hover:bg-gray-50">
                <td className="px-3 py-2">{r.id}</td>
                <td className="px-3 py-2">{r.expenseId}</td>
                <td className="px-3 py-2">{r.sequence}</td>
                <td className="px-3 py-2">{r.approverName}</td>
                <td className="px-3 py-2 text-xs">{r.decision}</td>
                <td className="px-3 py-2 flex gap-2">
                  <button onClick={()=>act(r.id,'APPROVED')} className="btn-primary !px-2 !py-1 text-[11px]">Approve</button>
                  <button onClick={()=>act(r.id,'REJECTED')} className="btn-secondary !px-2 !py-1 text-[11px]">Reject</button>
                </td>
              </tr>
            ))}
            {!rows.length && !loading && <tr><td colSpan={6} className="text-center py-6 text-xs text-gray-400">No pending approvals</td></tr>}
            {loading && <tr><td colSpan={6} className="text-center py-6 text-xs text-gray-400">Loading...</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}
