package in.test.yuluexploreplaces.contracts;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import in.test.yuluexploreplaces.models.Venue;
import io.reactivex.Observable;

@Dao
public interface VenuesDaoObject {

    @Insert
    void insertAll(List<Venue> venues);

    @Query("SELECT * FROM VenueTable")
    Observable<List<Venue>> getAll();

    @Query("DELETE  FROM VenueTable")
    void deleteAll();
}
