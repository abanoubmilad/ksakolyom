package abanoubm.ksakolyom;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentSearchContent extends Fragment {
    private StoryDisplayListAdapter mAdapter;
    private DB mDB;
    private static final String ARG_ID = "id";


    private class SearchTask extends
            AsyncTask<String, Void, ArrayList<Story>> {
        private ProgressDialog pBar;

        @Override
        protected void onPreExecute() {
            pBar = new ProgressDialog(getActivity());
            pBar.setCancelable(false);
            pBar.show();
        }

        @Override
        protected ArrayList<Story> doInBackground(String... params) {
            if (mDB == null)
                mDB = DB.getInstant(getActivity());
            return mDB.searchStories(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Story> result) {
            if (getContext() == null)
                return;
            mAdapter.clearThenAddAll(result);
            if (result.size() == 0)
                Toast.makeText(getActivity(),
                        R.string.msg_no_results, Toast.LENGTH_SHORT).show();
            pBar.dismiss();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_search_content, container, false);

        final EditText input = (EditText) root.findViewById(R.id.input);
        ListView lv = (ListView) root.findViewById(R.id.list);
        mAdapter = new StoryDisplayListAdapter(getActivity(), new ArrayList<StoryList>(0));
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1,
                                    int position, long arg3) {

                ((CallBack) getActivity()).notifyFired((mAdapter.getItem(position).getId()));


            }
        });
        root.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = input.getText().toString().trim();
                if (temp.length() > 0)
                    new SearchTask().execute(temp);

            }
        });
        return root;
    }

}
