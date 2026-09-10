import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { api } from '../lib/api.js';
import { formatPrice } from '../lib/format.js';
import { useAuth } from '../context/AuthContext.jsx';
import { useCart } from '../context/CartContext.jsx';
import Field from '../components/Field.jsx';

export default function Checkout() {
  const { user } = useAuth();
  const { cart, loaded, refresh } = useCart();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: user.fullName, phone: '', addressLine: '', city: '', state: '', pincode: '',
  });
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [placing, setPlacing] = useState(false);

  if (!loaded) return <p className="loading">Loading</p>;
  if (cart.items.length === 0 && !placing) return <Navigate to="/cart" replace />;

  const set = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  async function placeOrder(e) {
    e.preventDefault();
    setPlacing(true);
    setErrors({});
    setError('');
    try {
      const order = await api('/orders', { method: 'POST', body: form });
      await refresh(); // the cart is now empty
      navigate(`/orders/${order.id}`, { state: { justPlaced: true } });
    } catch (err) {
      setErrors(err.fieldErrors);
      setError(err.message);
      setPlacing(false);
    }
  }

  return (
    <div className="checkout-page">
      <h1>Checkout</h1>
      <div className="cart-layout">
        <form className="form" onSubmit={placeOrder} noValidate>
          <h2>Delivery address</h2>
          {error && <p className="notice notice-error" role="alert">{error}</p>}
          <Field label="Full name" name="fullName" value={form.fullName} onChange={set} error={errors.fullName} autoComplete="name" />
          <Field label="Mobile number" name="phone" value={form.phone} onChange={set} error={errors.phone}
                 inputMode="numeric" maxLength={10} autoComplete="tel-national" hint="10 digits, used for delivery updates" />
          <Field label="Address" name="addressLine" as="textarea" rows={3} value={form.addressLine} onChange={set}
                 error={errors.addressLine} autoComplete="street-address" />
          <div className="field-row">
            <Field label="City" name="city" value={form.city} onChange={set} error={errors.city} autoComplete="address-level2" />
            <Field label="State" name="state" value={form.state} onChange={set} error={errors.state} autoComplete="address-level1" />
          </div>
          <Field label="PIN code" name="pincode" value={form.pincode} onChange={set} error={errors.pincode}
                 inputMode="numeric" maxLength={6} autoComplete="postal-code" />
          <p className="muted">Payment is cash on delivery.</p>
          <button type="submit" className="btn btn-block" disabled={placing}>
            {placing ? 'Placing order' : `Place order · ${formatPrice(cart.totalAmount)}`}
          </button>
        </form>

        <aside className="summary">
          <h2>Your order</h2>
          <ul className="summary-items">
            {cart.items.map((i) => (
              <li key={i.productId}>
                <span>{i.name} × {i.quantity}</span>
                <span className="price">{formatPrice(i.lineTotal)}</span>
              </li>
            ))}
          </ul>
          <dl>
            <div className="summary-total"><dt>Total</dt><dd>{formatPrice(cart.totalAmount)}</dd></div>
          </dl>
          <Link to="/cart" className="back-link">Edit cart</Link>
        </aside>
      </div>
    </div>
  );
}
