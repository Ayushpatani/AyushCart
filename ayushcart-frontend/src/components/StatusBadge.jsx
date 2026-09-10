import { STATUS_LABELS } from '../lib/format.js';

export default function StatusBadge({ status }) {
  return <span className={`status status-${status.toLowerCase()}`}>{STATUS_LABELS[status]}</span>;
}
