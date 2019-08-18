package in.test.yuluexploreplaces.utils;

import in.test.yuluexploreplaces.models.NearbyPlacesResponse;
import in.test.yuluexploreplaces.models.SearchNearbyPlacesResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiClass {

    @GET("/v2/venues/explore")
    Observable<NearbyPlacesResponse>
    getNearbyPlaces(@Query("client_id") String clientId, @Query("client_secret") String clientSecret,
                    @Query("section") String section,
                    @Query("ll") String latlngString, @Query("v") String dateVersion);


    @GET("/v2/venues/search")
    Observable<SearchNearbyPlacesResponse>
    searchNearbyPlaces(@Query("client_id") String clientId, @Query("client_secret") String clientSecret,
                       @Query("query") String query,
                       @Query("ll") String latlngString, @Query("v") String dateVersion);
}
