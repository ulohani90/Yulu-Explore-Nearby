package in.test.yuluexploreplaces.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;
import in.test.yuluexploreplaces.R;
import in.test.yuluexploreplaces.adapters.PlacesRVAdapter;

import in.test.yuluexploreplaces.components.DaggerPlacesListActivityComponent;
import in.test.yuluexploreplaces.components.PlacesListActivityComponent;
import in.test.yuluexploreplaces.contracts.PlacesListActivityContract;
import in.test.yuluexploreplaces.models.Venue;
import in.test.yuluexploreplaces.presenters.PlacesListActivityPresenter;
import in.test.yuluexploreplaces.providers.APIModule;
import in.test.yuluexploreplaces.providers.PlacesListActivityModule;
import in.test.yuluexploreplaces.usecases.ExploreNearbyPlacesUseCase;
import in.test.yuluexploreplaces.usecases.InsertPlacesInDBUseCase;
import in.test.yuluexploreplaces.usecases.PlacesFromDBUseCase;
import in.test.yuluexploreplaces.usecases.SearchNearbyPlacesUseCase;
import in.test.yuluexploreplaces.utils.Constants;
import retrofit2.Retrofit;

import static in.test.yuluexploreplaces.utils.Constants.REQUEST_LOCATION_PERMISSION;

public class PlacesListActivity extends AppCompatActivity implements PlacesListActivityContract.IPlacesListActivityView, OnMapReadyCallback {


    @Inject
    Retrofit mRetrofit;

    @Inject
    PlacesRVAdapter mAdapter;

    @Inject
    PlacesListActivityPresenter mPresenter;

    @Inject
    FusedLocationProviderClient fusedLocationProviderClient;

    @Inject
    ExploreNearbyPlacesUseCase nearbyPlacesUseCase;

    @Inject
    SearchNearbyPlacesUseCase searchNearbyPlacesUseCase;

    @Inject
    PlacesFromDBUseCase placesFromDBUseCase;

    @Inject
    InsertPlacesInDBUseCase insertPlacesInDBUseCase;

    @BindView(R.id.places_rv)
    RecyclerView placesRV;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.map_view)
    MapView mapView;

    @BindView(R.id.error_message_text)
    TextView errorMessageText;

    @BindView(R.id.refresh_parent)
    View errorRefreshLayout;

    @BindView(R.id.empty_layout_parent)
    View emptyLayout;

    @BindView(R.id.null_case_tv)
    TextView emptyLayoutTV;

    @BindView(R.id.collapsingHeader)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.header_title)
    TextView headerTitle;

    @BindView(R.id.food_tv)
    TextView foodTv;

    @BindView(R.id.trending_tv)
    TextView trendingTv;

    @BindView(R.id.shops_tv)
    TextView shopsTv;

    @BindView(R.id.coffee_tv)
    TextView coffeeTv;

    @BindView(R.id.arts_tv)
    TextView artsTv;

    @BindView(R.id.sights_tv)
    TextView sightsTv;

    @BindView(R.id.drinks_tv)
    TextView drinksTv;

    @BindView(R.id.outdoors_tv)
    TextView outdoorsTv;

    @BindString(R.string.loc_req_heading)
    String locationReqHeading;

    @BindString(R.string.loc_req_message)
    String locationReqMessage;

    @BindString(R.string.go_to_settings)
    String goToSettingsBtn;

    @BindString(R.string.add_permission)
    String addPermission;

    @BindString(R.string.location_turned_off)
    String locationTurnedoff;

    @BindString(R.string.enable_loc_for_best_experience)
    String enableLocForBestExperience;

    @BindString(R.string.retry_text)
    String retry;

    @BindString(R.string.loading_location_text)
    String loadingLocation;

    @BindString(R.string.loading_nearby_places)
    String loadingNearbyPlaces;

    @BindString(R.string.explore_places)
    String explorePlaces;

    @BindString(R.string.cancel)
    String cancel;

    @BindString(R.string.no_place_text)
    String noPlacesText;

    @BindString(R.string.no_internet_connection)
    String noInternetConnection;

    int isLocationCheckedCount = 0;

    int screenHeight;

    boolean isMapViewShown = false;

    String currentQuery;

    boolean isExploring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        PlacesListActivityComponent component = DaggerPlacesListActivityComponent.builder()
                .aPIModule(new APIModule())
                .placesListActivityModule(new PlacesListActivityModule(this))
                .build();
        component.injectPlacesListActivity(this);
        mPresenter.setUpMVPView(this);
        mPresenter.setUpLayout();
        initUseCases();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        fab.setOnClickListener(v -> {
            if (isMapViewShown) {
                searchView.setVisibility(View.VISIBLE);
                mPresenter.showPlacesList();
                fab.hide();
                fab.setImageResource(android.R.drawable.ic_dialog_map);
                fab.show();
            } else {
                if (mAdapter.getVenues() != null && mAdapter.getVenues().size() > 0) {
                    searchView.setVisibility(View.GONE);
                    fab.hide();
                    fab.setImageResource(R.drawable.ic_list);
                    fab.show();
                    mPresenter.showMapView();
                }
            }
        });
        errorRefreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExploring) {
                    mPresenter.detectLocation(fusedLocationProviderClient, currentQuery);
                } else {
                    mPresenter.exploreNearbyPlaces(currentQuery);
                }
            }
        });
        mapView.onCreate(savedInstanceState);
        currentQuery = "topPicks";
        isExploring = true;
        mPresenter.detectLocation(fusedLocationProviderClient, currentQuery);
        handleIntent(getIntent());
    }

    private void initUseCases() {
        mPresenter.setNearbyPlacesUseCase(nearbyPlacesUseCase);
        mPresenter.setSearchNearbyPlacesUseCase(searchNearbyPlacesUseCase);
        mPresenter.setPlacesFromDBUseCase(placesFromDBUseCase);
        mPresenter.setInsertPlacesInDBUseCase(insertPlacesInDBUseCase);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchView.setIconified(true);
            searchView.setIconified(true);
            String query = intent.getStringExtra(SearchManager.QUERY);
            currentQuery = query;
            isExploring = false;
            mPresenter.searchNearbyPlaces(query);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    SearchView searchView;

    MenuItem searchItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);
        searchView =
                (SearchView) searchItem.getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        final int textViewID = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(textViewID);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUpAllViews() {
        placesRV.setLayoutManager(new LinearLayoutManager(this));
        placesRV.setAdapter(mAdapter);
        foodTv.setOnClickListener(onClickListener);
        trendingTv.setOnClickListener(onClickListener);
        coffeeTv.setOnClickListener(onClickListener);
        artsTv.setOnClickListener(onClickListener);
        shopsTv.setOnClickListener(onClickListener);
        sightsTv.setOnClickListener(onClickListener);
        outdoorsTv.setOnClickListener(onClickListener);
        drinksTv.setOnClickListener(onClickListener);
    }

    @Override
    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void showLoading() {
        fab.hide();
        mapView.setVisibility(View.GONE);
        placesRV.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        errorRefreshLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        collapsingToolbarLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        errorRefreshLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        collapsingToolbarLayout.setVisibility(View.VISIBLE);
        placesRV.setVisibility(View.VISIBLE);
        if (isMapViewShown) {
            fab.hide();
            fab.setImageResource(android.R.drawable.ic_dialog_map);
            fab.show();
            slideRecyclerViewIn();
        }
        fab.show();
    }

    @Override
    public void setErrorView(String errorMessage) {
        fab.hide();
        progressBar.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        placesRV.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        errorRefreshLayout.setVisibility(View.VISIBLE);
        errorMessageText.setText(errorMessage);
        collapsingToolbarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyView(String emptyMessage) {
        fab.hide();
        progressBar.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        mAdapter.clearData();
        placesRV.setVisibility(View.GONE);
        errorRefreshLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
        collapsingToolbarLayout.setVisibility(View.VISIBLE);
        emptyLayoutTV.setText(emptyMessage);
    }


    @Override
    public void setPlacesDataToRecyclerView(List<Venue> venues, String query, boolean saveToDB) {

        if (Constants.isInternetConnected(this)) {
            fab.show();
            hideSnackBar();
        } else {
            showSnackbar(noInternetConnection);
            fab.hide();
        }
        headerTitle.setText(Constants.getQueryFormatted(query) + " (Showing " + venues.size() + " results)");
        if (venues.size() > 0) {
            mAdapter.setVenues(venues);
            if (saveToDB) {
                mPresenter.insertPlacesInDB(venues);
            }
        } else {
            showEmptyView(noPlacesText);
        }

    }

    Snackbar snackbar;

    @Override
    public void showSnackbar(String message) {
        snackbar = Snackbar.make(placesRV, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(retry, v -> {
            if (isExploring) {
                mPresenter.detectLocation(fusedLocationProviderClient, currentQuery);
            } else {
                mPresenter.searchNearbyPlaces(currentQuery);
            }
        });
        snackbar.show();
    }

    @Override
    public void hideSnackBar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0) {
                    isLocationCheckedCount = 1;
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mPresenter.detectLocation(fusedLocationProviderClient, currentQuery);
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        showGiveLocationPermissionDialog();
                    }
                }
        }
    }

    private void showGiveLocationPermissionDialog() {
        setErrorView("Location permission is required. Please enable it from App settings");
        new AlertDialog.Builder(this).setTitle(locationReqHeading).setMessage(locationReqMessage)
                .setPositiveButton(addPermission, (dialog, which) -> moveToAppPermissionScreen())
                .setNegativeButton(retry, (dialog, which) -> {
                    isLocationCheckedCount = 0;
                    mPresenter.detectLocation(fusedLocationProviderClient, currentQuery);
                })
                .show();
    }

    private void moveToAppPermissionScreen() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void moveToLocationSettingsScreen() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    @Override
    public void slideRecyclerViewOut() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(placesRV, "translationY", 0, screenHeight);
        anim.setDuration(300);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPresenter.startMapInitializing();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @Override
    public void slideRecyclerViewIn() {
        isMapViewShown = false;
        ObjectAnimator anim = ObjectAnimator.ofFloat(placesRV, "translationY", screenHeight, 0);
        anim.setDuration(300);
        anim.start();
    }

    @Override
    public void showEnableLocationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(locationTurnedoff)
                .setMessage(enableLocForBestExperience)
                .setPositiveButton(goToSettingsBtn, (dialog, which) -> {
                    dialog.dismiss();
                    moveToLocationSettingsScreen();
                }).setNegativeButton(cancel, (dialog, which) -> {
            dialog.dismiss();
        }).show();
    }

    @Override
    public void initializeMap() {
        isMapViewShown = true;
        mapView.setVisibility(View.VISIBLE);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMinZoomPreference(13);
        googleMap.clear();
        List<Venue> venues = mAdapter.getVenues();
        for (Venue venue : venues) {
            MarkerOptions options = new MarkerOptions();
            LatLng latlng = new LatLng(venue.getLocation().getLat(), venue.getLocation().getLng());
            options.position(latlng);
            options.title(venue.getName());
            options.snippet(venue.getLocation().getDistanceString());
            googleMap.addMarker(options);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mPresenter.getUserLat(), mPresenter.getUserLng())));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        isLocationCheckedCount = 0;
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        nearbyPlacesUseCase.unsubscribe();
        searchNearbyPlacesUseCase.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isMapViewShown) {
            fab.hide();
            fab.setImageResource(android.R.drawable.ic_dialog_map);
            fab.show();
            slideRecyclerViewIn();
        } else {
            super.onBackPressed();
        }
    }

    View.OnClickListener onClickListener = v -> {
        String section = null;
        switch (v.getId()) {
            case R.id.food_tv:
                section = "Food";
                break;
            case R.id.drinks_tv:
                section = "Drinks";
                break;
            case R.id.shops_tv:
                section = "Shops";
                break;
            case R.id.trending_tv:
                section = "Trending";
                break;
            case R.id.arts_tv:
                section = "Arts";
                break;
            case R.id.outdoors_tv:
                section = "Outdoors";
                break;
            case R.id.sights_tv:
                section = "Sights";
                break;
            case R.id.coffee_tv:
                section = "Coffee";
                break;
            default:
                section = "Top picks";
                break;
        }
        isExploring = false;
        currentQuery = section;
        mPresenter.exploreNearbyPlaces(section);
    };
}
