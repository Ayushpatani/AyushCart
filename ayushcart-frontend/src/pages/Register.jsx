import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import Field from '../components/Field.jsx';

export default function Register() {
  const { user, register } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname ?? '/';

  const [form, setForm] = useState({ fullName: '', email: '', password: '' });
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
      await register(form.fullName, form.email, form.password);
      navigate(from, { replace: true });
    } catch (err) {
      setErrors(err.fieldErrors);
      setError(err.message);
      setBusy(false);
    }
  }

  return (
    <div className="auth-page">
      <h1>Create an account</h1>
      <form className="form" onSubmit={submit} noValidate>
        {error && <p className="notice notice-error" role="alert">{error}</p>}
        <Field label="Full name" name="fullName" value={form.fullName} onChange={set} error={errors.fullName} autoComplete="name" />
        <Field label="Email" name="email" type="email" value={form.email} onChange={set} error={errors.email} autoComplete="email" />
        <Field label="Password" name="password" type="password" value={form.password} onChange={set}
               error={errors.password} autoComplete="new-password" hint="At least 6 characters" />
        <button type="submit" className="btn btn-block" disabled={busy}>{busy ? 'Creating account' : 'Create account'}</button>
      </form>
      <p>Already have an account? <Link to="/login" state={location.state}>Log in</Link></p>
    </div>
  );
}
