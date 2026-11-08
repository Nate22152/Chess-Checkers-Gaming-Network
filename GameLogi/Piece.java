package GameLogi;

// maybe we can add color to the text in the future, but for now, we will just use strings to represent the pieces
public class Piece {
    private String type;
    private String color;

    public Piece(String type, String color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }
}