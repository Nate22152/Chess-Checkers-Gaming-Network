
const GameRouter = (() => {
  //  State between Chess & Checkers
  let _selectedGame = null;   
  let _listeners    = {};     
  // once done route to the selected pages following the clicks
  const PAGES = {
    MENU:  'page-menu',
    LOBBY: 'page-lobby',
  };

  /**
   * Show a named page and hide all others.
   * @param {string} pageId
   */
  function navigate(pageId) {
    Object.values(PAGES).forEach(id => {
      const el = document.getElementById(id);
      if (el) el.classList.toggle('page--active', id === pageId);
    });
    _emit('navigate', pageId);
  }

  /**
   * Store the selected game and route to the lobby page.
   * Emits 'gameSelected' so lobby.js (and game modules) can react.
   * @param {'chess'|'checkers'} game
   */
  function selectGame(game) {
    if (!['chess', 'checkers'].includes(game)) {
      console.error(`[GameRouter] Unknown game: "${game}"`);
      return;
    }
    _selectedGame = game;
    _emit('gameSelected', game);
    navigate(PAGES.LOBBY);
  }

  /** Return to the main menu and clear the selected game. */
  function goHome() {
    _selectedGame = null;
    _emit('gameDeselected');
    navigate(PAGES.MENU);
  }

  /** @returns {'chess'|'checkers'|null} */
  function getSelectedGame() {
    return _selectedGame;
  }

  // Simple Event Bus 
  /**
   * Register a callback for a named event.
   * @param {string}   event
   * @param {Function} cb
   */
  function on(event, cb) {
    if (!_listeners[event]) _listeners[event] = [];
    _listeners[event].push(cb);
  }

  /** Remove a specific callback (or all if cb omitted). */
  function off(event, cb) {
    if (!_listeners[event]) return;
    _listeners[event] = cb
      ? _listeners[event].filter(fn => fn !== cb)
      : [];
  }

  function _emit(event, data) {
    (_listeners[event] || []).forEach(fn => {
      try { fn(data); } catch (e) { console.error('[GameRouter] event error:', e); }
    });
  }

  // Init
  /** Call once after the DOM is ready. */
  function init() {
    navigate(PAGES.MENU);
    console.log('[GameRouter] Initialized — showing main menu.');
  }

  // Public API
  return { init, navigate, selectGame, goHome, getSelectedGame, on, off, PAGES };
})();
