// Applies the selected game to the host lobby while connection integration is pending.
const LobbyController = (() => {
  const $ = id => document.getElementById(id);
  const gameLabels = { chess: 'Chess', checkers: 'Checkers' };

  function init() {
    $('btn-back-menu').addEventListener('click', GameRouter.goHome);
    GameRouter.on('gameSelected', game => {
      // TODO: Connect the host as a spectator once its server address is available, then show the player link.
      const heading = $('lobby-game-name');
      heading.textContent = gameLabels[game];
      heading.className = `lobby-header--${game}`;
      const icons = game === 'chess' ? ['♔', '♚'] : ['○', '●'];
      icons.forEach((icon, index) => {
        const avatar = $(`avatar-p${index + 1}`);
        avatar.className = `player-avatar player-avatar--${game}`;
        avatar.textContent = icon;
      });
    });

    // TODO: Update both player slots from server-confirmed joins and departures.
    // TODO: Enable Start Game once both players join and send the start request.
    // TODO: Show the board and current turn after the server confirms the start.
  }

  return { init };
})();
