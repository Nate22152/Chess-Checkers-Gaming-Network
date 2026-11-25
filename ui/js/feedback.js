// Keep errors accessible in the connection log after their toasts expire.
function showFeedback(message, type = 'info') {
  const log = document.getElementById('connection-log');
  const entry = document.createElement('li');
  entry.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
  log.prepend(entry);
  if (log.children.length > 30) log.lastElementChild.remove();

  if (type !== 'error') {
    console.info(message);
    return;
  }

  console.error(message);
  entry.className = 'connection-error';
  const toast = document.createElement('div');
  toast.className = 'toast toast--error';
  toast.textContent = message;
  document.getElementById('toast-container').appendChild(toast);
  setTimeout(() => toast.remove(), 5000);
}
