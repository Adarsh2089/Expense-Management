import React, { useEffect, useState } from 'react';
import { api } from '../../services/api';
import { Link } from 'react-router-dom';
import dayjs from 'dayjs';

const statusColor = s => ({ DRAFT:'badge-warning', PENDING:'badge-warning', APPROVED:'badge-success', REJECTED:'badge-danger' }[s]||'badge');

export default function MyExpensesPage(){
  const [rows,setRows]=useState([]);
  const [loading,setLoading]=useState(true);
  useEffect(()=> { api.myExpenses().then(r=> setRows(r)).finally(()=>setLoading(false)); },[]);
  return (
    <div className="card">
      <h3 className="font-medium mb-4">My Expenses</h3>
      <div className="overflow-auto">
        <table className="min-w-full text-sm">
          <thead className="table-head">
            <tr>
              <th className="text-left px-3 py-2">ID</th>
              <th className="text-left px-3 py-2">Date</th>
              <th className="text-left px-3 py-2">Category</th>
              <th className="text-left px-3 py-2">Description</th>
              <th className="text-left px-3 py-2">Amount</th>
              <th className="text-left px-3 py-2">Status</th>
              <th className="text-left px-3 py-2">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {rows.map(r=> (
              <tr key={r.id} className="hover:bg-gray-50">
                <td className="px-3 py-2">{r.id}</td>
                <td className="px-3 py-2 text-xs">{r.expenseDate}</td>
                <td className="px-3 py-2">{r.category}</td>
                <td className="px-3 py-2 truncate max-w-[200px]">{r.description}</td>
                <td className="px-3 py-2 text-xs">{r.amount} {r.currency}</td>
                <td className="px-3 py-2"><span className={statusColor(r.status)}>{r.status}</span></td>
                <td className="px-3 py-2 text-xs"><Link to={`/expense/${r.id}`} className="text-primary-600 hover:underline">View</Link></td>
              </tr>
            ))}
            {!rows.length && !loading && <tr><td colSpan={7} className="text-center py-6 text-xs text-gray-400">No expenses</td></tr>}
            {loading && <tr><td colSpan={7} className="text-center py-6 text-xs text-gray-400">Loading...</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}
