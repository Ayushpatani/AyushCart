const priceFormat = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  minimumFractionDigits: 0,
  maximumFractionDigits: 2,
});

export function formatPrice(value) {
  return priceFormat.format(Number(value ?? 0));
}

export function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

export const STATUS_LABELS = {
  PLACED: 'Placed',
  CONFIRMED: 'Confirmed',
  SHIPPED: 'Shipped',
  DELIVERED: 'Delivered',
  CANCELLED: 'Cancelled',
};

/** Mirrors OrderStatus.allowedNext() in the backend. */
export const NEXT_STATUSES = {
  PLACED: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['SHIPPED', 'CANCELLED'],
  SHIPPED: ['DELIVERED'],
  DELIVERED: [],
  CANCELLED: [],
};

export const DELIVERY_STEPS = ['PLACED', 'CONFIRMED', 'SHIPPED', 'DELIVERED'];

export function plural(n, word) {
  return `${n} ${n === 1 ? word : `${word}s`}`;
}
