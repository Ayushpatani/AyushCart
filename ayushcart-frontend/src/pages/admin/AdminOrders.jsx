import { Fragment, useCallback, useEffect, useState } from 'react';
import { api } from '../../lib/api.js';
import { NEXT_STATUSES, STATUS_LABELS, formatDate, formatPrice } from '../../lib/format.js';
import StatusBadge from '../../components/StatusBadge.jsx';
import Pagination from '../../components/Pagination.jsx';
import { AddressBlock, OrderItems } from '../OrderDetail.jsx';

function StatusControl({ order, onUpdated, onError }) {
  const options = NEXT_STATUSES[order.status];
  const [next, setNext] = useState(options[0] ?? '');
  const [saving, setSaving] = useState(false);

  useEffect(() => setNext(NEXT_STATUSES[order.status][0] ?? ''), [order.status]);

  if (options.length === 0) return <span className="muted">Final</span>;

  async function update() {
    if (next === 'CANCELLED' && !window.confirm(`Cancel order #${order.id}? Items go back into stock.`)) return;
    setSaving(true);
    try {
      onUpdated(await api(`/admin/orders/${order.id}/status`, { method: 'PATCH', body: { status: next } }));
    } catch (e) {
      onError(e.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="status-control">
      <label className="sr-only" htmlFor={`status-${order.id}`}>New status for order {order.id}</label>
      <select id={`status-${order.id}`} value={next} onChange={(e) => setNext(e.target.value)}>
        {options.map((s) => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
      </select>
      <button type="button" className="btn btn-small" onClick={update} disabled={saving}>Update</button>
    </div>
  );
}

export default function AdminOrders() {
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [result, setResult] = useState(null);
  const [expanded, setExpanded] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(() => {
    const params = new URLSearchParams({ page, size: 20 });
    if (status) params.set('status', status);
    api(`/admin/orders?${params}`).then(setResult).catch((e) => setError(e.message));
  }, [page, status]);

  useEffect(() => {
    load();
  }, [load]);

  function replaceOrder(updated) {
    setError('');
    setResult((r) => ({ ...r, content: r.content.map((o) => (o.id === updated.id ? updated : o)) }));
  }

  return (
    <div>
      <div className="toolbar">
        <label className="sort">
          Show
          <select value={status} onChange={(e) => { setPage(0); setStatus(e.target.value); }}>
            <option value="">All orders</option>
            {Object.entries(STATUS_LABELS).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
          </select>
        </label>
      </div>
      {error && <p className="notice notice-error" role="alert">{error}</p>}

      {result && (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th scope="col">Order</th><th scope="col">Customer</th><th scope="col">Date</th>
                <th scope="col">Total</th><th scope="col">Status</th><th scope="col">Change status</th>
              </tr>
            </thead>
            <tbody>
              {result.content.map((o) => (
                <Fragment key={o.id}>
                  <tr>
                    <td>
                      <button type="button" className="link-button" aria-expanded={expanded === o.id}
                              onClick={() => setExpanded(expanded === o.id ? null : o.id)}>
                        #{o.id}
                      </button>
                    </td>
                    <td>{o.customerName}<br /><span className="muted">{o.customerEmail}</span></td>
                    <td>{formatDate(o.createdAt)}</td>
                    <td className="price">{formatPrice(o.totalAmount)}</td>
                    <td><StatusBadge status={o.status} /></td>
                    <td><StatusControl order={o} onUpdated={replaceOrder} onError={setError} /></td>
                  </tr>
                  {expanded === o.id && (
                    <tr className="expanded-row">
                      <td colSpan={6}>
                        <div className="admin-split">
                          <OrderItems order={o} />
                          <AddressBlock address={o.shippingAddress} />
                        </div>
                      </td>
                    </tr>
                  )}
                </Fragment>
              ))}
            </tbody>
          </table>
          {result.content.length === 0 && <p className="muted">No orders here yet.</p>}
        </div>
      )}
      {result && <Pagination page={result.page} totalPages={result.totalPages} onChange={setPage} />}
    </div>
  );
}
