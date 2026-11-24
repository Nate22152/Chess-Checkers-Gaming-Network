package GameLogi;

public class Board
{
    public static final int SIZE = 8;

    private Piece[][] grid;

    public Board(String gameType)
    {
        switch(gameType)
        {
            case "Chess":
                chessBoard();
                break;
            case "Checkers":
                checkersBoard();
                break;
            default:
                throw new IllegalArgumentException("Unsupported game type: " + gameType);
        }
    }
    // Add methods to manipulate the board state
    private void chessBoard()
    {
        grid = new Piece[8][8];
        // Initialize chess pieces on the board
        // For simplicity, we can just place pawns and rooks as an example
        for (int i = 0; i < 8; i++) {
            grid[1][i] = new Piece("Pawn", "Black");
            grid[6][i] = new Piece("Pawn", "White");
        }
        grid[0][0] = new Piece("Rook", "Black");
        grid[0][7] = new Piece("Rook", "Black");
        grid[7][0] = new Piece("Rook", "White");
        grid[7][7] = new Piece("Rook", "White");
    }

    private void checkersBoard()
    {
        grid = new Piece[8][8];
        // Initialize checkers pieces on the board (dark squares only)
        // Black starts on rows 0-2, White starts on rows 5-7, same sides as the chess board
        for (int i = 0; i < 3; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                grid[i][j] = new Piece("Checker", "Black");
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                grid[i][j] = new Piece("Checker", "White");
            }
        }
    }

    public boolean inBounds(int row, int col)
    {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    // Returns the piece on a square, or null if the square is empty
    public Piece getPiece(int row, int col)
    {
        return grid[row][col];
    }

    public void setPiece(int row, int col, Piece piece)
    {
        grid[row][col] = piece;
    }

    public void clearSquare(int row, int col)
    {
        grid[row][col] = null;
    }

    // Rudimentary console view of the board. Columns run across the top, rows down the side.
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (int col = 0; col < SIZE; col++) {
            sb.append(' ').append(col);
        }
        sb.append(" (col)\n");
        for (int row = 0; row < SIZE; row++) {
            sb.append(' ').append(row).append(' ');
            for (int col = 0; col < SIZE; col++) {
                Piece piece = grid[row][col];
                sb.append(' ').append(piece == null ? '.' : piece.getSymbol());
            }
            sb.append('\n');
        }
        sb.append("(row)");
        return sb.toString();
    }
}
