package abanoubm.ksakolyom;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private ProgressBar next;
    private SwipeRefreshLayout previous;
    private boolean loading_previous = false, loading_next = false, paging_allowed = false;

    private class GetAllTask extends AsyncTask<Void, Void, ArrayList<Story>> {
        @Override
        protected void onPreExecute() {
            previous.setRefreshing(true);
        }

        @Override
        protected ArrayList<Story> doInBackground(Void... params) {
            if (getContext() == null)
                return null;
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
            if (getContext() == null)
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
            previous.setRefreshing(false);

        }
    }

    private class GetPreviousPagingTask extends AsyncTask<Void, Void, ArrayList<Story>> {

        @Override
        protected void onPreExecute() {
            if (getContext() == null)
                return;
            previous.setRefreshing(true);
        }

        @Override
        protected ArrayList<Story> doInBackground(Void... params) {
            if (getContext() == null)
                return null;
            if (mDB == null)
                mDB = DB.getInstant(getActivity());

            ArrayList<Story> stories = null;
            if (Utility.isNetworkAvailable(getContext()) && (stories =
                    Utility.getPagingStories(getContext(), Utility.TAG_PREVIOUS)) != null)
                mDB.addStories(stories);

            if (stories != null && stories.size() != 0 && getContext() != null)
                Utility.checkLastTodayStory(getContext(), stories.get(0).getDate());

            return stories;
        }

        @Override
        protected void onPostExecute(ArrayList<Story> stories) {
            if (getContext() == null)
                return;
            loading_previous = false;
            if (stories != null)
                mAdapter.appendAllOnTop(stories);

            previous.setRefreshing(false);

        }
    }

    private class GetNextPagingTask extends AsyncTask<Void, Void, ArrayList<Story>> {

        @Override
        protected void onPreExecute() {
            next.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Story> doInBackground(Void... params) {
            if (getContext() == null)
                return null;
            if (mDB == null)
                mDB = DB.getInstant(getActivity());

            ArrayList<Story> stories = null;
            if (Utility.isNetworkAvailable(getContext()) && (stories = Utility.getPagingStories(getContext(), Utility.TAG_NEXT)) != null)
                mDB.addStories(stories);

            return stories;
        }

        @Override
        protected void onPostExecute(ArrayList<Story> stories) {
            if (getContext() == null)
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


        next = (ProgressBar) root.findViewById(R.id.next);

        mAdapter = new StoryDisplayListAdapter(getActivity(), new ArrayList<StoryList>(0));
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1,
                                    int position, long arg3) {
                if (isDualMode)
                    mAdapter.setSelectedIndex(position);

                previousPosition = lv.getFirstVisiblePosition();

                ((CallBack) getActivity()).notifyFired((mAdapter.getItem(position).getId()));
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
                    loading_previous = true;
                    new GetPreviousPagingTask().execute();
                } else if (firstVisibleItem + visibleItemCount >= totalItemCount && !loading_next && Utility.hasPaging(getContext(), Utility.TAG_NEXT)) {
                    loading_next = true;
                    new GetNextPagingTask().execute();
                }
            }
        });
        previous = (SwipeRefreshLayout) root.findViewById(R.id.reload);
        previous.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //      Log.i("check","i'm in previous");
                        if (!loading_previous && Utility.isNetworkAvailable(getContext()) && !loading_next) {
                            loading_previous = true;
                            new GetPreviousPagingTask().execute();
                            //     Log.i("check","loading true");

                        } else {
                            previous.setRefreshing(false);
                        }
                    }
                }
        );

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        paging_allowed = false;
        new GetAllTask().execute();

    }


}
