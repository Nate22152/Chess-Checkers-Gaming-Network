package GameLogi;

// Server-side move verification for Checkers.
// Owns the turn, the multi-jump state and the winner; the board is only changed by valid moves.
//
// Orientation (matches Board.checkersBoard):
//   Black starts on rows 0-2, moves toward row 7 and is kinged on row 7.
//   White starts on rows 5-7, moves toward row 0 and is kinged on row 0.
//   White moves first. Pieces only ever sit on dark squares, where (row + col) is odd.
public class CheckersGame
{
    public static final String WHITE = "White";
    public static final String BLACK = "Black";
    public static final String MOVE_FORMAT = "(piece) (fromCol) (fromRow) (toCol) (toRow), e.g. pw 0 5 1 4";

    private static final int[][] ALL_DIRECTIONS = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };

    private final Board board;
    private String turn = WHITE;
    private String winner = null;
    // Square of the piece that must keep jumping, or -1 when no multi-jump is in progress
    private int pendingRow = -1;
    private int pendingCol = -1;

    public CheckersGame()
    {
        this(new Board("Checkers"));
    }

    public CheckersGame(Board board)
    {
        this.board = board;
    }

    public Board getBoard()
    {
        return board;
    }

    public String getTurn()
    {
        return turn;
    }

    public String getWinner()
    {
        return winner;
    }

    // Parses a move message from a client and verifies it.
    // Format: (piece) (fromCol) (fromRow) (toCol) (toRow), 0-based, not case sensitive.
    // Piece is pw for a regular piece or k for a king. A leading "move" is allowed.
    public MoveResult handleCommand(String player, String line)
    {
        if (line == null) {
            return error("Empty move. Format: " + MOVE_FORMAT);
        }
        String[] parts = line.trim().toLowerCase().split("\\s+", 2);
        if (parts[0].equals("move") && parts.length == 2) {
            parts = parts[1].split("\\s+", 2);
        }
        if (parts.length < 2) {
            return error("Could not read that move. Format: " + MOVE_FORMAT);
        }
        String pieceName = parts[0];
        if (!pieceName.equals("pw") && !pieceName.equals("k")) {
            return error("Unknown piece '" + pieceName + "'. Use pw for a pawn or k for a king.");
        }
        // Every position is a single digit, so spaces between them are optional
        String digits = parts[1].replaceAll("\\s+", "");
        if (!digits.matches("\\d{4}")) {
            return error("Could not read the positions. Format: " + MOVE_FORMAT);
        }
        int fromCol = digits.charAt(0) - '0';
        int fromRow = digits.charAt(1) - '0';
        int toCol = digits.charAt(2) - '0';
        int toRow = digits.charAt(3) - '0';

        // The named piece has to match what is actually on the square
        if (board.inBounds(fromRow, fromCol)) {
            Piece piece = board.getPiece(fromRow, fromCol);
            if (piece != null && piece.isKing() != pieceName.equals("k")) {
                return error("The piece at " + describe(fromRow, fromCol) + " is a "
                        + (piece.isKing() ? "king (k)" : "pawn (pw)") + ".");
            }
        }
        return move(player, fromRow, fromCol, toRow, toCol);
    }

    // Verifies a move and, only if it is legal, applies it to the board.
    public MoveResult move(String player, int fromRow, int fromCol, int toRow, int toCol)
    {
        if (winner != null) {
            return error("The game is over. " + winner + " won.");
        }
        if (!turn.equals(player)) {
            return error("It is not your turn.");
        }

        // 1. Basic validation
        if (!board.inBounds(fromRow, fromCol) || !board.inBounds(toRow, toCol)) {
            return error("Columns and rows must be between 0 and " + (Board.SIZE - 1) + ".");
        }
        Piece piece = board.getPiece(fromRow, fromCol);
        if (piece == null) {
            return error("There is no piece at " + describe(fromRow, fromCol) + ".");
        }
        if (!piece.getColor().equals(player)) {
            return error("The piece at " + describe(fromRow, fromCol) + " is not yours.");
        }
        if (board.getPiece(toRow, toCol) != null) {
            return error("The square at " + describe(toRow, toCol) + " is already occupied.");
        }
        if (!isDarkSquare(toRow, toCol)) {
            return error("Pieces can only move onto dark squares.");
        }
        if (pendingRow != -1 && (fromRow != pendingRow || fromCol != pendingCol)) {
            return error("You must keep jumping with the piece at " + describe(pendingRow, pendingCol) + ".");
        }

        int deltaRow = toRow - fromRow;
        int deltaCol = toCol - fromCol;

        // Direction constraint (non-kings can only advance)
        if (!piece.isKing() && Integer.signum(deltaRow) != forward(player)) {
            return error("Only kings can move backward.");
        }

        // 2. Identify move type
        boolean isRegularMove = Math.abs(deltaRow) == 1 && Math.abs(deltaCol) == 1;
        boolean isJumpMove = Math.abs(deltaRow) == 2 && Math.abs(deltaCol) == 2;
        if (!isRegularMove && !isJumpMove) {
            return error("Pieces move one square diagonally, or two squares when jumping.");
        }

        // 3. Mandatory jump rule
        if (isRegularMove && (pendingRow != -1 || playerHasJump(player))) {
            return error("A jump is available, so you must jump.");
        }
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        if (isJumpMove) {
            Piece jumped = board.getPiece(midRow, midCol);
            if (jumped == null || jumped.getColor().equals(player)) {
                return error("There is no opposing piece to jump over.");
            }
        }

        // 4. Execute move
        board.setPiece(toRow, toCol, piece);
        board.clearSquare(fromRow, fromCol);
        if (isJumpMove) {
            board.clearSquare(midRow, midCol);
        }

        // 5. King promotion
        boolean justPromoted = false;
        if (!piece.isKing() && toRow == promotionRow(player)) {
            piece.setKing(true);
            justPromoted = true;
        }

        // 6. Multi-jump vs. turn end
        if (isJumpMove && !justPromoted && canJumpFrom(toRow, toCol)) {
            pendingRow = toRow;
            pendingCol = toCol;
            return new MoveResult(true, player + " jumped and must jump again from "
                    + describe(toRow, toCol) + ".", turn, null);
        }
        pendingRow = -1;
        pendingCol = -1;
        String opponent = opponentOf(player);
        turn = opponent;

        // Win condition: the opponent has no pieces left or no legal move
        if (!hasAnyMove(opponent)) {
            winner = player;
            return new MoveResult(true, player + " wins!", turn, winner);
        }
        String summary = player + (isJumpMove ? " jumped to " : " moved to ") + describe(toRow, toCol)
                + (justPromoted ? " and was kinged." : ".");
        return new MoveResult(true, summary, turn, null);
    }

    // ---- helpers ----

    private MoveResult error(String message)
    {
        return new MoveResult(false, message, turn, winner);
    }

    private static String describe(int row, int col)
    {
        return "col " + col + ", row " + row;
    }

    private static boolean isDarkSquare(int row, int col)
    {
        return (row + col) % 2 == 1;
    }

    private static String opponentOf(String player)
    {
        return player.equals(WHITE) ? BLACK : WHITE;
    }

    // Row direction a regular piece advances in
    private static int forward(String player)
    {
        return player.equals(BLACK) ? 1 : -1;
    }

    private static int promotionRow(String player)
    {
        return player.equals(BLACK) ? Board.SIZE - 1 : 0;
    }

    // Diagonals a piece is allowed to travel along
    private static int[][] directionsFor(Piece piece)
    {
        if (piece.isKing()) {
            return ALL_DIRECTIONS;
        }
        int rowStep = forward(piece.getColor());
        return new int[][] { {rowStep, -1}, {rowStep, 1} };
    }

    // True if the piece on this square can capture something (HasSubsequentJump in the design doc)
    private boolean canJumpFrom(int row, int col)
    {
        Piece piece = board.getPiece(row, col);
        for (int[] dir : directionsFor(piece)) {
            int midRow = row + dir[0];
            int midCol = col + dir[1];
            int landRow = row + 2 * dir[0];
            int landCol = col + 2 * dir[1];
            if (!board.inBounds(landRow, landCol) || board.getPiece(landRow, landCol) != null) {
                continue;
            }
            Piece middle = board.getPiece(midRow, midCol);
            if (middle != null && !middle.getColor().equals(piece.getColor())) {
                return true;
            }
        }
        return false;
    }

    private boolean canStepFrom(int row, int col)
    {
        Piece piece = board.getPiece(row, col);
        for (int[] dir : directionsFor(piece)) {
            int toRow = row + dir[0];
            int toCol = col + dir[1];
            if (board.inBounds(toRow, toCol) && board.getPiece(toRow, toCol) == null) {
                return true;
            }
        }
        return false;
    }

    // True if any of the player's pieces can capture (FindAllJumpsForPlayer in the design doc)
    private boolean playerHasJump(String player)
    {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor().equals(player) && canJumpFrom(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    // False when the player has no pieces left or every piece is blocked
    private boolean hasAnyMove(String player)
    {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor().equals(player)
                        && (canJumpFrom(row, col) || canStepFrom(row, col))) {
                    return true;
                }
            }
        }
        return false;
    }
}
