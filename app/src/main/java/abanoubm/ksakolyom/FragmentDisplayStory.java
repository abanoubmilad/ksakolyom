package abanoubm.ksakolyom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FragmentDisplayStory extends Fragment {

    private String id;
    private Story mStory = null;
    private static final String ARG_ID = "id";
    private boolean isFav = false;

    private TextView content, dateView, shares, likes, comments;
    private ImageView photo, fav, check;

    private DB mDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getString(ARG_ID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_display_story, container, false);

        photo = (ImageView) root.findViewById(R.id.photo);
        content = (TextView) root.findViewById(R.id.content);
        dateView = (TextView) root.findViewById(R.id.date);


        shares = (TextView) root.findViewById(R.id.shares);
        comments = (TextView) root.findViewById(R.id.comments);
        likes = (TextView) root.findViewById(R.id.likes);

        fav = (ImageView) root.findViewById(R.id.fav);
        check = (ImageView) root.findViewById(R.id.check);


        return root;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new GetTask().execute();
        new GetPostDesTask().execute();

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateFavTask().execute();
            }
        });
        view.findViewById(R.id.share_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isNetworkAvailable(getContext())) {

                    Toast.makeText(getActivity(),
                            R.string.msg_no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        getActivity().getPackageManager().getPackageInfo(
                                "com.facebook.katana", 0);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("fb://post/" + id)));
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("https://www.facebook.com/ksa.kol.yom/" + id)));
                    }
                }
            }
        });
        view.findViewById(R.id.share_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isNetworkAvailable(getContext())) {

                    Toast.makeText(getActivity(),
                            R.string.msg_no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        getActivity().getPackageManager().getPackageInfo(
                                "com.facebook.katana", 0);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("fb://post/" + id)));
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("https://www.facebook.com/ksa.kol.yom/" + id)));
                    }
                }
            }
        });

    }

    private class GetTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mDB == null)
                mDB = DB.getInstant(getActivity());
            mStory = mDB.getStory(id);
            if (mStory.getRead().equals("0"))
                mDB.markAsRead(mStory.getId());
            return null;
        }


        @Override
        protected void onPostExecute(Void story) {
            if(getContext()==null)
                return;

            if (((WifiManager) getContext().getSystemService(Context.WIFI_SERVICE)).isWifiEnabled()) {
                if (mStory.getFullPhoto().length() != 0)
                    Picasso.with(getContext()).load(mStory.getFullPhoto()).placeholder(R.mipmap.ic_def).into(photo);
                else if (mStory.getPhoto().length() != 0)
                    Picasso.with(getContext()).load(mStory.getPhoto()).placeholder(R.mipmap.ic_def).into(photo);
                else
                    photo.setImageResource(R.mipmap.ic_def);
            } else {
                if (mStory.getPhoto().length() != 0)
                    Picasso.with(getContext()).load(mStory.getPhoto()).placeholder(R.mipmap.ic_def).into(photo);
                else
                    photo.setImageResource(R.mipmap.ic_def);
            }

            content.setText(mStory.getContent());
            try {

                dateView.setText(new SimpleDateFormat("EEEE d - M - yyyy", new Locale("ar")).format(new SimpleDateFormat("yyyy-MM-dd").parse(mStory.getDate())));
            } catch (Exception e) {
                dateView.setText(mStory.getDate());
            }

            content.setText(mStory.getContent());


            if (mStory.getRead().equals("2")) {
                fav.setImageResource(R.mipmap.ic_fav);
                isFav = true;
                check.setVisibility(View.VISIBLE);
            } else if (mStory.getRead().equals("1")) {
                check.setVisibility(View.VISIBLE);
            }

        }
    }

    private class GetPostDesTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String[] doInBackground(Void... params) {
            return Utility.getPostDes(id);
        }


        @Override
        protected void onPostExecute(String[] arr) {
            if (arr != null) {
                likes.setText(arr[0]);
                comments.setText(arr[1]);
                shares.setText(arr[2]);
            }

        }
    }

    private class UpdateFavTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            fav.setClickable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            isFav = !isFav;
            if (isFav)
                DB.getInstant(getActivity()).markAsFav(mStory.getId());
            else
                DB.getInstant(getActivity()).markAsRead(mStory.getId());
            return null;
        }


        @Override
        protected void onPostExecute(Void story) {
            if (isFav) {
                fav.setImageResource(R.mipmap.ic_fav);
                Toast.makeText(getActivity(),
                        R.string.msg_added_fav, Toast.LENGTH_SHORT).show();
            } else
                fav.setImageResource(R.mipmap.ic_add);

            fav.setClickable(true);


        }

    }

}