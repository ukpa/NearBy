package me.unnikrishnanpatel.nearby;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.realm.RealmResults;
import me.unnikrishnanpatel.nearby.data.Place;

/**
 * Created by unnikrishnanpatel on 20/05/16.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private RealmResults<Place> mDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView distance;
        public TextView category;
        public ImageView image;
        public TextView now;

        public ViewHolder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.name);
            distance = (TextView)v.findViewById(R.id.distance);
            category = (TextView)v.findViewById(R.id.category);
            image = (ImageView) v.findViewById(R.id.image);
            now = (TextView)v.findViewById(R.id.now);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DataAdapter(RealmResults<Place> myDataset,Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.setText(mDataset.get(position).getName());
        holder.category.setText(mDataset.get(position).getCategory());
        Picasso.with(mContext).load(mDataset.get(position).getIcon_url()).into(holder.image);
        holder.distance.setText(String.valueOf(mDataset.get(position).getDistance()));
        holder.now.setText(String.valueOf(mDataset.get(position).isNow()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset==null){
            return 0;
        }
        return mDataset.size();
    }
}


