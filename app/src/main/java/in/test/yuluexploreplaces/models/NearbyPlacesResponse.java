package in.test.yuluexploreplaces.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbyPlacesResponse {

    @SerializedName("response")
    NearbyPlaces response;

    public NearbyPlaces getResponse() {
        return response;
    }

    public class NearbyPlaces {
        @SerializedName("groups")
        List<NearbyPlacesGroup> groups;

        public List<NearbyPlacesGroup> getGroups() {
            return groups;
        }
    }

    public class NearbyPlacesGroup {
        @SerializedName("items")
        List<PlaceModel> items;

        public List<PlaceModel> getItems() {
            return items;
        }
    }
}
