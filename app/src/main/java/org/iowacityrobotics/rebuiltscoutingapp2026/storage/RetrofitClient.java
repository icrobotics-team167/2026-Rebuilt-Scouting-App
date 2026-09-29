package org.iowacityrobotics.rebuiltscoutingapp2026.storage;

import android.content.Context;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL =
            "https://www.thebluealliance.com/api/v3/";

    private static Retrofit retrofit;

    public static synchronized Retrofit getClient(Context context) {

        if (retrofit == null) {

            String apiKey = context.getString(R.string.api_key);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("X-TBA-Auth-Key", apiKey)
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