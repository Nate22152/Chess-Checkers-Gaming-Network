package GameLogi;

// What the server gets back after asking the game logic to verify a move.
public class MoveResult
{
    private final boolean valid;
    private final String message;  // error text when invalid, short description when valid
    private final String nextTurn; // color of the player who moves next
    private final String winner;   // color of the winner, or null while the game is still going

    public MoveResult(boolean valid, String message, String nextTurn, String winner)
    {
        this.valid = valid;
        this.message = message;
        this.nextTurn = nextTurn;
        this.winner = winner;
    }

    public boolean isValid()
    {
        return valid;
    }

    public String getMessage()
    {
        return message;
    }

    public String getNextTurn()
    {
        return nextTurn;
    }

    public String getWinner()
    {
        return winner;
    }

    public boolean isGameOver()
    {
        return winner != null;
    }
}
