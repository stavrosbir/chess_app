package gr.birmpilis.stavros.chess;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by stavros on 1/11/2015.
 */
public class Board extends Activity implements AdapterView.OnItemClickListener {

    static boolean hold = false;
    static Piece pieceOnHold;
    boolean whiteTurn = true;
    GridView chessboardGridView;
    TextView moveText;
    SquareAdapter adapter;
    static Piece pieces[] = new Piece[32];
    static Tile board[];
    static ArrayList<Integer> glist;
    boolean castlingPossible[] = {true, true, true, true};
    int enpassan = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chessgrid);
        moveText = (TextView) findViewById(R.id.move);

        initPieces();
        initBoard();
        adapter = new SquareAdapter(this);

        chessboardGridView = (GridView) findViewById(R.id.chessgrid);
        chessboardGridView.setOnItemClickListener(this);
        chessboardGridView.setAdapter(adapter);
    }

    private void initBoard() {
        board = new Tile[64];
        for (int i = 0; i < 64; i++) {
            board[i] = new Tile(i);
            if (i < 8) {
                board[i].setPieceOn(pieces[i + 24]);
            } else if (i < 16) {
                board[i].setPieceOn(pieces[i + 8]);
            } else if (i >= 48) {
                board[i].setPieceOn(pieces[i - 48]);
            }
        }
    }

    private void initPieces() {
        //pawns
        for (int i = 0; i < 8; i++) {
            pieces[i] = new Piece("white", "pawn", R.drawable.white_pawn);
            pieces[i].setPosition(i + 48);
            pieces[i + 16] = new Piece("black", "pawn", R.drawable.black_pawn);
            pieces[i + 16].setPosition(i + 8);
        }
        //rooks
        pieces[8] = new Piece("white", "rook", R.drawable.white_rook);
        pieces[8].setPosition(56);
        pieces[24] = new Piece("black", "rook", R.drawable.black_rook);
        pieces[24].setPosition(0);
        pieces[15] = new Piece("white", "rook", R.drawable.white_rook);
        pieces[15].setPosition(63);
        pieces[31] = new Piece("black", "rook", R.drawable.black_rook);
        pieces[31].setPosition(7);
        //knights
        pieces[9] = new Piece("white", "knight", R.drawable.white_knight);
        pieces[9].setPosition(57);
        pieces[25] = new Piece("black", "knight", R.drawable.black_knight);
        pieces[25].setPosition(1);
        pieces[14] = new Piece("white", "knight", R.drawable.white_knight);
        pieces[14].setPosition(62);
        pieces[30] = new Piece("black", "knight", R.drawable.black_knight);
        pieces[30].setPosition(6);
        //bishops
        pieces[10] = new Piece("white", "bishop", R.drawable.white_bishop);
        pieces[10].setPosition(58);
        pieces[26] = new Piece("black", "bishop", R.drawable.black_bishop);
        pieces[26].setPosition(2);
        pieces[13] = new Piece("white", "bishop", R.drawable.white_bishop);
        pieces[13].setPosition(61);
        pieces[29] = new Piece("black", "bishop", R.drawable.black_bishop);
        pieces[29].setPosition(5);
        //queens
        pieces[11] = new Piece("white", "queen", R.drawable.white_queen);
        pieces[11].setPosition(59);
        pieces[27] = new Piece("black", "queen", R.drawable.black_queen);
        pieces[27].setPosition(3);
        //kings
        pieces[12] = new Piece("white", "king", R.drawable.white_king);
        pieces[12].setPosition(60);
        pieces[28] = new Piece("black", "king", R.drawable.black_king);
        pieces[28].setPosition(4);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (hold) {
            makeMove(position, pieceOnHold);
            chessboardGridView.setAdapter(adapter);
        } else {
            if (!board[position].isEmpty) {
                Piece piece = board[position].piece;
                if (piece.getColor().equals("white") == whiteTurn) {
                    hold = true;
                    pieceOnHold = piece;

                    ArrayList<Integer> legals = PseudoLegalMoves(piece);
                    glist = legals;
                    chessboardGridView.setAdapter(adapter);
                } else {
                    if (whiteTurn) {
                        Toast.makeText(this, "White is playing!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Black is playing!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void makeMove(int to, Piece piece) {

        //more checks
        if (glist.contains(to)) {

            Tile[] tempboard = board;

            //check if there is something to take
            String takes = "";
            if (!board[to].isEmpty) {
                Piece pieceTaken = board[to].piece;
                takes = " and takes " + pieceTaken.getColor() + " " + pieceTaken.getKind();
            }
            //now set the new position
            updateCastling(piece);
            board[piece.getPosition()].makeEmpty();
            piece.setPosition(to);
            board[piece.getPosition()].setPieceOn(piece);
            //do side Analysis
            protectedTilesAnalysis();
            whiteTurn = !whiteTurn;

            //you hava much much work to do......

            //comment
            moveText.setText(String.format("%s moves %s%s to %s", piece.getColor(), piece.getKind(), takes, piece.getPositionS()));
        } else {
            Toast.makeText(this, "Not a valid move", Toast.LENGTH_LONG).show();
        }
        hold = false;
    }

    private void updateCastling(Piece piece) {
        if (piece.getKind().equals("king")) {
            if (piece.getColor().equals("white")) {
                castlingPossible[0] = false;
                castlingPossible[1] = false;
            } else {
                castlingPossible[2] = false;
                castlingPossible[3] = false;
            }
        }
        if (piece.getKind().equals("rook")) {
            if (piece.getColor().equals("white")) {
                if (piece.getPosition() == 63) {
                    castlingPossible[0] = false;
                } else {
                    castlingPossible[1] = false;
                }
            } else {
                if (piece.getPosition() == 0) {
                    castlingPossible[3] = false;
                } else {
                    castlingPossible[2] = false;
                }
            }
        }
    }

    private void protectedTilesAnalysis() {
        for (Tile tile : board) {
            tile.isProtectedByWhite = false;
            tile.isProtectedByBlack = false;
        }
        ArrayList<Integer> wl, bl;
        for (Tile tile : board) {
            if (!tile.isEmpty) {
                if (tile.piece.getColor().equals("white")) {
                    wl = PseudoLegalMoves(tile.piece);
                    for (int wt : wl) {
                        board[wt].isProtectedByWhite = true;
                    }
                } else {
                    bl = PseudoLegalMoves(tile.piece);
                    for (int bt : bl) {
                        board[bt].isProtectedByBlack = true;
                    }
                }
            }
        }
    }

    private ArrayList<Integer> PseudoLegalMoves(Piece piece) {
        int from = pieceOnHold.getPosition(), x = from % 8, y = from / 8, c;
        ArrayList<Integer> list = new ArrayList<>();
        switch (piece.getKind()) {
            case "pawn":
                if (whiteTurn) {
                    if (board[from - 8].isEmpty) {
                        list.add(from - 8);
                        if (y == 6 && board[from - 16].isEmpty) {
                            list.add(from - 16);
                        }
                    }
                    if (x + 1 < 8 && !board[from - 7].isEmpty && board[from - 7].piece.getColor().equals("black")) {
                        list.add(from - 7);
                    }
                    if (x - 1 >= 0 && !board[from - 9].isEmpty && board[from - 9].piece.getColor().equals("black")) {
                        list.add(from - 9);
                    }
                } else {
                    if (board[from + 8].isEmpty) {
                        list.add(from + 8);
                        if (y == 1 && board[from + 16].isEmpty) {
                            list.add(from + 16);
                        }
                    }
                    if (x - 1 >= 0 && !board[from + 7].isEmpty && board[from + 7].piece.getColor().equals("white")) {
                        list.add(from + 7);
                    }
                    if (x + 1 < 8 && !board[from + 9].isEmpty && board[from + 9].piece.getColor().equals("white")) {
                        list.add(from + 9);
                    }
                }
                break;
            case "knight":
                if (from - 17 >= 0 && x - 1 >= 0 && diffColorOrEmpty(pieceOnHold, board[from - 17])) {
                    list.add(from - 17);
                }
                if (from - 10 >= 0 && x - 2 >= 0 && diffColorOrEmpty(pieceOnHold, board[from - 10])) {
                    list.add(from - 10);
                }
                if (from - 15 >= 0 && x + 1 < 8 && diffColorOrEmpty(pieceOnHold, board[from - 15])) {
                    list.add(from - 15);
                }
                if (from - 6 >= 0 && x + 2 < 8 && diffColorOrEmpty(pieceOnHold, board[from - 6])) {
                    list.add(from - 6);
                }
                if (from + 6 < 64 && x - 2 >= 0 && diffColorOrEmpty(pieceOnHold, board[from + 6])) {
                    list.add(from + 6);
                }
                if (from + 15 < 64 && x - 1 >= 0 && diffColorOrEmpty(pieceOnHold, board[from + 15])) {
                    list.add(from + 15);
                }
                if (from + 10 < 64 && x + 2 < 8 && diffColorOrEmpty(pieceOnHold, board[from + 10])) {
                    list.add(from + 10);
                }
                if (from + 17 < 64 && x + 1 < 8 && diffColorOrEmpty(pieceOnHold, board[from + 17])) {
                    list.add(from + 17);
                }
                break;
            case "bishop":
                c = 1;
                while (from - 9 * c >= 0 && x - c >= 0 && board[from - 9 * c].isEmpty) {
                    list.add(from - 9 * c);
                    c++;
                }
                if (from - 9 * c >= 0 && x - c >= 0 && !board[from - 9 * c].isEmpty && !sameColor(pieceOnHold, board[from - 9 * c])) {
                    list.add(from - 9 * c);
                }
                c = 1;
                while (from - 7 * c >= 0 && x + c < 8 && board[from - 7 * c].isEmpty) {
                    list.add(from - 7 * c);
                    c++;
                }
                if (from - 7 * c >= 0 && x + c < 8 && !board[from - 7 * c].isEmpty && !sameColor(pieceOnHold, board[from - 7 * c])) {
                    list.add(from - 7 * c);
                }
                c = 1;
                while (from + 9 * c < 64 && x + c < 8 && board[from + 9 * c].isEmpty) {
                    list.add(from + 9 * c);
                    c++;
                }
                if (from + 9 * c < 64 && x + c < 8 && !board[from + 9 * c].isEmpty && !sameColor(pieceOnHold, board[from + 9 * c])) {
                    list.add(from + 9 * c);
                }
                c = 1;
                while (from + 7 * c < 64 && x - c >= 0 && board[from + 7 * c].isEmpty) {
                    list.add(from + 7 * c);
                    c++;
                }
                if (from + 7 * c < 64 && x - c >= 0 && !board[from + 7 * c].isEmpty && !sameColor(pieceOnHold, board[from + 7 * c])) {
                    list.add(from + 7 * c);
                }
                break;
            case "rook":
                c = 1;
                while (from - 8 * c >= 0 && board[from - 8 * c].isEmpty) {
                    list.add(from - 8 * c);
                    c++;
                }
                if (from - 8 * c >= 0 && !board[from - 8 * c].isEmpty && !sameColor(pieceOnHold, board[from - 8 * c])) {
                    list.add(from - 8 * c);
                }
                c = 1;
                while (from - c >= 0 && x - c >= 0 && board[from - c].isEmpty) {
                    list.add(from - c);
                    c++;
                }
                if (from - c >= 0 && x - c >= 0 && !board[from - c].isEmpty && !sameColor(pieceOnHold, board[from - c])) {
                    list.add(from * c);
                }
                c = 1;
                while (from + 8 * c < 64 && board[from + 8 * c].isEmpty) {
                    list.add(from + 8 * c);
                    c++;
                }
                if (from + 8 * c < 64 && !board[from + 8 * c].isEmpty && !sameColor(pieceOnHold, board[from + 8 * c])) {
                    list.add(from + 8 * c);
                }
                c = 1;
                while (from + c < 64 && x + c < 8 && board[from + c].isEmpty) {
                    list.add(from + c);
                    c++;
                }
                if (from + c < 64 && x + c < 8 && !board[from + c].isEmpty && !sameColor(pieceOnHold, board[from + c])) {
                    list.add(from + c);
                }
                break;
            case "queen":
                c = 1;
                while (from - 9 * c >= 0 && x - c >= 0 && board[from - 9 * c].isEmpty) {
                    list.add(from - 9 * c);
                    c++;
                }
                if (from - 9 * c >= 0 && x - c >= 0 && !board[from - 9 * c].isEmpty && !sameColor(pieceOnHold, board[from - 9 * c])) {
                    list.add(from - 9 * c);
                }
                c = 1;
                while (from - 7 * c >= 0 && x + c < 8 && board[from - 7 * c].isEmpty) {
                    list.add(from - 7 * c);
                    c++;
                }
                if (from - 7 * c >= 0 && x + c < 8 && !board[from - 7 * c].isEmpty && !sameColor(pieceOnHold, board[from - 7 * c])) {
                    list.add(from - 7 * c);
                }
                c = 1;
                while (from + 9 * c < 64 && x + c < 8 && board[from + 9 * c].isEmpty) {
                    list.add(from + 9 * c);
                    c++;
                }
                if (from + 9 * c < 64 && x + c < 8 && !board[from + 9 * c].isEmpty && !sameColor(pieceOnHold, board[from + 9 * c])) {
                    list.add(from + 9 * c);
                }
                c = 1;
                while (from + 7 * c < 64 && x - c >= 0 && board[from + 7 * c].isEmpty) {
                    list.add(from + 7 * c);
                    c++;
                }
                if (from + 7 * c < 64 && x - c >= 0 && !board[from + 7 * c].isEmpty && !sameColor(pieceOnHold, board[from + 7 * c])) {
                    list.add(from + 7 * c);
                }
                c = 1;
                while (from - 8 * c >= 0 && board[from - 8 * c].isEmpty) {
                    list.add(from - 8 * c);
                    c++;
                }
                if (from - 8 * c >= 0 && !board[from - 8 * c].isEmpty && !sameColor(pieceOnHold, board[from - 8 * c])) {
                    list.add(from - 8 * c);
                }
                c = 1;
                while (from - c >= 0 && x - c >= 0 && board[from - c].isEmpty) {
                    list.add(from - c);
                    c++;
                }
                if (from - c >= 0 && x - c >= 0 && !board[from - c].isEmpty && !sameColor(pieceOnHold, board[from - c])) {
                    list.add(from * c);
                }
                c = 1;
                while (from + 8 * c < 64 && board[from + 8 * c].isEmpty) {
                    list.add(from + 8 * c);
                    c++;
                }
                if (from + 8 * c < 64 && !board[from + 8 * c].isEmpty && !sameColor(pieceOnHold, board[from + 8 * c])) {
                    list.add(from + 8 * c);
                }
                c = 1;
                while (from + c < 64 && x + c < 8 && board[from + c].isEmpty) {
                    list.add(from + c);
                    c++;
                }
                if (from + c < 64 && x + c < 8 && !board[from + c].isEmpty && !sameColor(pieceOnHold, board[from + c])) {
                    list.add(from + c);
                }
                break;
            case "king":
                if (from - 1 >= 0 && x - 1 >= 0 && diffColorOrEmpty(pieceOnHold, board[from - 1])) {
                    list.add(from - 1);
                }
                if (from - 9 >= 0 && x - 1 >= 0 && diffColorOrEmpty(pieceOnHold, board[from - 9])) {
                    list.add(from - 9);
                }
                if (from - 8 >= 0 && diffColorOrEmpty(pieceOnHold, board[from - 8])) {
                    list.add(from - 8);
                }
                if (from - 7 >= 0 && x + 1 < 8 && diffColorOrEmpty(pieceOnHold, board[from - 7])) {
                    list.add(from - 7);
                }
                if (from + 1 < 64 && x + 1 < 8 && diffColorOrEmpty(pieceOnHold, board[from + 1])) {
                    list.add(from + 1);
                }
                if (from + 9 < 64 && x + 1 < 8 && diffColorOrEmpty(pieceOnHold, board[from + 9])) {
                    list.add(from + 9);
                }
                if (from + 8 < 64 && diffColorOrEmpty(pieceOnHold, board[from + 8])) {
                    list.add(from + 8);
                }
                if (from + 7 < 64 && x - 1 >= 0 && diffColorOrEmpty(pieceOnHold, board[from + 7])) {
                    list.add(from + 7);
                }
        }
        return list;
    }

    public boolean diffColorOrEmpty(Piece piece, Tile to) {
        return to.isEmpty || !piece.getColor().equals(to.piece.getColor());
    }

    public boolean sameColor(Piece piece, Tile to) {
        return piece.getColor().equals(to.piece.getColor());
    }

}