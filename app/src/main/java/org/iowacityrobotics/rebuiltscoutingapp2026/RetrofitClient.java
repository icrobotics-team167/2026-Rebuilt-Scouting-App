package org.iowacityrobotics.rebuiltscoutingapp2026;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String API_KEY =
            "Cg1EaqGBzK8oRJA1jCMkgpMHjd40R7QVe8tQ02t6YMw4f6vVf78UdjDWd8uEdQqb";

    private static final String BASE_URL =
            "https://www.thebluealliance.com/api/v3/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {

        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("X-TBA-Auth-Key", API_KEY)
                                .build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
