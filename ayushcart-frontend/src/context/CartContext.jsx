import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api } from '../lib/api.js';
import { useAuth } from './AuthContext.jsx';

const EMPTY_CART = { items: [], totalItems: 0, totalAmount: 0 };
const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { user } = useAuth();
  const [cart, setCart] = useState(EMPTY_CART);
  const [loaded, setLoaded] = useState(false);

  const refresh = useCallback(async () => {
    if (!user) {
      setCart(EMPTY_CART);
      setLoaded(false);
      return;
    }
    setCart(await api('/cart'));
    setLoaded(true);
  }, [user]);

  // Reload the cart whenever the logged-in user changes
  useEffect(() => {
    refresh().catch(() => {
      setCart(EMPTY_CART);
      setLoaded(true);
    });
  }, [refresh]);

  const value = useMemo(
    () => ({
      cart,
      loaded,
      refresh,
      addItem: async (productId, quantity = 1) =>
        setCart(await api('/cart/items', { method: 'POST', body: { productId, quantity } })),
      updateItem: async (productId, quantity) =>
        setCart(await api(`/cart/items/${productId}`, { method: 'PUT', body: { quantity } })),
      removeItem: async (productId) => setCart(await api(`/cart/items/${productId}`, { method: 'DELETE' })),
    }),
    [cart, loaded, refresh],
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  return useContext(CartContext);
}
