package in.test.yuluexploreplaces.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.test.yuluexploreplaces.R;
import in.test.yuluexploreplaces.models.Venue;

public class PlacesRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;

    List<Venue> venues;


    public PlacesRVAdapter(Context context) {
        this.mContext = context;
    }


    public void setVenues(List<Venue> venues) {
        this.venues = venues;
        notifyDataSetChanged();
    }

    public List<Venue> getVenues() {
        return venues;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.places_list_item_layout, viewGroup, false);
        PlaceViewHolder holder = new PlaceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        PlaceViewHolder holder = (PlaceViewHolder) viewHolder;
        Venue venue = venues.get(position);
        holder.address.setText(venue.getLocation().getFormattedAddressString());
        holder.name.setText(venue.getName());
        holder.distance.setText(venue.getLocation().getDistanceString());
        holder.categoriesText.setText(venue.getCategoryString());

    }

    @Override
    public int getItemCount() {

        return venues != null ? venues.size() : 0;
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView distance;
        TextView address;
        TextView categoriesText;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.hotel_name);
            distance = (TextView) itemView.findViewById(R.id.hotel_distance);
            address = (TextView) itemView.findViewById(R.id.hotel_address);
            categoriesText = (TextView) itemView.findViewById(R.id.categories_text);
        }
    }

    public void clearData() {
        if (venues != null) {
            venues.clear();
        }
        notifyDataSetChanged();
    }
}
