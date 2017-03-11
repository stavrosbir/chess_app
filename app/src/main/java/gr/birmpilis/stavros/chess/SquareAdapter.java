package gr.birmpilis.stavros.chess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by stavros on 6/11/2015.
 */
public class SquareAdapter extends BaseAdapter {

    int bimg[] = {R.drawable.white, R.drawable.grey};
    Context context;

    public SquareAdapter(Context c) {
        context = c;
    }

    @Override
    public int getCount() {
        return 64;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View squareContainerView = convertView;
        if (convertView == null) {
            //Inflate the layout
            final LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            squareContainerView = layoutInflater.inflate(R.layout.square, null);

            // Background
            if (Board.hold && Board.pieceOnHold.getPosition() == position) {
                final ImageView squareView = (ImageView) squareContainerView.findViewById(R.id.square_light);
                squareView.setImageResource(R.drawable.border);
            }
            if(Board.hold && Board.glist.contains(position)) {
                final ImageView squareView = (ImageView) squareContainerView.findViewById(R.id.square_light);
                squareView.setImageResource(R.drawable.target1);
            }
            final ImageView squareView = (ImageView) squareContainerView.findViewById(R.id.square_background);
            squareView.setImageResource(bimg[(position + position / 8) % 2]);

            /*for (Piece piece : Board.pieces) {
                if (position == piece.getPosition()) {
                    final ImageView pieceView = (ImageView) squareContainerView.findViewById(R.id.piece);
                    pieceView.setImageResource(piece.getResource());
                    pieceView.setTag(position);
                }
            }*/
            if(Board.board[position]!=null && !Board.board[position].isEmpty) {
                final ImageView pieceView = (ImageView) squareContainerView.findViewById(R.id.piece);
                pieceView.setImageResource(Board.board[position].piece.getResource());
                pieceView.setTag(position);
            }
            squareContainerView.setLayoutParams(new GridView.LayoutParams(85, 85));
        }
        return squareContainerView;
    }
}
