package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by peter on 30/06/16.
 */
public class TripItemAdapter extends RecyclerView.Adapter<TripItemAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private Context context;

    public TripItemAdapter(List<Trip> tripList, Context context) {
        this.tripList = tripList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder (TripItemAdapter.TripViewHolder holder, int position) {
        Trip currTrip = tripList.get(position);
        holder.tripText.setText(currTrip.getName());
    }

    @Override
    public TripItemAdapter.TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        protected TextView tripText;

        public TripViewHolder(View v) {
            super(v);
            tripText = (TextView) v.findViewById(R.id.textView_tripText);
        }
    }
}
