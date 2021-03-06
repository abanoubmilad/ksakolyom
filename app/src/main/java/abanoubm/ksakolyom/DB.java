package abanoubm.ksakolyom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {
    private static String DB_NAME = "ksakolyom_db";
    public static final int DB_VERSION = 1;

    public static final String TB_STORY = "ksakolyom_tb",
            STORY_CONTENT = "story_con",
            STORY_PHOTO = "story_ph",
            STORY_FULL_PHOTO = "story_ph_full",
            STORY_DATE = "story_date",
            STORY_READ = "story_read",
            STORY_ID = "story_id";

    private static DB dbm;
    private SQLiteDatabase readableDB, writableDB;


    public static DB getInstant(Context context) {
        return dbm != null ? dbm : (dbm = new DB(context));
    }

    private DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        readableDB = getReadableDatabase();
        writableDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TB_STORY + " ( " +
                STORY_ID + "  text, " +
                STORY_CONTENT + "  text, " +
                STORY_READ + "  character(1) default '0', " +
                STORY_PHOTO + " text default '', " +
                STORY_FULL_PHOTO + " text default '', " +
                STORY_DATE + " character(10), " +
                "primary key (" + STORY_ID + "," + STORY_DATE + "))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {


    }

    public boolean isHoldingStories() {
        boolean check = false;
        Cursor c = readableDB.query(TB_STORY, new String[]{STORY_DATE}, null, null, null, null, null, "1");
        if (c.moveToNext())
            check = true;
        c.close();
        return check;
    }

    public void addStories(ArrayList<Story> stories) {
        writableDB.beginTransaction();
        ContentValues values;
        for (Story story : stories) {
            values = new ContentValues();
            values.put(STORY_CONTENT, story.getContent());
            values.put(STORY_DATE, story.getDate());
            values.put(STORY_PHOTO, story.getPhoto());
            values.put(STORY_FULL_PHOTO, story.getFullPhoto());
            values.put(STORY_ID, story.getId());
            writableDB.insert(TB_STORY, null, values);
        }
        writableDB.setTransactionSuccessful();
        writableDB.endTransaction();
    }

    public ArrayList<Story> getStories(int displayType) {

        String selection = displayType == Utility.STORIES_ALL ? null :
                displayType == Utility.STORIES_FAV ? STORY_READ + "= '2'" :
                        displayType == Utility.STORIES_UN_READ ? STORY_READ + "= '0'" :
                                STORY_READ + "!= '0'";

        Cursor c = readableDB.query(TB_STORY,
                new String[]{STORY_ID, STORY_PHOTO, STORY_FULL_PHOTO, STORY_CONTENT, STORY_DATE,STORY_READ},
                selection, null, null, null, STORY_DATE + " DESC", null);

        ArrayList<Story> result = new ArrayList<>(c.getCount());

        if (c.moveToFirst()) {

            do {
                result.add(new Story(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3), c.getString(4), c.getString(5)));

            } while (c.moveToNext());
        }
        c.close();

        return result;

    }

    public ArrayList<Story> searchStories(String token) {
        Cursor c = readableDB.query(TB_STORY,
                new String[]{STORY_ID, STORY_PHOTO, STORY_FULL_PHOTO, STORY_CONTENT, STORY_DATE,STORY_READ},
                STORY_CONTENT + " like ?", new String[]{"%" + token + "%"}, null, null, STORY_DATE + " DESC", null);
        ArrayList<Story> result = new ArrayList<>(c.getCount());

        if (c.moveToFirst()) {

            do {
                result.add(new Story(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3), c.getString(4), c.getString(5)));

            } while (c.moveToNext());
        }
        c.close();

        return result;

    }

    public ArrayList<Story> searchDates(String date) {
        Cursor c = readableDB.query(TB_STORY,
                new String[]{STORY_ID, STORY_PHOTO, STORY_FULL_PHOTO, STORY_CONTENT, STORY_DATE,STORY_READ},
                STORY_DATE + "=?", new String[]{date}, null, null, STORY_DATE + " DESC", null);
        ArrayList<Story> result = new ArrayList<>(c.getCount());

        if (c.moveToFirst()) {

            do {
                result.add(new Story(c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3), c.getString(4), c.getString(5)));

            } while (c.moveToNext());
        }
        c.close();

        return result;

    }

    public Story getStory(String id) {
        Cursor c = readableDB.query(TB_STORY,
                new String[]{STORY_ID, STORY_PHOTO, STORY_FULL_PHOTO, STORY_CONTENT, STORY_DATE,STORY_READ},
                STORY_ID + "=?", new String[]{id}, null, null, null, null);
        Story story = null;

        if (c.moveToFirst()) {
            story = new Story(c.getString(0), c.getString(1),
                    c.getString(2), c.getString(3), c.getString(4), c.getString(5));
        }
        c.close();

        return story;

    }

    public void markAsRead(String id) {
        ContentValues values = new ContentValues();
        values.put(STORY_READ, "1");
        writableDB.update(TB_STORY, values, STORY_ID + "=?", new String[]{id});
    }

    public void markAsFav(String id) {
        ContentValues values = new ContentValues();
        values.put(STORY_READ, "2");
        writableDB.update(TB_STORY, values, STORY_ID + "=?", new String[]{id});
    }


}
