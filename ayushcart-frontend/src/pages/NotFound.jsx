import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="narrow">
      <h1>Page not found</h1>
      <p>The link may be broken, or the product is no longer sold.</p>
      <Link to="/" className="btn">Go to the shop</Link>
    </div>
  );
}
