type ToastProps = {
  message: string | null;
  tone?: 'success' | 'error' | 'info';
  onClose?: () => void;
};

export function Toast({ message, tone = 'info', onClose }: ToastProps) {
  if (!message) {
    return null;
  }

  return (
    <div className={`toast toast--${tone}`} role={tone === 'error' ? 'alert' : 'status'}>
      <span>{message}</span>
      {onClose ? (
        <button type="button" onClick={onClose} aria-label="Fechar aviso">
          x
        </button>
      ) : null}
    </div>
  );
}
