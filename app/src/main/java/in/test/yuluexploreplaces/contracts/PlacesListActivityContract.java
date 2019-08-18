package in.test.yuluexploreplaces.contracts;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;

import in.test.yuluexploreplaces.models.Venue;
import in.test.yuluexploreplaces.usecases.ExploreNearbyPlacesUseCase;
import in.test.yuluexploreplaces.usecases.InsertPlacesInDBUseCase;
import in.test.yuluexploreplaces.usecases.PlacesFromDBUseCase;
import in.test.yuluexploreplaces.usecases.SearchNearbyPlacesUseCase;

public class PlacesListActivityContract {

    public interface IPlacesListActivityPresenter {
        void setUpMVPView(IPlacesListActivityView view);

        void setUpLayout();

        void detectLocation(FusedLocationProviderClient fusedLocationProviderClient, String section);

        void detectLocationSuccess(Location location, String section);

        void setNearbyPlacesUseCase(ExploreNearbyPlacesUseCase nearbyPlacesUseCase);

        void setSearchNearbyPlacesUseCase(SearchNearbyPlacesUseCase searchNearbyPlacesUseCase);

        void setPlacesFromDBUseCase(PlacesFromDBUseCase placesFromDBUseCase);

        void setInsertPlacesInDBUseCase(InsertPlacesInDBUseCase insertPlacesInDBUseCase);

        void showPlacesList();

        void showMapView();

        void insertPlacesInDB(List<Venue> venues);

        void searchNearbyPlaces(String query);

        void exploreNearbyPlaces(String section);

        void loadPlacesFromDB();

        void startMapInitializing();
    }


    public interface IPlacesListActivityView {
        void setUpAllViews();

        void requestLocationPermission();

        void showLoading();

        void hideLoading();

        void setPlacesDataToRecyclerView(List<Venue> venues, String query, boolean saveToDB);

        void setErrorView(String errorMessage);

        void slideRecyclerViewOut();

        void slideRecyclerViewIn();

        void showEnableLocationDialog();

        void showEmptyView(String emptyMessage);

        void initializeMap();

        void showSnackbar(String message);

        void hideSnackBar();

    }


}
