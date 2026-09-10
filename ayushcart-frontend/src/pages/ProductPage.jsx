import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { api } from '../lib/api.js';
import { formatPrice } from '../lib/format.js';
import { useAuth } from '../context/AuthContext.jsx';
import { useCart } from '../context/CartContext.jsx';
import ProductArt from '../components/ProductArt.jsx';
import StockNote from '../components/StockNote.jsx';
import NotFound from './NotFound.jsx';

export default function ProductPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const { cart, addItem } = useCart();
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [notFound, setNotFound] = useState(false);
  const [quantity, setQuantity] = useState(1);
  const [message, setMessage] = useState(null); // { type: 'ok' | 'error', text }
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    setProduct(null);
    setNotFound(false);
    setMessage(null);
    setQuantity(1);
    api(`/products/${id}`)
      .then(setProduct)
      .catch((e) => (e.status === 404 ? setNotFound(true) : setMessage({ type: 'error', text: e.message })));
  }, [id]);

  if (notFound) return <NotFound />;
  if (!product) return message ? <p className="notice notice-error">{message.text}</p> : <p className="loading">Loading</p>;

  const inCart = cart.items.find((i) => i.productId === product.id)?.quantity ?? 0;
  const maxQty = Math.max(0, Math.min(product.stock - inCart, 10));

  async function add() {
    if (!user) {
      navigate('/login', { state: { from: { pathname: `/products/${product.id}` } } });
      return;
    }
    setAdding(true);
    setMessage(null);
    try {
      await addItem(product.id, quantity);
      setMessage({ type: 'ok', text: `Added ${quantity} to your cart.` });
      setQuantity(1);
    } catch (e) {
      setMessage({ type: 'error', text: e.message });
    } finally {
      setAdding(false);
    }
  }

  return (
    <article className="product-page">
      <ProductArt name={product.name} category={product.categoryName} imageUrl={product.imageUrl} size="large" />

      <div className="product-info">
        <Link to={`/?category=${product.categoryId}`} className="back-link">{product.categoryName}</Link>
        <h1>{product.name}</h1>
        <p className="price price-large">{formatPrice(product.price)}</p>
        <StockNote stock={product.stock} />
        {product.description && <p className="description">{product.description}</p>}

        {product.stock > 0 && (
          <div className="buy-row">
            <label className="qty-select">
              Quantity
              <select value={quantity} onChange={(e) => setQuantity(Number(e.target.value))} disabled={maxQty === 0}>
                {Array.from({ length: Math.max(maxQty, 1) }, (_, i) => i + 1).map((n) => (
                  <option key={n} value={n}>{n}</option>
                ))}
              </select>
            </label>
            <button type="button" className="btn" onClick={add} disabled={adding || maxQty === 0}>
              {adding ? 'Adding' : 'Add to cart'}
            </button>
          </div>
        )}
        {maxQty === 0 && inCart > 0 && <p className="muted">All available stock is already in your cart.</p>}

        {message && (
          <p className={`notice ${message.type === 'ok' ? 'notice-ok' : 'notice-error'}`} role="status">
            {message.text} {message.type === 'ok' && <Link to="/cart">View cart</Link>}
          </p>
        )}
      </div>
    </article>
  );
}
