/** A labelled input with its validation error underneath. Extra props go to the input. */
export default function Field({ label, name, error, as = 'input', children, hint, ...props }) {
  const Control = as;
  const errorId = error ? `${name}-error` : undefined;
  return (
    <div className="field">
      <label htmlFor={name}>{label}</label>
      <Control id={name} name={name} aria-invalid={Boolean(error)} aria-describedby={errorId} {...props}>
        {children}
      </Control>
      {hint && !error && <p className="field-hint">{hint}</p>}
      {error && <p className="field-error" id={errorId}>{error}</p>}
    </div>
  );
}
