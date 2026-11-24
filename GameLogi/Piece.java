package GameLogi;

// maybe we can add color to the text in the future, but for now, we will just use strings to represent the pieces
public class Piece {
    private String type;
    private String color;
    private boolean king; // only used by checkers pieces

    public Piece(String type, String color) {
        this.type = type;
        this.color = color;
        this.king = false;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public boolean isKing() {
        return king;
    }

    public void setKing(boolean king) {
        this.king = king;
    }

    // One character used when printing the board to the console.
    // Checkers: w/b for a regular piece, W/B for a king.
    // Chess: first letter of the piece (N for Knight), uppercase for White, lowercase for Black.
    public char getSymbol() {
        char symbol;
        if (type.equals("Checker")) {
            symbol = Character.toLowerCase(color.charAt(0));
            return king ? Character.toUpperCase(symbol) : symbol;
        }
        symbol = type.equals("Knight") ? 'N' : type.charAt(0);
        return color.equals("White") ? Character.toUpperCase(symbol) : Character.toLowerCase(symbol);
    }
}
