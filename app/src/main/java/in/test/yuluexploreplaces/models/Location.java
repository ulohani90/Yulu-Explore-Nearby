package in.test.yuluexploreplaces.models;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.List;

import androidx.room.TypeConverters;
import in.test.yuluexploreplaces.converters.AddressTypeConverter;

public class Location {
    @SerializedName("address")
    public String address;

    @SerializedName("lat")
    public double lat;

    @SerializedName("lng")
    public double lng;

    @SerializedName("crossStreet")
    public String crossStreet;

    @SerializedName("distance")
    public long distance;

    @TypeConverters(AddressTypeConverter.class)
    @SerializedName("formattedAddress")
    public List<String> formattedAddress;

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


    public List<String> getFormattedAddress() {
        return formattedAddress;
    }

    public String getFormattedAddressString() {
        StringBuilder builder = new StringBuilder();
        if (formattedAddress == null) {
            return builder.toString();
        }
        for (String item : formattedAddress) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(item);
        }
        return builder.toString();
    }

    public long getDistance() {
        return distance;
    }

    public String getCrossStreet() {
        return crossStreet;
    }

    public String getDistanceString() {
        StringBuilder builder = new StringBuilder();

        if (distance < 1000) {
            builder.append(distance);
            builder.append(" m");
        } else {
            DecimalFormat df = new DecimalFormat("0.00");
            double distaneInKM = (double) distance / 1000;
            builder.append(df.format(distaneInKM));
            builder.append(" km");
        }
        return builder.toString();
    }
}
