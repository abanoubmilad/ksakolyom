package abanoubm.ksakolyom;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class BackgroundService extends WakefulIntentService {

    public BackgroundService() {
        super("BackgroundService");
    }

    /**
     * Asynchronous background operations of service, with wakelock
     */
    @Override
    public void doWakefulWork(Intent intent) {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_YEAR, -1);
//        String date = new SimpleDateFormat("yyyy-MM-dd").format(
//                cal.getTime());
        StoryList story = Utility.getTodayStory();
        if (story == null)
            return;
        if (Utility.checkLastTodayStory(this, story.getDate())) {
            Utility.showStoryNotification(this, story);
        }
    }
}