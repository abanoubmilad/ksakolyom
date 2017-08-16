package abanoubm.ksakolyom;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentSearchDates extends Fragment {
    private StoryDisplayListAdapter mAdapter;
    private DB mDB;
    private static final String ARG_ID = "id";
    private String targetDay;

    private class SearchTask extends
            AsyncTask<Void, Void, ArrayList<Story>> {
        private ProgressDialog pBar;

        @Override
        protected void onPreExecute() {
            pBar = new ProgressDialog(getActivity());
            pBar.setCancelable(false);
            pBar.show();
        }

        @Override
        protected ArrayList<Story> doInBackground(Void... params) {
            if (mDB == null)
                mDB = DB.getInstant(getActivity());
            return mDB.searchDates(targetDay);
        }

        @Override
        protected void onPostExecute(ArrayList<Story> result) {
            if(getContext()==null)
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
        View root = inflater.inflate(R.layout.frag_search_dates, container, false);


        final TextView date = (TextView) root.findViewById(R.id.date);
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

        Calendar cal = Calendar.getInstance();
        final DatePickerDialog picker_date = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        targetDay = Utility.produceDate(dayOfMonth, monthOfYear + 1, year);
                        date.setText(targetDay);
                        new SearchTask().execute();

                    }

                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        root.findViewById(R.id.pick_date)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        picker_date.show();

                    }
                });
        return root;
    }

}
