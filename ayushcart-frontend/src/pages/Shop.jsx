import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api } from '../lib/api.js';
import { plural } from '../lib/format.js';
import ProductCard from '../components/ProductCard.jsx';
import Pagination from '../components/Pagination.jsx';

const SORTS = [
  { value: 'createdAt,desc', label: 'Newest' },
  { value: 'price,asc', label: 'Price: low to high' },
  { value: 'price,desc', label: 'Price: high to low' },
  { value: 'name,asc', label: 'Name: A to Z' },
];

export default function Shop() {
  // Filters live in the URL, so they survive refresh and can be shared as links
  const [params, setParams] = useSearchParams();
  const q = params.get('q') ?? '';
  const categoryId = params.get('category') ?? '';
  const sort = params.get('sort') ?? 'createdAt,desc';
  const page = Number(params.get('page') ?? 0);

  const [categories, setCategories] = useState([]);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api('/categories').then(setCategories).catch(() => {});
  }, []);

  useEffect(() => {
    const query = new URLSearchParams({ page, size: 12, sort });
    if (q) query.set('search', q);
    if (categoryId) query.set('categoryId', categoryId);

    let ignore = false; // avoid showing a stale response if filters change quickly
    setError('');
    api(`/products?${query}`)
      .then((data) => !ignore && setResult(data))
      .catch((e) => !ignore && setError(e.message));
    return () => {
      ignore = true;
    };
  }, [q, categoryId, sort, page]);

  function update(changes) {
    const next = new URLSearchParams(params);
    for (const [key, value] of Object.entries(changes)) {
      if (value === '' || value == null) next.delete(key);
      else next.set(key, value);
    }
    if (!('page' in changes)) next.delete('page');
    setParams(next);
  }

  const activeCategory = categories.find((c) => String(c.id) === categoryId);

  return (
    <div className="shop">
      <aside className="shop-filters">
        <h2 className="filters-title">Categories</h2>
        <ul className="category-list">
          <li>
            <button type="button" aria-pressed={!categoryId} onClick={() => update({ category: '' })}>
              All products
            </button>
          </li>
          {categories.map((c) => (
            <li key={c.id}>
              <button
                type="button"
                aria-pressed={String(c.id) === categoryId}
                onClick={() => update({ category: c.id })}
              >
                {c.name}
              </button>
            </li>
          ))}
        </ul>
      </aside>

      <section className="shop-results" aria-live="polite">
        <div className="results-head">
          <div>
            <h1 className="shop-title">{activeCategory ? activeCategory.name : 'All products'}</h1>
            <p className="muted">
              {result
                ? `${plural(result.totalElements, 'product')}${q ? ` matching “${q}”` : ''}`
                : 'Loading products'}
            </p>
          </div>
          <label className="sort">
            Sort by
            <select value={sort} onChange={(e) => update({ sort: e.target.value })}>
              {SORTS.map((s) => (
                <option key={s.value} value={s.value}>{s.label}</option>
              ))}
            </select>
          </label>
        </div>

        {error && <p className="notice notice-error">{error}</p>}

        {result && result.content.length === 0 && (
          <div className="empty">
            <p>No products match this search. Try a shorter word or another category.</p>
            <button type="button" className="btn btn-outline" onClick={() => setParams({})}>Show all products</button>
          </div>
        )}

        {result && result.content.length > 0 && (
          <ul className="product-grid">
            {result.content.map((p) => (
              <li key={p.id}><ProductCard product={p} /></li>
            ))}
          </ul>
        )}

        {result && <Pagination page={result.page} totalPages={result.totalPages} onChange={(p) => update({ page: p })} />}
      </section>
    </div>
  );
}
