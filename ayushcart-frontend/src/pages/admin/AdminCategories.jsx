import { useEffect, useState } from 'react';
import { api } from '../../lib/api.js';
import Field from '../../components/Field.jsx';

function CategoryRow({ category, onChanged, onError }) {
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({ name: category.name, description: category.description ?? '' });

  async function save(e) {
    e.preventDefault();
    try {
      await api(`/admin/categories/${category.id}`, { method: 'PUT', body: form });
      setEditing(false);
      onChanged();
    } catch (err) {
      onError(err.message);
    }
  }

  async function remove() {
    if (!window.confirm(`Delete the category "${category.name}"?`)) return;
    try {
      await api(`/admin/categories/${category.id}`, { method: 'DELETE' });
      onChanged();
    } catch (err) {
      onError(err.message);
    }
  }

  if (editing) {
    return (
      <li className="category-row">
        <form className="inline-form" onSubmit={save}>
          <label className="sr-only" htmlFor={`cat-name-${category.id}`}>Name</label>
          <input id={`cat-name-${category.id}`} value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <label className="sr-only" htmlFor={`cat-desc-${category.id}`}>Description</label>
          <input id={`cat-desc-${category.id}`} value={form.description} placeholder="Description"
                 onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <button type="submit" className="btn btn-small">Save</button>
          <button type="button" className="link-button" onClick={() => setEditing(false)}>Cancel</button>
        </form>
      </li>
    );
  }

  return (
    <li className="category-row">
      <div>
        <strong>{category.name}</strong>
        {category.description && <p className="muted">{category.description}</p>}
      </div>
      <div className="actions">
        <button type="button" className="link-button" onClick={() => setEditing(true)}>Edit</button>
        <button type="button" className="link-button danger" onClick={remove}>Delete</button>
      </div>
    </li>
  );
}

export default function AdminCategories() {
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({ name: '', description: '' });
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');

  const load = () => api('/categories').then(setCategories).catch((e) => setError(e.message));

  useEffect(() => {
    load();
  }, []);

  async function create(e) {
    e.preventDefault();
    setErrors({});
    setError('');
    try {
      await api('/admin/categories', { method: 'POST', body: form });
      setForm({ name: '', description: '' });
      load();
    } catch (err) {
      setErrors(err.fieldErrors);
      setError(err.message);
    }
  }

  return (
    <div className="admin-split">
      <section>
        {error && <p className="notice notice-error" role="alert">{error}</p>}
        <ul className="category-rows">
          {categories.map((c) => (
            <CategoryRow key={c.id} category={c} onChanged={load} onError={setError} />
          ))}
        </ul>
      </section>
      <form className="form panel" onSubmit={create} noValidate>
        <h2>New category</h2>
        <Field label="Name" name="name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} error={errors.name} />
        <Field label="Description" name="description" value={form.description}
               onChange={(e) => setForm({ ...form, description: e.target.value })} error={errors.description} />
        <button type="submit" className="btn">Create category</button>
        <p className="muted">A category can only be deleted once it has no products.</p>
      </form>
    </div>
  );
}
