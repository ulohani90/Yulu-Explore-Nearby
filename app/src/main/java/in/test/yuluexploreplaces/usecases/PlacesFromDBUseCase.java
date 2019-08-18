package in.test.yuluexploreplaces.usecases;

import android.content.Context;

import java.util.List;

import in.test.yuluexploreplaces.contracts.VenuesDatabase;
import in.test.yuluexploreplaces.models.Venue;
import in.test.yuluexploreplaces.observers.VenueListObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PlacesFromDBUseCase {

    Context mContext;

    DisposableObserver<List<Venue>> disposable;

    public PlacesFromDBUseCase(Context context) {
        this.mContext = context;
    }

    public void execute(DisposableObserver<List<Venue>> venueListObserver) {
        disposable = VenuesDatabase.getInstance(mContext).venuesDaoObject().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(venueListObserver);

    }

    public void unsubscribe() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
