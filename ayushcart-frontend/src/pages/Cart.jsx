import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../context/CartContext.jsx';
import { formatPrice, plural } from '../lib/format.js';
import ProductArt from '../components/ProductArt.jsx';

export default function Cart() {
  const { cart, loaded, updateItem, removeItem } = useCart();
  const [error, setError] = useState('');
  const [busyId, setBusyId] = useState(null);

  async function run(productId, action) {
    setBusyId(productId);
    setError('');
    try {
      await action();
    } catch (e) {
      setError(e.message);
    } finally {
      setBusyId(null);
    }
  }

  if (!loaded) return <p className="loading">Loading</p>;

  if (cart.items.length === 0) {
    return (
      <div className="narrow">
        <h1>Your cart</h1>
        <div className="empty">
          <p>Your cart is empty.</p>
          <Link to="/" className="btn">Browse products</Link>
        </div>
      </div>
    );
  }

  const unavailable = cart.items.some((i) => !i.active || i.quantity > i.stock);

  return (
    <div className="cart-page">
      <h1>Your cart</h1>
      {error && <p className="notice notice-error" role="alert">{error}</p>}

      <div className="cart-layout">
        <ul className="cart-list">
          {cart.items.map((item) => (
            <li key={item.productId} className="cart-row">
              <ProductArt name={item.name} category={item.categoryName} imageUrl={item.imageUrl} size="thumb" />
              <div className="cart-row-main">
                <Link to={`/products/${item.productId}`} className="cart-row-name">{item.name}</Link>
                <p className="muted">{formatPrice(item.unitPrice)} each</p>
                {!item.active && <p className="stock stock-out">No longer sold. Remove it to check out.</p>}
                {item.active && item.quantity > item.stock && (
                  <p className="stock stock-out">Only {item.stock} left. Lower the quantity to check out.</p>
                )}
              </div>
              <div className="stepper" aria-label={`Quantity of ${item.name}`}>
                <button
                  type="button"
                  aria-label="Decrease quantity"
                  disabled={busyId === item.productId}
                  onClick={() => run(item.productId, () => updateItem(item.productId, item.quantity - 1))}
                >
                  −
                </button>
                <span aria-live="polite">{item.quantity}</span>
                <button
                  type="button"
                  aria-label="Increase quantity"
                  disabled={busyId === item.productId || item.quantity >= item.stock}
                  onClick={() => run(item.productId, () => updateItem(item.productId, item.quantity + 1))}
                >
                  +
                </button>
              </div>
              <p className="cart-row-total price">{formatPrice(item.lineTotal)}</p>
              <button
                type="button"
                className="link-button danger"
                onClick={() => run(item.productId, () => removeItem(item.productId))}
              >
                Remove
              </button>
            </li>
          ))}
        </ul>

        <aside className="summary">
          <h2>Summary</h2>
          <dl>
            <div><dt>Items</dt><dd>{plural(cart.totalItems, 'item')}</dd></div>
            <div><dt>Delivery</dt><dd>Free</dd></div>
            <div className="summary-total"><dt>Total</dt><dd>{formatPrice(cart.totalAmount)}</dd></div>
          </dl>
          {unavailable ? (
            <p className="muted">Fix the highlighted items to continue.</p>
          ) : (
            <Link to="/checkout" className="btn btn-block">Go to checkout</Link>
          )}
        </aside>
      </div>
    </div>
  );
}
