package in.test.yuluexploreplaces.providers;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import dagger.Module;
import dagger.Provides;
import in.test.yuluexploreplaces.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class APIModule {

    @Provides
    Retrofit provideRetrofitClient(OkHttpClient client, GsonConverterFactory converterFactory) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client).build();
        return retrofit;
    }


    @Provides
    OkHttpClient providesOkHttpClient(HttpLoggingInterceptor interceptor) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        return client;
    }

    @Provides
    GsonConverterFactory providesGsonConvertorFactory() {
        GsonConverterFactory converterFactory = GsonConverterFactory.create(new Gson());
        return converterFactory;

    }

    @Provides
    HttpLoggingInterceptor providesHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        return interceptor;
    }
}
