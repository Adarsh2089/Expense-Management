import React, { useEffect, useState } from 'react';
import { api, uploadReceipt } from '../../services/api';
import { useToast } from '../../components/ToastProvider';
import dayjs from 'dayjs';

export default function SubmitExpensePage(){
  const { push } = useToast();
  const [form,setForm]=useState({ description:'', category:'', expenseDate:'', currency:'USD', amount:'', paidBy:'', notes:'', receipt:null });
  const [ocrLoading,setOcrLoading]=useState(false);
  const [rates,setRates]=useState({ base:'USD', rates:{} });
  const [errors,setErrors]=useState({});

  useEffect(()=> { api.rates('USD').then(r=> setRates({ base:r.base, rates:r.rates||{} })).catch(()=>{}); },[]);

  const onReceiptChange=async e=>{
    const file = e.target.files[0];
    if(!file) return;
    setForm(f=>({...f, receipt:file }));
    setOcrLoading(true);
    try {
      const res = await uploadReceipt(file);
      if(res.description || res.totalAmount || res.expenseDate){
        setForm(f=>({ ...f, description: res.description || f.description, amount: res.totalAmount || f.amount, expenseDate: res.expenseDate || f.expenseDate }));
        push('OCR parsed receipt','success');
      }
    } catch { push('OCR failed','error'); }
    finally { setOcrLoading(false); }
  };

  const validate=()=> {
    const e={};
    if(!form.description) e.description='Required';
    if(!form.category) e.category='Required';
    if(!form.expenseDate) e.expenseDate='Required';
    if(!form.amount) e.amount='Required';
    if(!form.receipt) e.receipt='Required';
    setErrors(e); return Object.keys(e).length===0;
  };

  const submit=async e=>{
    e.preventDefault();
    if(!validate()) { push('Please fill required fields','error'); return; }
    try {
      await api.submitExpense({ description:form.description, category:form.category, amount:Number(form.amount), currency:form.currency, expenseDate:form.expenseDate, receiptImageUrl:'uploaded-receipt-placeholder' });
      push('Expense submitted','success');
      setForm({ description:'', category:'', expenseDate:'', currency:form.currency, amount:'', paidBy:'', notes:'', receipt:null });
    } catch { push('Submit failed','error'); }
  };

  return (
    <div className="max-w-3xl">
      <div className="card">
        <h3 className="font-medium mb-4">Submit Expense</h3>
        <form onSubmit={submit} className="grid md:grid-cols-2 gap-6 text-sm">
          <div className="md:col-span-2">
            <label className="text-xs font-medium text-gray-600">Description</label>
            <input className="input mt-1" required value={form.description} onChange={e=>setForm(f=>({...f,description:e.target.value}))} />
            {errors.description && <p className="text-[10px] text-red-600 mt-0.5">{errors.description}</p>}
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Category</label>
            <input className="input mt-1" required value={form.category} onChange={e=>setForm(f=>({...f,category:e.target.value}))} />
            {errors.category && <p className="text-[10px] text-red-600 mt-0.5">{errors.category}</p>}
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Expense Date</label>
            <input className="input mt-1" required type="date" value={form.expenseDate} onChange={e=>setForm(f=>({...f,expenseDate:e.target.value}))} />
            {errors.expenseDate && <p className="text-[10px] text-red-600 mt-0.5">{errors.expenseDate}</p>}
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Currency</label>
            <select className="input mt-1" value={form.currency} onChange={e=>setForm(f=>({...f,currency:e.target.value}))}>
              <option value="USD">USD</option>
              {Object.keys(rates.rates).filter(c=>c!=='USD').slice(0,20).map(c=> <option key={c}>{c}</option>)}
            </select>
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Total Amount</label>
            <input className="input mt-1" required type="number" min={0} step="0.01" value={form.amount} onChange={e=>setForm(f=>({...f,amount:e.target.value}))} />
            {errors.amount && <p className="text-[10px] text-red-600 mt-0.5">{errors.amount}</p>}
          </div>
          <div>
            <label className="text-xs font-medium text-gray-600">Paid By</label>
            <input className="input mt-1" value={form.paidBy} onChange={e=>setForm(f=>({...f,paidBy:e.target.value}))} />
          </div>
          <div className="md:col-span-2">
            <label className="text-xs font-medium text-gray-600">Notes</label>
            <textarea className="input mt-1 h-24" value={form.notes} onChange={e=>setForm(f=>({...f,notes:e.target.value}))}></textarea>
          </div>
          <div className="md:col-span-2">
            <label className="text-xs font-medium text-gray-600">Receipt Upload</label>
            <input className="mt-1 block w-full text-xs" type="file" accept="image/*" onChange={onReceiptChange} />
            {errors.receipt && <p className="text-[10px] text-red-600 mt-0.5">{errors.receipt}</p>}
            {ocrLoading && <p className="text-[11px] text-gray-500 mt-1">Parsing receipt...</p>}
            {form.receipt && <p className="text-[11px] text-gray-500 mt-1">Selected: {form.receipt.name}</p>}
          </div>
          <div className="md:col-span-2 flex justify-end">
            <button className="btn-primary">Submit Expense</button>
          </div>
        </form>
      </div>
    </div>
  );
}
