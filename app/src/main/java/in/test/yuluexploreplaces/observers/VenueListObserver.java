package in.test.yuluexploreplaces.observers;


import java.util.List;

import in.test.yuluexploreplaces.contracts.PlacesListActivityContract;
import in.test.yuluexploreplaces.models.Venue;
import io.reactivex.observers.DisposableObserver;

public class VenueListObserver extends DisposableObserver<List<Venue>> {


    PlacesListActivityContract.IPlacesListActivityView mView;


    boolean mSaveToDB;

    String mQuery;

    public VenueListObserver(PlacesListActivityContract.IPlacesListActivityView view, String query, boolean saveToDB) {
        this.mView = view;
        this.mQuery = query;
        this.mSaveToDB = saveToDB;
    }

    @Override
    public void onNext(List<Venue> venues) {
        mView.hideLoading();
        mView.setPlacesDataToRecyclerView(venues, mQuery, mSaveToDB);
    }

    @Override
    public void onError(Throwable e) {
        mView.setErrorView(e.getMessage());
    }

    @Override
    public void onComplete() {

    }
}
