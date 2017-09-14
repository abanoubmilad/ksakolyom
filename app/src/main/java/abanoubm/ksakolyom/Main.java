package abanoubm.ksakolyom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Main extends AppCompatActivity implements CallBack {

    private boolean dualMode;
    private static final String ARG_ID = "id";
    private static final String ARG_DUAL_MODE = "dual";
    private static final String ARG_SELECTION = "sel";
    private MenuItemAdapter mMenuItemAdapter;
    private DrawerLayout nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_display_stories);
        dualMode = findViewById(R.id.display_stories_fragment_dual) != null;

        ListView lv = (ListView) findViewById(R.id.list);
        nav = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuItemAdapter = new MenuItemAdapter(getApplicationContext(),
                new ArrayList<>(Arrays.asList(getResources()
                        .getStringArray(R.array.main_menu))));
        lv.setAdapter(mMenuItemAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                nav.closeDrawers();
                switch (position) {
                    case 0:
                        notifyFired(null);
                        updateContent(Utility.STORIES_ALL);

                        break;
                    case 1:
                        notifyFired(null);

                        updateContent(Utility.STORIES_FAV);

                        break;
                    case 2:
                        notifyFired(null);

                        updateContent(Utility.STORIES_UN_READ);

                        break;
                    case 3:
                        notifyFired(null);

                        updateContent(Utility.STORIES_READ);

                        break;
                    case 4:
                        notifyFired(null);

                        ((TextView) findViewById(R.id.subhead)).setText(getResources().getString(R.string.sub_search_content));

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.display_stories_fragment, new FragmentSearchContent())
                                .commit();

                        break;
                    case 5:
                        notifyFired(null);

                        ((TextView) findViewById(R.id.subhead)).setText(getResources().getString(R.string.sub_search_dates));

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.display_stories_fragment, new FragmentSearchDates())
                                .commit();
                        break;
                    case 6:
                        try {
                            getPackageManager().getPackageInfo(
                                    "com.facebook.katana", 0);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("fb://page/208748925813135")));
                        } catch (Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("https://www.facebook.com/ksa.kol.yom")));
                        }
                        break;
                    case 7:
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
                                .parse("https://drive.google.com/open?id=1GuP-wW_0MVH_HyN7Ypb9djHrfcxVGKbTL_g8C_uRw2M")));
                        break;
                    case 8:
                        try {
                            getPackageManager().getPackageInfo(
                                    "com.facebook.katana", 0);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("fb://profile/1363784786")));
                        } catch (Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("https://www.facebook.com/EngineeroBono")));
                        }
                        break;

                    case 9:
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(goToMarket);
                        } catch (Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        }
                        break;
                }
            }
        });
//        if (savedInstanceState == null) {
//
//
//            updateContent(getIntent().getIntExtra(ARG_SELECTION, Utility.STORIES_ALL));
//
//
//        }
        findViewById(R.id.nav_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nav.openDrawer(Gravity.RIGHT);

            }
        });
        ((TextView) findViewById(R.id.footer)).setText("ksa kol yom " +
                BuildConfig.VERSION_NAME + " ©" + new SimpleDateFormat(
                "yyyy", Locale.getDefault())
                .format(new Date()) + " Abanoub M.");
        updateContent(Utility.STORIES_ALL);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMenuItemAdapter.recycleIcons();

    }

    private void updateContent(int selection) {


        if (selection == Utility.STORIES_ALL)
            ((TextView) findViewById(R.id.subhead)).setText(getResources().getString(R.string.sub_stories));
        else if (selection == Utility.STORIES_FAV)
            ((TextView) findViewById(R.id.subhead)).setText(getResources().getString(R.string.sub_fav));
        else if (selection == Utility.STORIES_UN_READ)
            ((TextView) findViewById(R.id.subhead)).setText(getResources().getString(R.string.sub_unread));
        else if (selection == Utility.STORIES_READ)
            ((TextView) findViewById(R.id.subhead)).setText(getResources().getString(R.string.sub_read));

        Bundle args = new Bundle();
        args.putBoolean(ARG_DUAL_MODE, dualMode);

        if (selection == Utility.STORIES_ALL) {

            FragmentDisplayStories fragment = new FragmentDisplayStories();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.display_stories_fragment, fragment)
                    .commit();
        } else {
            FragmentDisplaySelection fragment = new FragmentDisplaySelection();
            args.putInt(ARG_SELECTION, selection);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.display_stories_fragment, fragment)
                    .commit();
        }
    }

    @Override
    public void notifyFired(String id) {
        if (id == null) {
            if (dualMode) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment != null)
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        } else {
            if (dualMode) {

                Bundle args = new Bundle();
                args.putString(ARG_ID, id);
                args.putBoolean(ARG_DUAL_MODE, true);

                FragmentDisplayStory fragment = new FragmentDisplayStory();
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.display_story_fragment, fragment)
                        .commit();

            } else {
                startActivity(new Intent(this, DisplayStory.class).putExtra(ARG_ID, id));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (nav.isDrawerOpen(Gravity.RIGHT)) {
            nav.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
    }
}
