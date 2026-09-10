import { useState } from 'react';
import { api } from '../../lib/api.js';
import Field from '../../components/Field.jsx';

const EMPTY = { name: '', description: '', price: '', stock: '', imageUrl: '', categoryId: '', active: true };

export default function ProductForm({ product, categories, onSaved, onCancel }) {
  const [form, setForm] = useState(() =>
    product
      ? { ...product, imageUrl: product.imageUrl ?? '', description: product.description ?? '' }
      : { ...EMPTY, categoryId: categories[0]?.id ?? '' },
  );
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const set = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  };

  async function save(e) {
    e.preventDefault();
    setSaving(true);
    setErrors({});
    setError('');
    const body = {
      name: form.name,
      description: form.description,
      price: form.price === '' ? null : Number(form.price),
      stock: form.stock === '' ? null : Number(form.stock),
      imageUrl: form.imageUrl,
      categoryId: form.categoryId === '' ? null : Number(form.categoryId),
      active: form.active,
    };
    try {
      const saved = product
        ? await api(`/admin/products/${product.id}`, { method: 'PUT', body })
        : await api('/admin/products', { method: 'POST', body });
      onSaved(saved);
    } catch (err) {
      setErrors(err.fieldErrors);
      setError(err.message);
      setSaving(false);
    }
  }

  return (
    <form className="form panel" onSubmit={save} noValidate>
      <h2>{product ? `Edit ${product.name}` : 'New product'}</h2>
      {error && <p className="notice notice-error" role="alert">{error}</p>}
      <Field label="Name" name="name" value={form.name} onChange={set} error={errors.name} />
      <div className="field-row">
        <Field label="Price (₹)" name="price" type="number" min="0" step="0.01" value={form.price} onChange={set} error={errors.price} />
        <Field label="Stock" name="stock" type="number" min="0" step="1" value={form.stock} onChange={set} error={errors.stock} />
        <Field label="Category" name="categoryId" as="select" value={form.categoryId} onChange={set} error={errors.categoryId}>
          {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
        </Field>
      </div>
      <Field label="Image URL" name="imageUrl" type="url" value={form.imageUrl} onChange={set} error={errors.imageUrl}
             hint="Optional. Without one, the product gets a colour tile." />
      <Field label="Description" name="description" as="textarea" rows={4} value={form.description} onChange={set} error={errors.description} />
      <label className="checkbox">
        <input type="checkbox" name="active" checked={form.active} onChange={set} />
        Show in shop
      </label>
      <div className="form-actions">
        <button type="submit" className="btn" disabled={saving}>{saving ? 'Saving' : product ? 'Save changes' : 'Create product'}</button>
        <button type="button" className="btn btn-quiet" onClick={onCancel}>Cancel</button>
      </div>
    </form>
  );
}
