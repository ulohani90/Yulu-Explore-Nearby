package in.test.yuluexploreplaces.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;

@Entity(tableName = "place_model")
public class PlaceModel {

    @SerializedName("venue")
    Venue venue;


    public Venue getVenue() {
        return venue;
    }


}
