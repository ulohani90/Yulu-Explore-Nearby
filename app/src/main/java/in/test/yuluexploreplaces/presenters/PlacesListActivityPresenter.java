package in.test.yuluexploreplaces.presenters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.List;

import androidx.core.app.ActivityCompat;
import in.test.yuluexploreplaces.contracts.PlacesListActivityContract;
import in.test.yuluexploreplaces.models.Venue;
import in.test.yuluexploreplaces.observers.VenueListObserver;
import in.test.yuluexploreplaces.usecases.ExploreNearbyPlacesUseCase;
import in.test.yuluexploreplaces.usecases.InsertPlacesInDBUseCase;
import in.test.yuluexploreplaces.usecases.PlacesFromDBUseCase;
import in.test.yuluexploreplaces.usecases.SearchNearbyPlacesUseCase;
import in.test.yuluexploreplaces.utils.Constants;
import io.reactivex.observers.DisposableObserver;


public class PlacesListActivityPresenter implements PlacesListActivityContract.IPlacesListActivityPresenter {

    String TAG = PlacesListActivityPresenter.class.getCanonicalName();

    PlacesListActivityContract.IPlacesListActivityView mView;

    Context mContext;

    LocationCallback locationCallback;

    double userLat;

    double userLng;

    FusedLocationProviderClient mFusedLocationProviderClient;

    ExploreNearbyPlacesUseCase mNearbyPlacesUseCase;

    SearchNearbyPlacesUseCase mSearchNearbyPlacesUseCase;

    PlacesFromDBUseCase mPlacesFromDBUseCase;

    InsertPlacesInDBUseCase mInsertPlacesInDBUseCase;

    public PlacesListActivityPresenter(Context context) {
        this.mContext = context;
    }

    @Override
    public void setUpMVPView(PlacesListActivityContract.IPlacesListActivityView mView) {
        this.mView = mView;
    }

    @Override
    public void setUpLayout() {
        if (mView != null) {
            mView.setUpAllViews();
        }
    }

    public void setFusedLocationProviderClient(FusedLocationProviderClient fusedLocationProviderClient) {
        this.mFusedLocationProviderClient = fusedLocationProviderClient;
    }

    @Override
    public void detectLocation(String section, boolean isExplore) {

        if (checkLocationPermission()) {
            mView.requestLocationPermission();
        } else {
            mView.showLoading();
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    if (!isLocationEnabled()) {
                        mView.showEnableLocationDialog();
                    }
                    detectLocationSuccess(location, section, isExplore);
                } else {
                    if (isLocationEnabled())
                        startLocationUpdate(section, isExplore);
                    else {
                        mView.setErrorView("Location could not be found. Please enable location settings and retry");
                        mView.showEnableLocationDialog();
                    }
                }
            });
        }

    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public double getUserLat() {
        return userLat;
    }

    public double getUserLng() {
        return userLng;
    }

    @Override
    public void detectLocationSuccess(Location location, String section, boolean isExplore) {
        if (locationCallback != null && mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        userLat = location.getLatitude();
        userLng = location.getLongitude();
        if (isExplore) {
            exploreNearbyPlaces(section);
        } else {
            searchNearbyPlaces(section);
        }
    }

    @Override
    public void exploreNearbyPlaces(String section) {
        if (!Constants.isInternetConnected(mContext)) {
            mView.showSnackbar("No Internet Connection");
            mView.setErrorView("No network available. Connect to internet and try again.");
        } else {
            if (userLat == 0 && userLng == 0) {
                detectLocation(section, true);
            } else {
                mView.showLoading();
                mNearbyPlacesUseCase.setData(section, userLat, userLng);
                mNearbyPlacesUseCase.execute(provideVenueListObserver(section, true));
            }
        }
    }

    @Override
    public void searchNearbyPlaces(String query) {
        if (!Constants.isInternetConnected(mContext)) {
            mView.showSnackbar("No Internet Connection");
            mView.setErrorView("No network available. Connect to internet and try again.");
        } else {
            if (userLat == 0 && userLng == 0) {
                detectLocation(query, false);
            } else {
                mView.showLoading();
                mSearchNearbyPlacesUseCase.setData(query, userLat, userLng);
                mSearchNearbyPlacesUseCase.execute(provideVenueListObserver(query, true));
            }
        }
    }

    @Override
    public void loadPlacesFromDB() {
        mView.showLoading();
        mPlacesFromDBUseCase.execute(provideVenueListObserver("Top Picks", false));
    }

    private VenueListObserver provideVenueListObserver(String query, boolean saveToDB) {
        VenueListObserver observer = new VenueListObserver(mView, query, saveToDB);
        return observer;
    }


    private void startLocationUpdate(String query, boolean isExplore) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(20 * 1000);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            detectLocationSuccess(location, query, isExplore);
                        }
                    }

                }
            };
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    public boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void setNearbyPlacesUseCase(ExploreNearbyPlacesUseCase nearbyPlacesUseCase) {
        this.mNearbyPlacesUseCase = nearbyPlacesUseCase;
    }

    @Override
    public void setPlacesFromDBUseCase(PlacesFromDBUseCase placesFromDBUseCase) {
        this.mPlacesFromDBUseCase = placesFromDBUseCase;
    }

    @Override
    public void setInsertPlacesInDBUseCase(InsertPlacesInDBUseCase insertPlacesInDBUseCase) {
        this.mInsertPlacesInDBUseCase = insertPlacesInDBUseCase;
    }

    @Override
    public void showPlacesList() {
        mView.slideRecyclerViewIn();
    }

    @Override
    public void showMapView() {
        mView.slideRecyclerViewOut();
    }

    @Override
    public void insertPlacesInDB(List<Venue> venues) {
        mInsertPlacesInDBUseCase.insertAll(venues);
    }

    @Override
    public void setSearchNearbyPlacesUseCase(SearchNearbyPlacesUseCase searchNearbyPlacesUseCase) {
        mSearchNearbyPlacesUseCase = searchNearbyPlacesUseCase;
    }


    @Override
    public void startMapInitializing() {
        mView.initializeMap();
    }


}
