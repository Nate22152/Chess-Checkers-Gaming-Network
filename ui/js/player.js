// Surface address typos using the browser's local URL validation.
(() => {
  const address = document.getElementById('player-address');
  address.addEventListener('change', () => {
    if (address.validity.typeMismatch) {
      showFeedback('Enter a full server URL.', 'error');
    }
  });
})();
