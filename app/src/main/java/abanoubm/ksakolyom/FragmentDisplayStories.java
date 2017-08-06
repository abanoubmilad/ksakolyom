package abanoubm.ksakolyom;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentDisplayStories extends Fragment {
    private ListView lv;
    private int previousPosition = 0;
    private boolean isDualMode = false;
    private StoryDisplayListAdapter mAdapter;
    private DB mDB;
    private static final String ARG_DUAL_MODE = "dual";
    private ProgressBar previous, next, loading;
    private boolean loading_previous = false, loading_next = false, paging_allowed = false;

    private class GetAllTask extends AsyncTask<Void, Void, ArrayList<Story>> {
        @Override
        protected void onPreExecute() {
            paging_allowed = false;
            loading.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<Story> doInBackground(Void... params) {
            if (mDB == null)
                mDB = DB.getInstant(getActivity());

            if (mDB.isHoldingStories())
                return mDB.getStories(Utility.STORIES_ALL);

            ArrayList<Story> stories = null;
            if (Utility.isNetworkAvailable(getContext()) && (stories = Utility.getPagingStories(getContext())) != null)
                mDB.addStories(stories);

            return stories;
        }

        @Override
        protected void onPostExecute(ArrayList<Story> stories) {
            if(getContext()==null)
                return;
            if (stories != null) {
                mAdapter.clearThenAddAll(stories);
                if (stories.size() == 0) {
               //     getActivity().finish();
                    Toast.makeText(getActivity(),
                            R.string.msg_no_internet, Toast.LENGTH_SHORT).show();

                } else {
                    paging_allowed = true;
                    if (previousPosition < stories.size())
                        lv.setSelection(previousPosition);
                    if (isDualMode) {
                        lv.performItemClick(lv.findViewWithTag(mAdapter.getItem(previousPosition)),
                                previousPosition, mAdapter.getItemId(previousPosition));
                    }
                }
            } else {
               // getActivity().finish();
                Toast.makeText(getActivity(),
                        R.string.msg_no_internet, Toast.LENGTH_SHORT).show();
            }
            loading.setVisibility(View.GONE);


        }
    }

    private class GetPreviousPagingTask extends AsyncTask<Void, Void, ArrayList<Story>> {

        @Override
        protected void onPreExecute() {
            previous.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Story> doInBackground(Void... params) {
            if (mDB == null)
                mDB = DB.getInstant(getActivity());

            ArrayList<Story> stories = null;
            if (Utility.isNetworkAvailable(getContext()) && (stories =
                    Utility.getPagingStories(getContext(), Utility.TAG_PREVIOUS)) != null)
                mDB.addStories(stories);

            return stories;
        }

        @Override
        protected void onPostExecute(ArrayList<Story> stories) {
            if(getContext()==null)
                return;
            loading_previous = false;
            if (stories != null)
                mAdapter.appendAllOnTop(stories);

            previous.setVisibility(View.GONE);

        }
    }

    private class GetNextPagingTask extends AsyncTask<Void, Void, ArrayList<Story>> {

        @Override
        protected void onPreExecute() {
            next.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Story> doInBackground(Void... params) {
            if (mDB == null)
                mDB = DB.getInstant(getActivity());

            ArrayList<Story> stories = null;
            if (Utility.isNetworkAvailable(getContext()) && (stories = Utility.getPagingStories(getContext(), Utility.TAG_NEXT)) != null)
                mDB.addStories(stories);

            return stories;
        }

        @Override
        protected void onPostExecute(ArrayList<Story> stories) {
            if(getContext()==null)
                return;
            loading_next = false;

            if (stories != null) {
                mAdapter.addAll(stories);
                if (stories.size() == 0)
                    loading_next = true;
            }
            next.setVisibility(View.GONE);

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            isDualMode = arguments.getBoolean(ARG_DUAL_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_display_stories, container, false);


        lv = (ListView) root.findViewById(R.id.list);


        previous = (ProgressBar) root.findViewById(R.id.previous);
        next = (ProgressBar) root.findViewById(R.id.next);
        loading = (ProgressBar) root.findViewById(R.id.loading);

        mAdapter = new StoryDisplayListAdapter(getActivity(), new ArrayList<StoryList>(0));
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1,
                                    int position, long arg3) {
                if (isDualMode)
                    mAdapter.setSelectedIndex(position);

                previousPosition = lv.getFirstVisiblePosition();

                ((CallBack) getActivity()).notify((mAdapter.getItem(position).getId()));
            }
        });


        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!paging_allowed)
                    return;
                if (firstVisibleItem == 0 && !loading_previous && Utility.isNetworkAvailable(getContext()) && Utility.hasPaging(getContext(), Utility.TAG_PREVIOUS)) {
                    //  Log.i("previoussssssssssssss", "previoussssssssssssss");
                    loading_previous = true;
                    new GetPreviousPagingTask().execute();
                } else if (firstVisibleItem + visibleItemCount >= totalItemCount && !loading_next && Utility.hasPaging(getContext(), Utility.TAG_NEXT)) {
                    loading_next = true;
                    //   Log.i("nextttttttttttttttttt", "nextttttttttttttttttt");
                    new GetNextPagingTask().execute();
                }
            }
        });
        View view = root.findViewById(R.id.reload);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loading_previous && Utility.isNetworkAvailable(getContext())) {
                    loading_previous = true;
                    new GetPreviousPagingTask().execute();
                }
            }
        });
        root.findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getCount() > 0)
                    lv.setSelection(0);
            }
        });
        root.findViewById(R.id.down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getCount() > 0)
                    lv.setSelection(mAdapter.getCount() - 1);
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GetAllTask().execute();

    }


}
