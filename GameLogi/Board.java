package GameLogi;

public class Board
{
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
        // Initialize checkers pieces on the board
        for (int i = 0; i < 3; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                grid[i][j] = new Piece("Checker", "Red");
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                grid[i][j] = new Piece("Checker", "Navy");
            }
        }
    }
}

