package org.iowacityrobotics.rebuiltscoutingapp2026;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TBAApi {

    @GET("event/{event_key}/matches")
    Call<List<Match>> getEventMatches(
            @Path("event_key") String eventKey
    );
}