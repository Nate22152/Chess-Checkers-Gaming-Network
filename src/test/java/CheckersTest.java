import GameLogi.Board;
import GameLogi.CheckersGame;
import GameLogi.MoveResult;
import GameLogi.Piece;

// Plain main-based tests for the checkers move verification (no build tool or JUnit needed).
// Compile: javac -d out GameLogi/*.java CheckersTest.java
// Run:     java -cp out CheckersTest
public class CheckersTest
{
    private static final String W = CheckersGame.WHITE;
    private static final String B = CheckersGame.BLACK;

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args)
    {
        openingMove();
        wrongTurn();
        basicValidation();
        backwardMoveByNonKing();
        kingCanMoveBackward();
        forcedJump();
        doubleJump();
        blackDoubleJump();
        kingJumps();
        promotionEndsTurn();
        winByCapture();
        winByBlocking();
        commandParsing();

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---- tests ----

    private static void openingMove()
    {
        CheckersGame game = new CheckersGame();
        MoveResult result = game.move(W, 5, 0, 4, 1);
        check("opening move is valid", result.isValid());
        check("opening move passes the turn to Black", B.equals(result.getNextTurn()));
        check("opening move leaves the start square empty", game.getBoard().getPiece(5, 0) == null);
        check("opening move puts the piece on the target", game.getBoard().getPiece(4, 1) != null);
        check("Black can reply", game.move(B, 2, 1, 3, 2).isValid());
    }

    private static void wrongTurn()
    {
        CheckersGame game = new CheckersGame();
        MoveResult result = game.move(B, 2, 1, 3, 2);
        check("Black cannot move first", !result.isValid());
        check("rejected move leaves the board alone", game.getBoard().getPiece(2, 1) != null
                && game.getBoard().getPiece(3, 2) == null);
        check("rejected move keeps the turn", W.equals(game.getTurn()));
    }

    private static void basicValidation()
    {
        CheckersGame game = new CheckersGame();
        check("out of bounds is rejected", !game.move(W, 5, 0, 4, -1).isValid());
        check("empty start square is rejected", !game.move(W, 4, 1, 3, 2).isValid());
        check("moving the opponent's piece is rejected", !game.move(W, 2, 1, 3, 2).isValid());
        check("occupied destination is rejected", !game.move(W, 6, 1, 5, 2).isValid());
        check("light square destination is rejected", !game.move(W, 5, 0, 4, 0).isValid());
        check("moving three squares is rejected", !game.move(W, 5, 0, 2, 3).isValid());
        check("jumping over nothing is rejected", !game.move(W, 5, 0, 3, 2).isValid());
    }

    private static void backwardMoveByNonKing()
    {
        CheckersGame game = custom();
        place(game, 4, 3, W, false);
        place(game, 0, 1, B, false);
        check("non-king cannot move backward", !game.move(W, 4, 3, 5, 4).isValid());
        check("non-king can move forward", game.move(W, 4, 3, 3, 2).isValid());
    }

    private static void kingCanMoveBackward()
    {
        CheckersGame game = custom();
        place(game, 3, 2, W, true);
        place(game, 0, 7, B, false);
        check("king can move backward", game.move(W, 3, 2, 4, 3).isValid());
    }

    private static void forcedJump()
    {
        CheckersGame game = custom();
        place(game, 4, 3, W, false);
        place(game, 6, 1, W, false);
        place(game, 3, 4, B, false);
        place(game, 0, 1, B, false);
        check("another piece cannot step when a jump exists", !game.move(W, 6, 1, 5, 0).isValid());
        check("the jumping piece cannot step instead", !game.move(W, 4, 3, 3, 2).isValid());
        MoveResult result = game.move(W, 4, 3, 2, 5);
        check("the jump is valid", result.isValid());
        check("the jumped piece is removed", game.getBoard().getPiece(3, 4) == null);
        check("a single jump ends the turn", B.equals(result.getNextTurn()));
    }

    private static void doubleJump()
    {
        CheckersGame game = custom();
        place(game, 6, 1, W, false);
        place(game, 7, 6, W, false);
        place(game, 5, 2, B, false);
        place(game, 3, 4, B, false);
        place(game, 0, 7, B, false);
        MoveResult first = game.move(W, 6, 1, 4, 3);
        check("first jump is valid", first.isValid());
        check("first jump keeps the turn", W.equals(first.getNextTurn()));
        check("opponent cannot move mid multi-jump", !game.move(B, 0, 7, 1, 6).isValid());
        check("a different piece cannot move mid multi-jump", !game.move(W, 7, 6, 6, 5).isValid());
        check("the jumping piece cannot step mid multi-jump", !game.move(W, 4, 3, 3, 2).isValid());
        MoveResult second = game.move(W, 4, 3, 2, 5);
        check("second jump is valid", second.isValid());
        check("second jump ends the turn", B.equals(second.getNextTurn()));
        check("both jumped pieces are removed", game.getBoard().getPiece(5, 2) == null
                && game.getBoard().getPiece(3, 4) == null);
    }

    private static void blackDoubleJump()
    {
        CheckersGame game = custom();
        place(game, 7, 0, W, false);
        place(game, 3, 2, W, false);
        place(game, 4, 5, W, false);
        place(game, 1, 2, B, false);
        place(game, 0, 7, B, false);
        check("White steps next to Black", game.move(W, 3, 2, 2, 3).isValid());
        check("Black cannot step when a jump exists", !game.move(B, 0, 7, 1, 6).isValid());
        MoveResult first = game.move(B, 1, 2, 3, 4);
        check("Black's first jump is valid and keeps the turn", first.isValid() && B.equals(first.getNextTurn()));
        MoveResult second = game.move(B, 3, 4, 5, 6);
        check("Black's second jump is valid and ends the turn", second.isValid() && W.equals(second.getNextTurn()));
        check("Black's jumped pieces are removed", game.getBoard().getPiece(2, 3) == null
                && game.getBoard().getPiece(4, 5) == null);
    }

    private static void kingJumps()
    {
        CheckersGame game = custom();
        place(game, 2, 1, W, true);
        place(game, 3, 2, B, false);
        place(game, 3, 4, B, false);
        place(game, 0, 7, B, false);
        MoveResult first = game.move(W, 2, 1, 4, 3); // backward capture
        check("king can jump backward", first.isValid() && W.equals(first.getNextTurn()));
        MoveResult second = game.move(W, 4, 3, 2, 5); // then forward again
        check("king continues a multi-jump in another direction", second.isValid()
                && B.equals(second.getNextTurn()));
        check("king's jumped pieces are removed", game.getBoard().getPiece(3, 2) == null
                && game.getBoard().getPiece(3, 4) == null);

        CheckersGame man = custom();
        place(man, 2, 1, W, false);
        place(man, 3, 2, B, false);
        place(man, 0, 7, B, false);
        check("non-king cannot jump backward", !man.move(W, 2, 1, 4, 3).isValid());
    }

    private static void promotionEndsTurn()
    {
        CheckersGame game = custom();
        place(game, 2, 1, W, false);
        place(game, 1, 2, B, false);
        place(game, 1, 4, B, false); // a king on (0,3) could jump this, but promotion ends the turn
        MoveResult result = game.move(W, 2, 1, 0, 3);
        check("jump onto the last row is valid", result.isValid());
        check("the piece is kinged", game.getBoard().getPiece(0, 3).isKing());
        check("promotion ends the turn even with a jump available", B.equals(result.getNextTurn()));
        check("the game is not over", !result.isGameOver());

        CheckersGame black = custom();
        place(black, 7, 6, W, false);
        place(black, 6, 1, B, false);
        black.move(W, 7, 6, 6, 5);
        check("Black is kinged on row 7", black.move(B, 6, 1, 7, 0).isValid()
                && black.getBoard().getPiece(7, 0).isKing());
    }

    private static void winByCapture()
    {
        CheckersGame game = custom();
        place(game, 4, 3, W, false);
        place(game, 3, 4, B, false);
        MoveResult result = game.move(W, 4, 3, 2, 5);
        check("capturing the last piece is valid", result.isValid());
        check("capturing the last piece wins", result.isGameOver() && W.equals(result.getWinner()));
        check("no moves are accepted after the game ends", !game.move(B, 0, 1, 1, 0).isValid()
                && !game.move(W, 2, 5, 1, 4).isValid());
    }

    private static void winByBlocking()
    {
        CheckersGame game = custom();
        place(game, 0, 1, B, false);
        place(game, 1, 0, W, false);
        place(game, 1, 2, W, false);
        place(game, 2, 3, W, false);
        place(game, 6, 5, W, false);
        MoveResult result = game.move(W, 6, 5, 5, 4);
        check("blocking every opposing piece wins", result.isValid() && W.equals(result.getWinner()));
    }

    private static void commandParsing()
    {
        CheckersGame game = new CheckersGame();
        check("garbage is rejected", !game.handleCommand(W, "hello there").isValid());
        check("missing positions are rejected", !game.handleCommand(W, "pw 0 5 1").isValid());
        check("unknown piece name is rejected", !game.handleCommand(W, "rk 0 5 1 4").isValid());
        check("wrong piece name is rejected", !game.handleCommand(W, "k 0 5 1 4").isValid());
        check("out of range position is rejected", !game.handleCommand(W, "pw 0 5 9 4").isValid());
        check("null is rejected", !game.handleCommand(W, null).isValid());
        check("col-then-row order is used", game.handleCommand(W, "pw 0 5 1 4").isValid()
                && game.getBoard().getPiece(4, 1) != null);
        check("upper case and leading 'move' are accepted", game.handleCommand(B, "MOVE PW 1 2 2 3").isValid());
        check("positions without spaces are accepted", game.handleCommand(W, "pw 25 34").isValid());

        CheckersGame kings = custom();
        place(kings, 3, 2, W, true);
        place(kings, 0, 7, B, false);
        check("a king must be named k", !kings.handleCommand(W, "pw 2 3 3 4").isValid());
        check("a king moves with k", kings.handleCommand(W, "k 2 3 3 4").isValid());
    }

    // ---- helpers ----

    // A game on an empty board so each test can set up its own position. White moves first.
    private static CheckersGame custom()
    {
        Board board = new Board("Checkers");
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                board.clearSquare(row, col);
            }
        }
        return new CheckersGame(board);
    }

    private static void place(CheckersGame game, int row, int col, String color, boolean king)
    {
        Piece piece = new Piece("Checker", color);
        piece.setKing(king);
        game.getBoard().setPiece(row, col, piece);
    }

    private static void check(String name, boolean condition)
    {
        if (condition) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }
}
