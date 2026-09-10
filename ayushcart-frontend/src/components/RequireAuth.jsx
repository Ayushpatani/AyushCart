import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import NotFound from '../pages/NotFound.jsx';

/** Wraps routes that need a logged-in user (and optionally a role). */
export default function RequireAuth({ role }) {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return <p className="loading">Loading</p>;
  if (!user) return <Navigate to="/login" replace state={{ from: location }} />;
  if (role && user.role !== role) return <NotFound />;
  return <Outlet />;
}
