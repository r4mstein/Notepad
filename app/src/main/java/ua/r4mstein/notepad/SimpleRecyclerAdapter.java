package ua.r4mstein.notepad;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.SimpleViewHolder> {

    private List<String> mFileNames = new ArrayList<>();

    public SimpleRecyclerAdapter(List<String> fileNames) {
        mFileNames = fileNames;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item, parent, false);
        SimpleViewHolder viewHolder = new SimpleViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.mTextView.setText(mFileNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mFileNames == null ? 0 : mFileNames.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView mTextView;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.itemCardView);
            mTextView = (TextView) itemView.findViewById(R.id.tvFileName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
