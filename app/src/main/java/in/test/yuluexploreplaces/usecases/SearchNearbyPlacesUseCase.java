package in.test.yuluexploreplaces.usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.test.yuluexploreplaces.models.SearchNearbyPlacesResponse;
import in.test.yuluexploreplaces.models.Venue;
import in.test.yuluexploreplaces.utils.ApiClass;
import in.test.yuluexploreplaces.utils.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SearchNearbyPlacesUseCase {
    Retrofit mRetroFit;

    String query;

    double lat;

    double lng;

    DisposableObserver<List<Venue>> disposable;

    public SearchNearbyPlacesUseCase(Retrofit retrofit) {
        this.mRetroFit = retrofit;
    }

    public void setData(String query, double lat, double lng) {
        this.query = query;
        this.lat = lat;
        this.lng = lng;
    }


    public void execute(DisposableObserver<List<Venue>> observer) {
        disposable = mRetroFit.create(ApiClass.class)
                .searchNearbyPlaces(Constants.CLIENT_ID, Constants.CLIENT_SECRET, query, lat + "," + lng, Constants.getDateInVersionFormat())
                .subscribeOn(Schedulers.io())
                .debounce(2000, TimeUnit.MILLISECONDS)
                .timeout(120, TimeUnit.SECONDS)
                .cache()
                .retry()
                .map(new Function<SearchNearbyPlacesResponse, List<Venue>>() {
                    @Override
                    public List<Venue> apply(SearchNearbyPlacesResponse searchNearbyPlacesResponse) throws Exception {
                        List<Venue> venues = new ArrayList<>();
                        if (searchNearbyPlacesResponse != null && searchNearbyPlacesResponse.getResponse() != null && searchNearbyPlacesResponse.getResponse().getVenues() != null) {
                            venues.addAll(searchNearbyPlacesResponse.getResponse().getVenues());
                        }
                        return venues;
                    }
                })
                //.onErrorResumeNext(CommonApi.refreshTokenAndRetry(getObservable()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }

    public void unsubscribe() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
