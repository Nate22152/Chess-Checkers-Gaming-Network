# Chess-Checkers-Gaming-Network
This is a project that is associated with UTD CS 4390 Group 4 final project that is geared to using a server-client build.

## Repository layout

```text
src/
  main/java/          Java client, server, and game session
    GameLogi/         Board, pieces, and checkers rules
  test/java/          Checkers tests
ui/                   Browser host and player pages, styles, and scripts
```





## Browser UI

See the [UI README](ui/README.md) for the host and player pages

## Move commands

Move actions should be in format of (**IT WILL NOT BE CASE SENSITIVE**)

    **move (Peice) (Col) (Row)**

Chess Ex:
    move rk 4 4

Checkers moves give the square the piece is on and the square it is going to (columns and rows are 0-7):

    **(Piece) (FromCol) (FromRow) (ToCol) (ToRow)**

Checkers Ex: 
    pw 0 5 1 4 

Chess Piece names:
    Pawn - **Pw**
    Rook - **Rk**
    Bishop - **Bs**
    Knight - **Kn**
    Queen - **Q**
    King - **K**

Checker Piece names:
    Pawn - **Pw**
    King - **K**
