import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import Field from '../components/Field.jsx';

export default function Login() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname ?? '/';

  const [form, setForm] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  if (user) return <Navigate to={from} replace />;

  const set = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  async function submit(e) {
    e.preventDefault();
    setBusy(true);
    setErrors({});
    setError('');
    try {
      const u = await login(form.email, form.password);
      navigate(u.role === 'ADMIN' && from === '/' ? '/admin' : from, { replace: true });
    } catch (err) {
      setErrors(err.fieldErrors);
      setError(err.message);
      setBusy(false);
    }
  }

  return (
    <div className="auth-page">
      <h1>Log in</h1>
      <form className="form" onSubmit={submit} noValidate>
        {error && <p className="notice notice-error" role="alert">{error}</p>}
        <Field label="Email" name="email" type="email" value={form.email} onChange={set} error={errors.email} autoComplete="email" />
        <Field label="Password" name="password" type="password" value={form.password} onChange={set} error={errors.password} autoComplete="current-password" />
        <button type="submit" className="btn btn-block" disabled={busy}>{busy ? 'Logging in' : 'Log in'}</button>
      </form>
      <p>New to AyushCart? <Link to="/register" state={location.state}>Create an account</Link></p>
      <p className="demo-accounts muted">
        Demo accounts: customer@ayushcart.com / Customer@123, admin@ayushcart.com / Admin@123
      </p>
    </div>
  );
}
