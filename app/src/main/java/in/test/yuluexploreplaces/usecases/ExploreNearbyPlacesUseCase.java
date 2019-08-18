package in.test.yuluexploreplaces.usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.test.yuluexploreplaces.models.NearbyPlacesResponse;
import in.test.yuluexploreplaces.models.PlaceModel;
import in.test.yuluexploreplaces.models.Venue;
import in.test.yuluexploreplaces.observers.VenueListObserver;
import in.test.yuluexploreplaces.utils.ApiClass;
import in.test.yuluexploreplaces.utils.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ExploreNearbyPlacesUseCase {

    Retrofit mRetroFit;

    String section;

    double lat;

    double lng;

    DisposableObserver<List<Venue>> disposable;

    public ExploreNearbyPlacesUseCase(Retrofit retrofit) {
        this.mRetroFit = retrofit;
    }

    public void setData(String section, double lat, double lng) {
        this.section = section;
        this.lat = lat;
        this.lng = lng;
    }


    public void execute(VenueListObserver observer) {
        disposable = mRetroFit.create(ApiClass.class)
                .getNearbyPlaces(Constants.CLIENT_ID, Constants.CLIENT_SECRET, section, lat + "," + lng, Constants.getDateInVersionFormat())
                .subscribeOn(Schedulers.io())
                .debounce(2000, TimeUnit.MILLISECONDS)
                .timeout(120, TimeUnit.SECONDS)
                .cache()
                .retry()

                .map(nearbyPlacesResponse -> {
                    List<Venue> venues = new ArrayList<>();
                    if (nearbyPlacesResponse != null && nearbyPlacesResponse.getResponse() != null && nearbyPlacesResponse.getResponse().getGroups() != null
                            && nearbyPlacesResponse.getResponse().getGroups().size() > 0) {
                        for (NearbyPlacesResponse.NearbyPlacesGroup group : nearbyPlacesResponse.getResponse().getGroups()) {
                            for (PlaceModel model : group.getItems()) {
                                venues.add(model.getVenue());
                            }
                        }
                    }
                    return venues;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }

    public void unsubscribe() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}
