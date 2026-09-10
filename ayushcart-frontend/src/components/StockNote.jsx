export default function StockNote({ stock }) {
  if (stock === 0) return <p className="stock stock-out">Out of stock</p>;
  if (stock <= 5) return <p className="stock stock-low">Only {stock} left</p>;
  return null;
}
