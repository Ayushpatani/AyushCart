import { useEffect, useState } from 'react';
import { Link, NavLink, useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { useCart } from '../context/CartContext.jsx';

export default function Header() {
  const { user, isAdmin, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();
  const location = useLocation();
  const [params] = useSearchParams();
  const [query, setQuery] = useState(params.get('q') ?? '');

  // Keep the search box in sync when the shop URL changes (e.g. back button)
  useEffect(() => {
    if (location.pathname === '/') setQuery(params.get('q') ?? '');
  }, [location.pathname, params]);

  function search(e) {
    e.preventDefault();
    const q = query.trim();
    navigate(q ? `/?q=${encodeURIComponent(q)}` : '/');
  }

  function handleLogout() {
    logout();
    navigate('/');
  }

  return (
    <header className="site-header">
      <div className="header-inner">
        <Link to="/" className="wordmark">AyushCart</Link>

        <form className="search" role="search" onSubmit={search}>
          <label htmlFor="site-search" className="sr-only">Search products</label>
          <input
            id="site-search"
            type="search"
            placeholder="Search products"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <button type="submit">Search</button>
        </form>

        <nav className="header-nav" aria-label="Account">
          {isAdmin && <NavLink to="/admin">Admin</NavLink>}
          {user ? (
            <>
              <NavLink to="/orders">Orders</NavLink>
              <button type="button" className="link-button" onClick={handleLogout}>Log out</button>
            </>
          ) : (
            <NavLink to="/login">Log in</NavLink>
          )}
          <Link to="/cart" className="cart-link">
            Cart
            <span className="cart-count" aria-label={`${cart.totalItems} items in cart`}>{cart.totalItems}</span>
          </Link>
        </nav>
      </div>
    </header>
  );
}
