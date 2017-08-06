package abanoubm.ksakolyom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Calendar;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(Splash.this, DisplayStories.class));
        finish();
    }
}