package in.test.yuluexploreplaces.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchNearbyPlacesResponse {

    @SerializedName("response")
    SearchPlacesResponse response;

    public SearchPlacesResponse getResponse() {
        return response;
    }

    public class SearchPlacesResponse {
        @SerializedName("venues")
        List<Venue> venues;

        public List<Venue> getVenues() {
            return venues;
        }
    }
}
