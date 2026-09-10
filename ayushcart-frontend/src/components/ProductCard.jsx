import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { useCart } from '../context/CartContext.jsx';
import { formatPrice } from '../lib/format.js';
import ProductArt from './ProductArt.jsx';
import StockNote from './StockNote.jsx';

export default function ProductCard({ product }) {
  const { user } = useAuth();
  const { addItem } = useCart();
  const navigate = useNavigate();
  const [status, setStatus] = useState('idle'); // idle | adding | added
  const [error, setError] = useState('');

  async function add() {
    if (!user) {
      navigate('/login', { state: { from: { pathname: `/products/${product.id}` } } });
      return;
    }
    setStatus('adding');
    setError('');
    try {
      await addItem(product.id, 1);
      setStatus('added');
      setTimeout(() => setStatus('idle'), 1600);
    } catch (e) {
      setError(e.message);
      setStatus('idle');
    }
  }

  const soldOut = product.stock === 0;

  return (
    <article className="product-card">
      <Link to={`/products/${product.id}`} className="product-card-link">
        <ProductArt name={product.name} category={product.categoryName} imageUrl={product.imageUrl} />
        <h3 className="product-name">{product.name}</h3>
      </Link>
      <p className="product-category">{product.categoryName}</p>
      <div className="product-foot">
        <span className="price">{formatPrice(product.price)}</span>
        {!soldOut && (
          <button type="button" className="btn btn-small" onClick={add} disabled={status === 'adding'}>
            {status === 'added' ? 'Added' : 'Add to cart'}
          </button>
        )}
      </div>
      <StockNote stock={product.stock} />
      {error && <p className="field-error" role="alert">{error}</p>}
    </article>
  );
}
