import React, { useState } from 'react';
import { useToast } from '../../components/ToastProvider';

// Placeholder rule builder UI. The backend endpoints for rules aren't visible in provided code; adjust integration later.
export default function RuleBuilderPage(){
  const { push } = useToast();
  const [rules,setRules]=useState([]);
  const [form,setForm]=useState({ name:'', approvers:'', sequence:true, minPct:100 });

  const addRule=e=>{ e.preventDefault();
    const approverList = form.approvers.split(',').map(s=>s.trim()).filter(Boolean);
    setRules(r=>[...r,{ id:Date.now(), ...form, approvers:approverList }]);
    setForm({ name:'', approvers:'', sequence:true, minPct:100 });
    push('Rule added (local only)','success');
  };

  return (
    <div className="space-y-8">
      <div className="card">
        <h3 className="font-medium mb-4">Configure Approval Rule</h3>
        <form onSubmit={addRule} className="grid md:grid-cols-5 gap-4 text-sm">
          <div>
            <label className="text-xs font-medium text-gray-600">Rule Name</label>
            <input className="input mt-1" required value={form.name} onChange={e=>setForm(f=>({...f,name:e.target.value}))} />
          </div>
          <div className="md:col-span-2">
            <label className="text-xs font-medium text-gray-600">Approvers (comma separated names)</label>
            <input className="input mt-1" required value={form.approvers} onChange={e=>setForm(f=>({...f,approvers:e.target.value}))} />
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Sequential?</label>
            <select className="input mt-1" value={form.sequence? 'yes':'no'} onChange={e=>setForm(f=>({...f,sequence:e.target.value==='yes'}))}>
              <option value="yes">Yes (Sequential)</option>
              <option value="no">No (Parallel)</option>
            </select>
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Min Approval %</label>
            <input className="input mt-1" type="number" min={1} max={100} required value={form.minPct} onChange={e=>setForm(f=>({...f,minPct:e.target.value}))} />
          </div>
          <div className="md:col-span-5 flex justify-end items-end">
            <button className="btn-primary">Add Rule</button>
          </div>
        </form>
      </div>
      <div className="card">
        <h3 className="font-medium mb-4">Rules (Local Session)</h3>
        <ul className="divide-y text-sm">
          {rules.map(r=> (
            <li key={r.id} className="py-2 flex flex-col">
              <div className="flex justify-between"><span className="font-medium">{r.name}</span><span className="text-xs text-gray-500">{r.sequence?'Sequential':'Parallel'} / {r.minPct}%</span></div>
              <div className="text-xs text-gray-600 mt-1">Approvers: {r.approvers.join(', ')}</div>
            </li>
          ))}
          {!rules.length && <li className="py-6 text-center text-xs text-gray-400">No rules configured this session</li>}
        </ul>
      </div>
      <p className="text-[11px] text-gray-400">Note: Integrate with backend rule endpoints once available.</p>
    </div>
  );
}
