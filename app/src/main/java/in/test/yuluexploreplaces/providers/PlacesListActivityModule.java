package in.test.yuluexploreplaces.providers;


import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import androidx.room.PrimaryKey;
import dagger.Module;
import dagger.Provides;
import in.test.yuluexploreplaces.activities.PlacesListActivity;
import in.test.yuluexploreplaces.adapters.PlacesRVAdapter;
import in.test.yuluexploreplaces.presenters.PlacesListActivityPresenter;
import in.test.yuluexploreplaces.usecases.ExploreNearbyPlacesUseCase;
import in.test.yuluexploreplaces.usecases.InsertPlacesInDBUseCase;
import in.test.yuluexploreplaces.usecases.PlacesFromDBUseCase;
import in.test.yuluexploreplaces.usecases.SearchNearbyPlacesUseCase;
import retrofit2.Retrofit;

@Module(includes = {APIModule.class})
public class PlacesListActivityModule {

    private final PlacesListActivity placesListActivity;

    public PlacesListActivityModule(PlacesListActivity placesListActivity) {
        this.placesListActivity = placesListActivity;
    }

    @Provides
    PlacesListActivityPresenter provideHomeActivityPresenter() {
        return new PlacesListActivityPresenter(placesListActivity);
    }

    @Provides
    PlacesRVAdapter providesPlacesRVAdapter() {
        return new PlacesRVAdapter(placesListActivity);
    }

    @Provides
    FusedLocationProviderClient providesFusedLocationProviderClient() {
        return LocationServices.getFusedLocationProviderClient(placesListActivity);
    }

    @Provides
    ExploreNearbyPlacesUseCase providesExploreNearbyPlacesUseCase(Retrofit retrofit) {
        return new ExploreNearbyPlacesUseCase(retrofit);
    }

    @Provides
    SearchNearbyPlacesUseCase providesSearchNearbyPlacesUseCase(Retrofit retrofit) {
        return new SearchNearbyPlacesUseCase(retrofit);
    }

    @Provides
    PlacesFromDBUseCase providesPlacesFromDBUseCase() {
        return new PlacesFromDBUseCase(placesListActivity.getApplicationContext());
    }

    @Provides
    InsertPlacesInDBUseCase providesInsertPlacesInDBUseCase() {
        return new InsertPlacesInDBUseCase(placesListActivity.getApplicationContext());
    }

}
