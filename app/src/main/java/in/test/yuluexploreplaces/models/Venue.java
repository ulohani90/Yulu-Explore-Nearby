package in.test.yuluexploreplaces.models;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import in.test.yuluexploreplaces.converters.AddressTypeConverter;
import in.test.yuluexploreplaces.converters.DataTypeConverter;

@Entity(tableName = "VenueTable")
public class Venue {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @TypeConverters(DataTypeConverter.class)
    @SerializedName("categories")
    public List<Category> categories;

    @Embedded
    @SerializedName("location")
    public Location location;

    public List<Category> getCategories() {
        return categories;
    }

    public String getCategoryString() {
        StringBuilder builder = new StringBuilder();
        if (categories == null) {
            return builder.toString();
        }
        for (Category category : categories) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(category.name);
        }
        return builder.toString();
    }


    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }


}
