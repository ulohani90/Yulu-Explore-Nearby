package in.test.yuluexploreplaces.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("name")
    public String name;

    public String getName() {
        return name;
    }
}
