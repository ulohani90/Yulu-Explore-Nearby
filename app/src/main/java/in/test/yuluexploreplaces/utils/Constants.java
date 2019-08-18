package in.test.yuluexploreplaces.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.gms.common.util.Strings;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.room.util.StringUtil;

public class Constants {
    public static String BASE_URL = "https://api.foursquare.com/";

    public static final int REQUEST_LOCATION_PERMISSION = 101;

    public static final String CLIENT_ID = "OTWCHM2E55T2EFGPSGDCJG3N4KPEGIZYIDQEGLC3TAC4EMZ2";

    public static final String CLIENT_SECRET = "B551BYLRZL5HRUN0L32L4W51FTT0PQ1CXKPRDLJQV434KBKW";


    public static String getDateInVersionFormat() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("YYYYMMDD");
        return format.format(date);
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (con.getActiveNetworkInfo() == null)
            return false;
        return con.getActiveNetworkInfo().isConnected();

    }


    public static String getQueryFormatted(String query) {
        if (query.equalsIgnoreCase("topPicks"))
            return "Top Picks";
        return Strings.capitalize(query);

    }
}
