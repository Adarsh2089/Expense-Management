const ENV_BASE = typeof import.meta !== 'undefined' ? import.meta.env?.VITE_API_BASE_URL : null;
const BASE_URL = (ENV_BASE || 'http://localhost:8080') + '/api';
let token = null;
let onUnauthorized = () => {};
export function setAuthToken(t){ token = t; }
export function setOnUnauthorized(cb){ onUnauthorized = cb; }

async function request(path, { method='GET', body, headers } = {}) {
  const h = { 'ngrok-skip-browser-warning':'true', ...(body instanceof FormData ? {} : { 'Content-Type':'application/json' }), ...(headers||{}) };
  if (token) h['Authorization'] = `Bearer ${token}`;
  const res = await fetch(BASE_URL + path, { method, headers: h, body: body instanceof FormData ? body : body ? JSON.stringify(body): undefined });
  if (res.status === 401) { onUnauthorized(); }
  if (!res.ok) { throw new Error('API ' + res.status); }
  if (res.status === 204) return null;
  const ct = res.headers.get('content-type')||'';
  if (!ct.includes('application/json')) return res.text();
  return res.json();
}

export const api = {
  get: (p)=>request(p),
  post: (p,b)=>request(p,{method:'POST',body:b}),
  put: (p,b)=>request(p,{method:'PUT',body:b}),
  delete:(p)=>request(p,{method:'DELETE'}),
  // domain helpers
  login:(email,password)=>api.post('/auth/login',{email,password}),
  signup:(payload)=>api.post('/auth/signup',payload),
  me:(authToken)=> { if(authToken) setAuthToken(authToken); return api.get('/users/me'); },
  createUser:(payload)=>api.post('/users',payload), // backend expects POST /api/users
  listUsers:(companyId)=> api.get(companyId? `/users?companyId=${companyId}`: '/users'),
  listManagers:(companyId)=> api.get(companyId? `/users/role/MANAGER?companyId=${companyId}`:'/users/role/MANAGER'),
  submitExpense:(payload)=>api.post('/expenses',payload),
  myExpenses:()=>api.get('/expenses/my'),
  myExpensesByStatus:(s)=>api.get(`/expenses/my/status/${s}`),
  expense:(id)=>api.get(`/expenses/${id}`),
  pendingApprovals:()=>api.get('/approvals/pending'),
  approvalSteps:(expenseId)=>api.get(`/approvals/expense/${expenseId}`),
  decide:(stepId,decision,comments)=>api.put(`/approvals/${stepId}`,{decision,comments}),
  pendingCompanyExpenses:()=>api.get('/expenses/pending'),
  countries:()=>api.get('/integration/countries'),
  rates:(base)=>api.get(`/integration/currency-rates/${base}`)
};

export async function uploadReceipt(file){
  const fd = new FormData(); fd.append('file', file);
  const h={ 'ngrok-skip-browser-warning':'true' };
  if(token) h['Authorization']=`Bearer ${token}`;
  const res = await fetch(BASE_URL + '/ocr/parse-receipt',{method:'POST', headers:h, body:fd});
  if(!res.ok) throw new Error('Upload failed');
  return res.json();
}
