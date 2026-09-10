import { NavLink, Outlet } from 'react-router-dom';

export default function AdminLayout() {
  return (
    <div className="admin">
      <h1>Store admin</h1>
      <nav className="tabs" aria-label="Admin sections">
        <NavLink to="/admin" end>Overview</NavLink>
        <NavLink to="/admin/orders">Orders</NavLink>
        <NavLink to="/admin/products">Products</NavLink>
        <NavLink to="/admin/categories">Categories</NavLink>
      </nav>
      <Outlet />
    </div>
  );
}
