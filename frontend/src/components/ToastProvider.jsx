import React, { createContext, useContext, useState, useRef, useCallback } from 'react';

const ToastCtx = createContext(null);
export function ToastProvider({ children }) {
  const [toasts,setToasts]=useState([]);
  const idRef=useRef(0);
  const push = useCallback((msg,type='info',ttl=4000)=>{
    const id=++idRef.current;
    setToasts(t=>[...t,{id,msg,type}]);
    setTimeout(()=> setToasts(t=> t.filter(x=>x.id!==id)), ttl);
  },[]);
  return (
    <ToastCtx.Provider value={{ push }}>
      {children}
      <div className="fixed top-4 right-4 space-y-2 z-50 w-72">
        {toasts.map(t=> <div key={t.id} className={`text-sm px-3 py-2 rounded shadow border bg-white ${t.type==='error'?'border-red-300 text-red-700':t.type==='success'?'border-green-300 text-green-700':'border-gray-200 text-gray-700'}`}>{t.msg}</div>)}
      </div>
    </ToastCtx.Provider>
  );
}
export function useToast(){ return useContext(ToastCtx); }
