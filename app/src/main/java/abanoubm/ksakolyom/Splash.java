package abanoubm.ksakolyom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WakefulIntentService.scheduleAlarms(new DailyListener(), this, false);

        startActivity(new Intent(Splash.this, Main.class));
        finish();
    }
}