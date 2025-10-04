import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext.jsx';
import { ToastProvider } from './components/ToastProvider.jsx';
import AuthLayout from './layouts/AuthLayout.jsx';
import AppLayout from './layouts/AppLayout.jsx';
import LoginPage from './pages/auth/LoginPage.jsx';
import AdminSignupPage from './pages/auth/AdminSignupPage.jsx';
import DashboardPage from './pages/dashboard/DashboardPage.jsx';
import UsersPage from './pages/admin/UsersPage.jsx';
import RuleBuilderPage from './pages/admin/RuleBuilderPage.jsx';
import SubmitExpensePage from './pages/expense/SubmitExpensePage.jsx';
import MyExpensesPage from './pages/expense/MyExpensesPage.jsx';
import ApprovalsPage from './pages/manager/ApprovalsPage.jsx';
import ExpenseDetailPage from './pages/expense/ExpenseDetailPage.jsx';

function PrivateRoute({ children, roles }) {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />;
  return children;
}

export default function App(){
  return (
    <ToastProvider>
      <AuthProvider>
        <Routes>
          <Route element={<AuthLayout />}> 
            <Route path="/login" element={<LoginPage />} />
            <Route path="/admin-signup" element={<AdminSignupPage />} />
          </Route>
          <Route element={<PrivateRoute><AppLayout /></PrivateRoute>}>
            <Route index element={<DashboardPage />} />
            <Route path="submit-expense" element={<SubmitExpensePage />} />
            <Route path="my-expenses" element={<MyExpensesPage />} />
            <Route path="expense/:id" element={<ExpenseDetailPage />} />
            <Route path="approvals" element={<PrivateRoute roles={['MANAGER','ADMIN']}><ApprovalsPage /></PrivateRoute>} />
            <Route path="admin/users" element={<PrivateRoute roles={['ADMIN']}><UsersPage /></PrivateRoute>} />
            <Route path="admin/rules" element={<PrivateRoute roles={['ADMIN']}><RuleBuilderPage /></PrivateRoute>} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </ToastProvider>
  );
}
