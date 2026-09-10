import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../../lib/api.js';
import { formatPrice } from '../../lib/format.js';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api('/admin/stats').then(setStats).catch((e) => setError(e.message));
  }, []);

  if (error) return <p className="notice notice-error">{error}</p>;
  if (!stats) return <p className="loading">Loading</p>;

  return (
    <div>
      <dl className="stats">
        <div><dt>Revenue (excluding cancelled)</dt><dd>{formatPrice(stats.revenue)}</dd></div>
        <div><dt>Orders</dt><dd>{stats.totalOrders}</dd></div>
        <div><dt>Customers</dt><dd>{stats.customers}</dd></div>
        <div><dt>Products on sale</dt><dd>{stats.activeProducts}</dd></div>
      </dl>

      <h2>Needs attention</h2>
      <ul className="attention">
        <li>
          {stats.ordersAwaitingAction === 0
            ? 'No orders are waiting to be confirmed or shipped.'
            : <><strong>{stats.ordersAwaitingAction}</strong> orders are waiting to be confirmed or shipped. <Link to="/admin/orders">Review orders</Link></>}
        </li>
        <li>
          {stats.lowStockProducts === 0
            ? 'Every product has more than 5 in stock.'
            : <><strong>{stats.lowStockProducts}</strong> products have 5 or fewer in stock. <Link to="/admin/products">Restock products</Link></>}
        </li>
      </ul>
    </div>
  );
}
