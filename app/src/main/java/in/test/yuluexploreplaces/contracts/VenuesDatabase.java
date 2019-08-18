package in.test.yuluexploreplaces.contracts;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import in.test.yuluexploreplaces.models.Venue;

@Database(entities = Venue.class, version = 1)
public abstract class VenuesDatabase extends RoomDatabase {

    public abstract VenuesDaoObject venuesDaoObject();

    private static VenuesDatabase instance;

    public static VenuesDatabase getInstance(Context mContext) {
        if (instance == null) {
            instance = Room.databaseBuilder(mContext.getApplicationContext(), VenuesDatabase.class, "venues_db")
                    .build();
        }

        return instance;
    }
}
