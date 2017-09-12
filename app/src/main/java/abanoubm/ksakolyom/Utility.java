package abanoubm.ksakolyom;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Utility {
    public final static int STORIES_ALL = 4, STORIES_FAV = 2, STORIES_READ = 3, STORIES_UN_READ = 0;

    public static final String TAG_PAGING = "paging", TAG_NEXT = "next", TAG_PREVIOUS = "previous";

    public static final String TAG_LAST = "last";
    public static final String TAG_LAST_NOTI = "last_noti";

    public static ArrayList<Story> parseStories(String response, Context context, String pagingType) {
        if (response == null)
            return null;

        try {
            JSONObject obj = new JSONObject(response);

            JSONArray array;

            array = obj.getJSONArray("data");


            int length = array.length();
            ArrayList<Story> stories = new ArrayList<>(length);
            JSONObject subObj;
            String picture;
            String fullPicture;
            String msg;
            for (int i = 0; i < length; i++) {
                subObj = array.getJSONObject(i);
                try {
                    picture = subObj.getString("picture");
                } catch (JSONException e) {
                    picture = "";
                }
                try {
                    fullPicture = subObj.getString("full_picture");
                } catch (JSONException e) {
                    fullPicture = "";
                }
                try {
                    msg = subObj.getString("message");
                } catch (JSONException e) {
                    msg = "";
                }
                stories.add(new Story(subObj.getString("id"), picture, fullPicture,
                        msg,
                        subObj.getString("created_time").substring(0, 10)));
            }

            if (length != 0) {
                obj = obj.getJSONObject("paging");

                if (pagingType == null) {
                    updatePagingURL(context, TAG_PREVIOUS, obj.getString(TAG_PREVIOUS));
                    updatePagingURL(context, TAG_NEXT, obj.getString(TAG_NEXT));
                    updateLastPaging(context);
                } else if (pagingType.equals(TAG_NEXT)) {
                    updatePagingURL(context, TAG_NEXT, obj.getString(TAG_NEXT));
                } else {
                    updatePagingURL(context, TAG_PREVIOUS, obj.getString(TAG_PREVIOUS));
                    updateLastPaging(context);
                }
            } else {
                if (pagingType == null)
                    updateLastPaging(context);
                else if (pagingType.equals(TAG_NEXT))
                    updatePagingURL(context, TAG_NEXT, "");
                else if (pagingType.equals(TAG_PREVIOUS))
                    updateLastPaging(context);
            }
            return stories;
        } catch (Exception e) {
            //     e.printStackTrace();
            return null;
        }
    }

    public static StoryList parseStory(String response) {

        if (response == null)
            return null;

        try {
            JSONObject obj = new JSONObject(response);

            JSONArray array;

            array = obj.getJSONArray("data");


            if (array.length() == 0)
                return null;
            JSONObject subObj;
            String picture;
            //  String fullPicture;
            String msg;
            subObj = array.getJSONObject(0);
            try {
                picture = subObj.getString("picture");
            } catch (JSONException e) {
                picture = "";
            }
//            try {
//                fullPicture = subObj.getString("full_picture");
//            } catch (JSONException e) {
//                fullPicture = "";
//            }
            try {
                msg = subObj.getString("message");
            } catch (JSONException e) {
                msg = "";
            }
            return new StoryList(subObj.getString("id"), picture,
                    msg,
                    subObj.getString("created_time").substring(0, 10));


//            if (length != 0) {
//                obj = obj.getJSONObject("paging");
//
//                if (pagingType == null) {
//                    updatePagingURL(context, TAG_PREVIOUS, obj.getString(TAG_PREVIOUS));
//                    updatePagingURL(context, TAG_NEXT, obj.getString(TAG_NEXT));
//                    updateLastPaging(context);
//                } else if (pagingType.equals(TAG_NEXT)) {
//                    updatePagingURL(context, TAG_NEXT, obj.getString(TAG_NEXT));
//                } else {
//                    updatePagingURL(context, TAG_PREVIOUS, obj.getString(TAG_PREVIOUS));
//                    updateLastPaging(context);
//                }
//            } else {
//                if (pagingType == null)
//                    updateLastPaging(context);
//                else if (pagingType.equals(TAG_NEXT))
//                    updatePagingURL(context, TAG_NEXT, "");
//                else if (pagingType.equals(TAG_PREVIOUS))
//                    updateLastPaging(context);
//            }
//            return stories;
        } catch (Exception e) {
            //   e.printStackTrace();
            return null;
        }
    }

    public static String[] parsePost(String response) {
        if (response == null)
            return null;
        String[] postDes = new String[3];
        try {
            JSONObject obj = new JSONObject(response);

            postDes[0] = obj.getJSONObject("likes").getJSONObject("summary").getString("total_count");
            postDes[1] = obj.getJSONObject("comments").getJSONObject("summary").getString("total_count");
            postDes[2] = obj.getJSONObject("shares").getString("count");

            return postDes;
        } catch (Exception e) {
            //    e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Story> getPagingStories(Context context) {
        return parseStories(HTTPClient.get(), context, null);
    }

    public static StoryList getTodayStory() {
        return parseStory(HTTPClient.getTodaySearch());
    }

    public static String[] getPostDes(String id) {
        return parsePost(HTTPClient.getPost(id));
    }

    public static ArrayList<Story> getPagingStories(Context context, String pagingType) {
        String pagingURL = getPagingURL(context, pagingType);
        Log.i("check-pagingURL", pagingURL);

        if (pagingURL.length() != 0)
            return parseStories(HTTPClient.get(pagingURL), context, pagingType);
        else
            return new ArrayList<>(0);
    }

    public static String getPagingURL(Context context, String pageType) {
        return context.getSharedPreferences(TAG_PAGING,
                Context.MODE_PRIVATE).getString(pageType, "");
    }


    public static void updatePagingURL(Context context, String tag, String value) {
        context.getSharedPreferences(TAG_PAGING,
                Context.MODE_PRIVATE).edit().putString(tag, value).commit();
    }

    public static void updateLastPaging(Context context) {
        context.getSharedPreferences(TAG_PAGING,
                Context.MODE_PRIVATE).edit().putString(TAG_LAST, new SimpleDateFormat("yyyy-MM-dd").format(
                new Date())).commit();
    }

    public static boolean checkLastTodayStory(Context context, String current) {
        String str = context.getSharedPreferences(TAG_LAST_NOTI,
                Context.MODE_PRIVATE).getString("day", "");
        if (current.compareTo(str) > 0) {
            context.getSharedPreferences(TAG_LAST_NOTI,
                    Context.MODE_PRIVATE).edit().putString("day", current).commit();
            return true;
        } else {
            return false;
        }
    }


    public static boolean hasPaging(Context context, String pagingType) {
        if (pagingType.equals(TAG_NEXT))
            return getPagingURL(context, TAG_NEXT).length() != 0;
        else
            return getPagingURL(context, TAG_PREVIOUS).length() != 0 && new SimpleDateFormat("yyyy-MM-dd").format(
                    new Date()).compareTo(context.getSharedPreferences(TAG_PAGING,
                    Context.MODE_PRIVATE).getString(TAG_LAST, "")) == 1;


    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String produceDate(int day, int month, int year) {
        if (month < 10) {
            if (day < 10)
                return year + "-0" + month + "-0" + day;
            else
                return year + "-0" + month + "-" + day;
        } else {
            if (day < 10)
                return year + "-" + month + "-0" + day;
            else
                return year + "-" + month + "-" + day;
        }
    }

    public static void showStoryNotification(Context context, StoryList story) {

        int offset = Math.min(story.getContent().length(), 25);
        String title = story.getContent().substring(0,
                Math.max(offset, story.getContent().indexOf(' ', offset)));

        Intent intent = new Intent(context, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationCompat.Builder n = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.mipmap.ic_mail)
                .setContentTitle(
                        "قصة اليوم"
                                + " - " + title
                )
                .setContentText(story.getContent().replace(">>الآن تطبيق قصة كل يوم على جوجل بلاى", "")
                        .replace("https://play.google.com/store/apps/details?id=abanoubm.ksakolyom", ""))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(story.getContent().replace(">>الآن تطبيق قصة كل يوم على جوجل بلاى", "")
                                .replace("https://play.google.com/store/apps/details?id=abanoubm.ksakolyom", "")))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context,
                        0, intent, 0));

        if (story.getPhoto().length() > 0)
            n.setLargeIcon(getBitmapFromURL(story.getPhoto()));


        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, n.build());
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
