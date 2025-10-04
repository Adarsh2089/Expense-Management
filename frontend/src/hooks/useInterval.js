import { useEffect, useRef } from 'react';
export function useInterval(callback, delay){
  const saved = useRef();
  useEffect(()=> { saved.current = callback; },[callback]);
  useEffect(()=> {
    if (delay == null) return;
    const id = setInterval(()=> saved.current && saved.current(), delay);
    return ()=> clearInterval(id);
  },[delay]);
}
