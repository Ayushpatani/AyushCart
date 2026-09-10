import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../lib/api.js';
import { formatDate, formatPrice, plural } from '../lib/format.js';
import StatusBadge from '../components/StatusBadge.jsx';
import Pagination from '../components/Pagination.jsx';

export default function Orders() {
  const [page, setPage] = useState(0);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api(`/orders?page=${page}`).then(setResult).catch((e) => setError(e.message));
  }, [page]);

  if (error) return <p className="notice notice-error">{error}</p>;
  if (!result) return <p className="loading">Loading</p>;

  return (
    <div className="narrow">
      <h1>Your orders</h1>
      {result.content.length === 0 ? (
        <div className="empty">
          <p>You haven't placed any orders yet.</p>
          <Link to="/" className="btn">Start shopping</Link>
        </div>
      ) : (
        <ul className="order-list">
          {result.content.map((o) => (
            <li key={o.id}>
              <Link to={`/orders/${o.id}`} className="order-row">
                <span className="order-id">Order #{o.id}</span>
                <span className="muted">{formatDate(o.createdAt)}</span>
                <span className="muted">{plural(o.items.reduce((n, i) => n + i.quantity, 0), 'item')}</span>
                <span className="price">{formatPrice(o.totalAmount)}</span>
                <StatusBadge status={o.status} />
              </Link>
            </li>
          ))}
        </ul>
      )}
      <Pagination page={result.page} totalPages={result.totalPages} onChange={setPage} />
    </div>
  );
}
