package in.test.yuluexploreplaces.usecases;

import android.content.Context;
import android.util.Log;

import java.util.List;

import in.test.yuluexploreplaces.contracts.VenuesDatabase;
import in.test.yuluexploreplaces.models.Venue;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class InsertPlacesInDBUseCase {

    Context mContext;
    CompositeDisposable compositeDisposable;

    public InsertPlacesInDBUseCase(Context context) {
        this.mContext = context;
        compositeDisposable = new CompositeDisposable();
    }

    public Completable deleteAll() {
        return Completable.fromAction(() ->
                VenuesDatabase.getInstance(mContext).venuesDaoObject().deleteAll())
                .subscribeOn(Schedulers.io());

    }

    public void insertAll(List<Venue> venues) {
        compositeDisposable.add(deleteAll()
                .andThen(Completable.fromAction(() -> {
                    VenuesDatabase.getInstance(mContext).venuesDaoObject().insertAll(venues);
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())).subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.i(InsertPlacesInDBUseCase.class.getCanonicalName(), "Insert complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(InsertPlacesInDBUseCase.class.getCanonicalName(), "Insert Error");
                    }
                }));

    }


    public void unsubscribe() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}
