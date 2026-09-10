import { useCallback, useEffect, useState } from 'react';
import { api } from '../../lib/api.js';
import { formatPrice } from '../../lib/format.js';
import Pagination from '../../components/Pagination.jsx';
import ProductForm from './ProductForm.jsx';

export default function AdminProducts() {
  const [categories, setCategories] = useState([]);
  const [result, setResult] = useState(null);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [query, setQuery] = useState('');
  const [editing, setEditing] = useState(null); // null | 'new' | product
  const [error, setError] = useState('');

  const load = useCallback(() => {
    const params = new URLSearchParams({ page, size: 20, sort: 'name,asc' });
    if (query) params.set('search', query);
    return api(`/admin/products?${params}`).then(setResult).catch((e) => setError(e.message));
  }, [page, query]);

  useEffect(() => {
    api('/categories').then(setCategories).catch((e) => setError(e.message));
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function toggleActive(product) {
    setError('');
    try {
      if (product.active) {
        await api(`/admin/products/${product.id}`, { method: 'DELETE' });
      } else {
        await api(`/admin/products/${product.id}`, {
          method: 'PUT',
          body: { ...product, active: true },
        });
      }
      load();
    } catch (e) {
      setError(e.message);
    }
  }

  if (editing) {
    return (
      <ProductForm
        product={editing === 'new' ? null : editing}
        categories={categories}
        onCancel={() => setEditing(null)}
        onSaved={() => {
          setEditing(null);
          load();
        }}
      />
    );
  }

  return (
    <div>
      <div className="toolbar">
        <form
          className="inline-search"
          role="search"
          onSubmit={(e) => {
            e.preventDefault();
            setPage(0);
            setQuery(search.trim());
          }}
        >
          <label htmlFor="admin-search" className="sr-only">Search products</label>
          <input id="admin-search" type="search" placeholder="Search by name" value={search} onChange={(e) => setSearch(e.target.value)} />
          <button type="submit" className="btn btn-outline">Search</button>
        </form>
        <button type="button" className="btn" onClick={() => setEditing('new')} disabled={categories.length === 0}>
          New product
        </button>
      </div>
      {categories.length === 0 && <p className="muted">Create a category before adding products.</p>}
      {error && <p className="notice notice-error" role="alert">{error}</p>}

      {result && (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th scope="col">Product</th><th scope="col">Category</th><th scope="col">Price</th>
                <th scope="col">Stock</th><th scope="col">In shop</th><th scope="col"><span className="sr-only">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              {result.content.map((p) => (
                <tr key={p.id} className={p.active ? '' : 'row-muted'}>
                  <td>{p.name}</td>
                  <td>{p.categoryName}</td>
                  <td className="price">{formatPrice(p.price)}</td>
                  <td className={p.stock <= 5 ? 'stock-low' : ''}>{p.stock}</td>
                  <td>{p.active ? 'Yes' : 'Hidden'}</td>
                  <td className="actions">
                    <button type="button" className="link-button" onClick={() => setEditing(p)}>Edit</button>
                    <button type="button" className="link-button" onClick={() => toggleActive(p)}>
                      {p.active ? 'Hide' : 'Show'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {result.content.length === 0 && <p className="muted">No products found.</p>}
        </div>
      )}
      {result && <Pagination page={result.page} totalPages={result.totalPages} onChange={setPage} />}
    </div>
  );
}
