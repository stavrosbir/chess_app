package gr.birmpilis.stavros.chess;

/**
 * Created by stavros on 8/11/2015.
 */
public class Tile {

    public Piece piece;
    public int position;
    public boolean isProtectedByWhite, isProtectedByBlack, isEmpty;

    public void makeEmpty() {
        isEmpty = true;
    }

    public void setPieceOn(Piece piece) {
        this.piece = piece;
        isEmpty = false;
    }

    public Tile(int position) {
        this.position = position;
        isEmpty = true;
        isProtectedByWhite = false;
        isProtectedByBlack = false;
    }
}
