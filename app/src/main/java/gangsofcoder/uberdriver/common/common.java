package gangsofcoder.uberdriver.common;

import gangsofcoder.uberdriver.remote.IGoogleApi;
import gangsofcoder.uberdriver.remote.RetrofitClient;

/**
 * Created by suraj on 13-Jan-18.
 */

public class common {
    public static final String baseURL = "https://maps.googleapis.com";

    public static IGoogleApi getGoogleApi() {
        return RetrofitClient.getClient(baseURL).create(IGoogleApi.class);
    }
}
