package in.test.yuluexploreplaces.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;
import in.test.yuluexploreplaces.models.Category;
import in.test.yuluexploreplaces.models.Venue;

public class DataTypeConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Category> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Category>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ListToString(List<Category> someObjects) {
        return gson.toJson(someObjects);
    }
}
