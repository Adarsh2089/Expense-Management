import React, { useEffect, useState, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../../services/api';
import dayjs from 'dayjs';
import { useInterval } from '../../hooks/useInterval.js';

const badge = s => ({ PENDING:'badge-warning', APPROVED:'badge-success', REJECTED:'badge-danger' }[s]||'badge');
export default function ExpenseDetailPage(){
  const { id } = useParams();
  const [exp,setExp]=useState(null);
  const [steps,setSteps]=useState([]);
  const [loading,setLoading]=useState(true);

  const fetchData = useCallback(async()=> {
    try {
      const e = await api.expense(id); setExp(e);
      const st = await api.approvalSteps(id).catch(()=>[]); setSteps(st);
    } finally { setLoading(false); }
  },[id]);

  useEffect(()=> { fetchData(); },[fetchData]);
  useInterval(()=> { fetchData(); }, 15000);

  if(loading) return <div className="card">Loading...</div>;
  if(!exp) return <div className="card">Not found</div>;

  const statusOrder=['PENDING','APPROVED','REJECTED'];

  return (
    <div className="space-y-6">
      <div className="card">
        <h3 className="font-medium mb-4">Expense #{exp.id}</h3>
        <div className="grid sm:grid-cols-2 gap-4 text-sm">
          <div><span className="text-xs text-gray-500">Description</span><div>{exp.description}</div></div>
          <div><span className="text-xs text-gray-500">Category</span><div>{exp.category}</div></div>
          <div><span className="text-xs text-gray-500">Date</span><div>{exp.expenseDate}</div></div>
          <div><span className="text-xs text-gray-500">Amount</span><div>{exp.amount} {exp.currency}</div></div>
          <div><span className="text-xs text-gray-500">Status</span><div><span className={badge(exp.status)}>{exp.status}</span></div></div>
        </div>
      </div>
      <div className="card">
        <h3 className="font-medium mb-4">Approval Timeline</h3>
        <ol className="relative border-l border-gray-200 ml-2">
          {steps.map(s=> (
            <li key={s.id} className="ml-4 mb-6">
              <div className="absolute -left-1.5 flex items-center justify-center w-3 h-3 rounded-full border bg-white border-gray-300" />
              <div className="flex items-center gap-2">
                <span className="text-sm font-medium">Seq {s.sequence}</span>
                <span className={`text-xs px-2 py-0.5 rounded ${s.decision==='PENDING'?'bg-yellow-100 text-yellow-700': s.decision==='APPROVED'?'bg-green-100 text-green-700':'bg-red-100 text-red-700'}`}>{s.decision}</span>
              </div>
              <div className="text-xs text-gray-600 mt-1">Approver: {s.approverName}</div>
              {s.comments && <div className="text-xs text-gray-500 mt-1">Comment: {s.comments}</div>}
            </li>
          ))}
          {!steps.length && <p className="text-xs text-gray-400">No steps loaded yet</p>}
        </ol>
      </div>
    </div>
  );
}
