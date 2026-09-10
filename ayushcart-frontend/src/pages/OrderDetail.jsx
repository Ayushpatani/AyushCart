import { useEffect, useState } from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';
import { api } from '../lib/api.js';
import { DELIVERY_STEPS, STATUS_LABELS, formatDate, formatPrice } from '../lib/format.js';
import NotFound from './NotFound.jsx';

export function OrderItems({ order }) {
  return (
    <table className="table">
      <thead>
        <tr><th scope="col">Product</th><th scope="col">Price</th><th scope="col">Qty</th><th scope="col">Total</th></tr>
      </thead>
      <tbody>
        {order.items.map((i) => (
          <tr key={i.productId}>
            <td>{i.productName}</td>
            <td className="price">{formatPrice(i.unitPrice)}</td>
            <td>{i.quantity}</td>
            <td className="price">{formatPrice(i.lineTotal)}</td>
          </tr>
        ))}
      </tbody>
      <tfoot>
        <tr><th scope="row" colSpan={3}>Total</th><td className="price">{formatPrice(order.totalAmount)}</td></tr>
      </tfoot>
    </table>
  );
}

export function AddressBlock({ address }) {
  return (
    <address className="address">
      {address.fullName}<br />
      {address.addressLine}<br />
      {address.city}, {address.state} {address.pincode}<br />
      {address.phone}
    </address>
  );
}

export default function OrderDetail() {
  const { id } = useParams();
  const location = useLocation();
  const [order, setOrder] = useState(null);
  const [notFound, setNotFound] = useState(false);
  const [error, setError] = useState('');
  const [cancelling, setCancelling] = useState(false);

  useEffect(() => {
    api(`/orders/${id}`)
      .then(setOrder)
      .catch((e) => (e.status === 404 ? setNotFound(true) : setError(e.message)));
  }, [id]);

  async function cancel() {
    if (!window.confirm('Cancel this order? Items go back into stock.')) return;
    setCancelling(true);
    setError('');
    try {
      setOrder(await api(`/orders/${id}/cancel`, { method: 'POST' }));
    } catch (e) {
      setError(e.message);
    } finally {
      setCancelling(false);
    }
  }

  if (notFound) return <NotFound />;
  if (!order) return error ? <p className="notice notice-error">{error}</p> : <p className="loading">Loading</p>;

  const cancelled = order.status === 'CANCELLED';
  const currentStep = DELIVERY_STEPS.indexOf(order.status);

  return (
    <div className="narrow">
      <Link to="/orders" className="back-link">All orders</Link>
      <h1>Order #{order.id}</h1>
      <p className="muted">Placed on {formatDate(order.createdAt)}</p>

      {location.state?.justPlaced && !cancelled && (
        <p className="notice notice-ok">Order placed. Its status updates here as it moves.</p>
      )}
      {error && <p className="notice notice-error" role="alert">{error}</p>}

      {cancelled ? (
        <p className="notice notice-error">This order was cancelled on {formatDate(order.updatedAt)}.</p>
      ) : (
        <ol className="tracker" aria-label="Delivery progress">
          {DELIVERY_STEPS.map((step, i) => (
            <li key={step} className={i <= currentStep ? 'done' : ''} aria-current={i === currentStep ? 'step' : undefined}>
              {STATUS_LABELS[step]}
            </li>
          ))}
        </ol>
      )}

      <section className="detail-section">
        <h2>Items</h2>
        <OrderItems order={order} />
      </section>

      <section className="detail-section">
        <h2>Delivery address</h2>
        <AddressBlock address={order.shippingAddress} />
      </section>

      {(order.status === 'PLACED' || order.status === 'CONFIRMED') && (
        <button type="button" className="btn btn-outline danger" onClick={cancel} disabled={cancelling}>
          {cancelling ? 'Cancelling' : 'Cancel order'}
        </button>
      )}
    </div>
  );
}
