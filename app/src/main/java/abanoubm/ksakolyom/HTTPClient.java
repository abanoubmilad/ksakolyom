package abanoubm.ksakolyom;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HTTPClient {
    private static OkHttpClient client = new OkHttpClient();

    public static String get() {

        Request request = new Request.Builder()
                .url("https://graph.facebook.com/v2.7/208748925813135/feed?" +
                        "fields=picture,full_picture,message,created_time&" +
                        BuildConfig.F_B_A_T
                )
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }

    }

    public static String getTodaySearch() {
        Request request = new Request.Builder()
                .url("https://graph.facebook.com/v2.7/208748925813135/feed?" +
                        "fields=picture,full_picture,message,created_time&limit=1&" +
                        BuildConfig.F_B_A_T
                )
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            return null;
        }

    }

    public static String getPost(String id) {

        Request request = new Request.Builder()
                .url("https://graph.facebook.com/v2.7/" +
                        id + "?fields=likes.limit(0).summary(true),comments.limit(0).summary(true),shares&" +
                        BuildConfig.F_B_A_T

                )
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }

    }

    public static String get(String pagingURL) {

        Request request = new Request.Builder()
                .url(pagingURL)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            //  e.printStackTrace();
            return null;
        }

    }


}
