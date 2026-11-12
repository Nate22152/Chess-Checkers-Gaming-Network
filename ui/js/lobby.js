const LobbyController = (() => {

  // Player Status
  const state = {
    player1: { connected: false, name: 'Player 1' },
    player2: { connected: false, name: 'Player 2' },
    //Provide game type ('chess' | 'checkers')
    game:    null,   
    pollingInterval: null,
  };

 
  const $ = id => document.getElementById(id);

  const GAME_META = {
    chess: {
      label:        'Chess',
      icon:         '♟',
      p1Role:       'White Pieces',
      p2Role:       'Black Pieces',
      p1AvatarIcon: '♔',
      p2AvatarIcon: '♚',
      headerClass:  'lobby-header--chess',
      avatarClass:  'player-avatar--chess',
    },
    checkers: {
      label:        'Checkers',
      icon:         '⬤',
      p1Role:       'Red Pieces',
      p2Role:       'Navy Pieces',
      p1AvatarIcon: '🔴',
      p2AvatarIcon: '🔵',
      headerClass:  'lobby-header--checkers',
      avatarClass:  'player-avatar--checkers',
    },
  };

  // Render Helpers

  /**
   * Update a single player slot's DOM to reflect connection status.
   * @param {1|2} playerNum
   * @param {boolean} connected
   */
  function _renderPlayerSlot(playerNum, connected) {
    const slot   = $(`slot-p${playerNum}`);
    const dot    = $(`dot-p${playerNum}`);
    const label  = $(`status-label-p${playerNum}`);

    if (!slot) return;

    // Swap slot border/background class
    slot.classList.toggle('player-slot--connected', connected);
    slot.classList.toggle('player-slot--waiting',  !connected);

    // Swap dot colour class
    if (dot) {
      dot.className = `status-dot status-dot--${connected ? 'connected' : 'waiting'}`;
    }

    // Update status text
    if (label) {
      label.textContent = connected ? 'Connected' : 'Waiting…';
      label.className   = `status-label status-label--${connected ? 'connected' : 'waiting'}`;
    }
  }

  /** Apply game-specific labels, icons, and avatar colours. */
  function _applyGameTheme(game) {
    const meta = GAME_META[game];
    if (!meta) return;

    // Header
    const header = $('lobby-game-name');
    if (header) {
      header.textContent = meta.label;
      // Remove old accent classes, add the right one
      header.classList.remove('lobby-header--chess', 'lobby-header--checkers');
      header.classList.add(meta.headerClass);
    }

    // Player roles
    const r1 = $('role-p1');
    const r2 = $('role-p2');
    if (r1) r1.textContent = meta.p1Role;
    if (r2) r2.textContent = meta.p2Role;

    // Avatars
    ['p1', 'p2'].forEach((p, i) => {
      const av = $(`avatar-${p}`);
      if (!av) return;
      av.textContent = i === 0 ? meta.p1AvatarIcon : meta.p2AvatarIcon;
      av.className   = `player-avatar ${meta.avatarClass}`;
    });
  }

  /** Sync the Start button's enabled state. */
  function _syncStartButton() {
    const btn = $('btn-start-game');
    if (!btn) return;
    const bothReady = state.player1.connected && state.player2.connected;
    btn.disabled    = !bothReady;
    btn.textContent = bothReady ? '▶  Start Game' : '⏳  Waiting for players…';
  }

  // Connection Simulation / Polling
  /**
   * Replace the body of this function with real socket logic
   */
  function _startConnectionPolling() {
    _stopConnectionPolling();

    // --- SIMULATION: replace this block with real WebSocket setup ---
    let p1Delay = 1200;  
    let p2Delay = 3500;   

    const t1 = setTimeout(() => {
      state.player1.connected = true;
      _renderPlayerSlot(1, true);
      _syncStartButton();
      _showToast('Player 1 connected!', 'success');
    }, p1Delay);

    const t2 = setTimeout(() => {
      state.player2.connected = true;
      _renderPlayerSlot(2, true);
      _syncStartButton();
      _showToast('Player 2 connected — both players ready!', 'success');
    }, p2Delay);

    state.pollingInterval = { t1, t2 };  
    // --- END SIMULATION ---
  }

  function _stopConnectionPolling() {
    if (state.pollingInterval) {
      clearTimeout(state.pollingInterval.t1);
      clearTimeout(state.pollingInterval.t2);
      state.pollingInterval = null;
    }
  }

  // Toast Notifications
  function _showToast(message, type = 'info') {
    const container = $('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast--${type}`;
    toast.textContent = message;
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transition = 'opacity 0.4s';
      setTimeout(() => toast.remove(), 450);
    }, 3200);
  }

  // Event Handlers
  function _onStartGame() {
    const game = state.game;
    console.log(`[Lobby] Starting ${game} session.`);
    _showToast(`Launching ${GAME_META[game]?.label ?? game} session…`, 'info');
    
    // TODO: send game-start signal to Server.java (WebSocket / fetch)
    // GameRouter.navigate(GameRouter.PAGES.GAME);
  }

  function _onBackToMenu() {
    _stopConnectionPolling();
    // Reset connection state
    state.player1.connected = false;
    state.player2.connected = false;
    _renderPlayerSlot(1, false);
    _renderPlayerSlot(2, false);
    _syncStartButton();
    GameRouter.goHome();
  }

  // GameRouter Integration
  function _onGameSelected(game) {
    state.game = game;
    _applyGameTheme(game);
    _renderPlayerSlot(1, false);
    _renderPlayerSlot(2, false);
    _syncStartButton();
    _startConnectionPolling();
  }

  // Init
  function init() {
    const startBtn = $('btn-start-game');
    const backBtn  = $('btn-back-menu');

    if (startBtn) startBtn.addEventListener('click', _onStartGame);
    if (backBtn)  backBtn.addEventListener('click',  _onBackToMenu);

    // Listen to the router for game selection events
    GameRouter.on('gameSelected', _onGameSelected);

    console.log('[LobbyController] Initialized.');
  }

  return { init, showToast: _showToast };
})();
