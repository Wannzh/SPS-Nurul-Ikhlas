import { Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Layout } from './components/layout/Layout';

// Pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import PaymentSuccess from './pages/payment/PaymentSuccess';
import PaymentFailed from './pages/payment/PaymentFailed';
import SetupPassword from './pages/SetupPassword';
import Dashboard from './pages/admin/Dashboard';
import NotFound from './pages/NotFound';
import Unauthorized from './pages/Unauthorized';

function ProtectedRoute({ allowedRoles }) {
  const { user, loading } = useAuth();

  if (loading) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (allowedRoles && !allowedRoles.includes(user.role)) return <Navigate to="/unauthorized" replace />;

  return <Outlet />;
}

import { AdminLayout } from './components/layout/AdminLayout';

import StudentList from './pages/admin/StudentList';
import AcademicYears from './pages/admin/AcademicYears';
import BillTypes from './pages/admin/BillTypes';
import Uniforms from './pages/admin/Uniforms';
import ParentDashboard from './pages/parent/ParentDashboard';

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/" element={<Layout />}>
          {/* Public Routes */}
          <Route index element={<Home />} />
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route path="payment/success" element={<PaymentSuccess />} />
          <Route path="payment/failed" element={<PaymentFailed />} />
          <Route path="setup-password" element={<SetupPassword />} />
          <Route path="unauthorized" element={<Unauthorized />} />
          <Route path="*" element={<NotFound />} />
        </Route>

        {/* Protected Admin Routes with Sidebar Layout */}
        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<Dashboard />} />
            <Route path="students" element={<StudentList />} />
            <Route path="students" element={<StudentList />} />
            <Route path="academic-years" element={<AcademicYears />} />
            <Route path="bill-types" element={<BillTypes />} />
            <Route path="uniforms" element={<Uniforms />} />
          </Route>
        </Route>

        {/* Protected Parent Routes */}
        <Route element={<ProtectedRoute allowedRoles={['ORTU']} />}>
          <Route path="/parent/dashboard" element={<ParentDashboard />} />
        </Route>
      </Routes>
    </AuthProvider>
  );
}

export default App;
