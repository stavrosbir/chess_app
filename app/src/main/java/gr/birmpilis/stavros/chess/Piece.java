package gr.birmpilis.stavros.chess;

/**
 * Created by stavros on 6/11/2015.
 */
public class Piece {

    final private String color;
    final private String kind;
    private int position;
    final private int resource;
    final int value;

    public String getColor() {
        return color;
    }

    public String getKind() {
        return kind;
    }

    public int getResource() {
        return resource;
    }

    public int getPosition() {
        return position;
    }

    public String getPositionS() {
        String pos;
        String second = String.valueOf(8 - position / 8);
        char first = (char) ('a' + position % 8);
        pos = first + second;
        return pos;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Piece(String color, String kind, int resource) {
        this.color = color;
        this.kind = kind;
        this.resource = resource;
        switch (kind) {
            case "pawn":
                value = 1;
                break;
            case "knight":
                value = 3;
                break;
            case "bishop":
                value = 3;
                break;
            case "rook":
                value = 5;
                break;
            case "queen":
                value = 9;
                break;
            case "king":
                value = 1000;
                break;
            default:
                value = 0;
        }
    }

}
