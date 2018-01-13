package gangsofcoder.uberdriver.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by suraj on 13-Jan-18.
 */

public interface IGoogleApi {
    @GET
    Call<String> getPath(@Url String url);
}
