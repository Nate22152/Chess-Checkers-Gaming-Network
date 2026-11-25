# Host and player pages

Open `host.html` for the host/spectator lobby or `player.html` for a player. The host address field is disabled pending automatic connection. Players can select a game and enter a WebSocket address. Each player waits for the server to assign their slot.

On powershell, run:
```
Start-Process .\ui\host.html
Start-Process .\ui\player.html
```

The current Java server listens for terminal clients over raw TCP on port 5000. These pages require a browser-compatible WebSocket endpoint.

## Integration TODOs

- Add a WebSocket endpoint or gateway and publish its URL. Serve the UI over HTTP(S) with an allowed browser origin, or explicitly support local file pages during development. Use `wss://` for an HTTPS deployment.
- Connect the host to the endpoint automatically and provide a shareable player link. Enable Start Game after the server reports both players.
- Agree on the JSON messages below and implement them on the server. Keep one lobby per game type at the endpoint, assign two player slots, free them on disconnect, and send updated lobby state to the host. Enforce the two-player requirement on the server too.
- Wire the host's start request into the Java session. Send the start confirmation and board updates to the host, and forward game state to both players. When a host reconnects to a running game, send the lobby state, start confirmation, and current board in that order.
- Agree on move messages, then add player move controls and forward the Java session's validation errors to the appropriate browser.

## Proposed WebSocket messages

| Direction | JSON message |
| --- | --- |
| Host → server | `{"type":"watch","game":"chess"}` |
| Player → server | `{"type":"join","game":"chess"}` |
| Server → player | `{"type":"joined","slot":1}` |
| Server → host | `{"type":"lobby","players":[true,false]}` |
| Host → server | `{"type":"start"}` |
| Server → host | `{"type":"started"}` |
| Server → host and players | `{"type":"board","board":"board text","turn":"White"}` |
| Server → affected browser | `{"type":"error","message":"This game is full."}` |

Use `chess` or `checkers` for the game. Lobby entries correspond to Player 1 and Player 2 in order. The board is plain text, and an empty `turn` clears the turn label.
