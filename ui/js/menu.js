const MenuController = (() => {
  function _getEl(id) {
    const el = document.getElementById(id);
    if (!el) console.warn(`[MenuController] Element not found: #${id}`);
    return el;
  }

  // Clicker for menu page selecting chess or checkers
  function _onChessClick() {
    console.log('[Menu] Chess selected.');
    GameRouter.selectGame('chess');
  }

  function _onCheckersClick() {
    console.log('[Menu] Checkers selected.');
    GameRouter.selectGame('checkers');
  }

  // ── Init ─────────────────────────────────────────────────────
  function init() {
    const chessCard    = _getEl('card-chess');
    const checkersCard = _getEl('card-checkers');
    const chessBtnEl   = _getEl('btn-chess');
    const checkersBtnEl = _getEl('btn-checkers');

    if (chessBtnEl)    chessBtnEl.addEventListener('click',    e => { e.stopPropagation(); _onChessClick();    });
    if (checkersBtnEl) checkersBtnEl.addEventListener('click', e => { e.stopPropagation(); _onCheckersClick(); });

    // Clicking the whole card also selects the game
    if (chessCard)    { chessCard.addEventListener('click',    _onChessClick); }
    if (checkersCard) { checkersCard.addEventListener('click', _onCheckersClick); }


    console.log('[MenuController] Initialized.');
  }

  return { init };
})();
