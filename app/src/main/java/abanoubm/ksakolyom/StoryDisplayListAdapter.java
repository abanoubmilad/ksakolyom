package abanoubm.ksakolyom;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StoryDisplayListAdapter extends ArrayAdapter<StoryList> {

    private int selected = -1;

    public StoryDisplayListAdapter(Context context, ArrayList<StoryList> stories) {
        super(context, 0, stories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        StoryList story = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_display_story_list, parent, false);
            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.photo = (ImageView) convertView.findViewById(R.id.photo);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.root = convertView.findViewById(R.id.root);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setText(story.getContent());
        holder.date.setText(story.getDate());

        try {

            holder.date.setText(new SimpleDateFormat("EEEE d - M - yyyy",
                    new Locale("ar")).format(new SimpleDateFormat("yyyy-MM-dd").parse(story.getDate())));
        } catch (Exception e) {
            holder.date.setText(story.getDate());
        }


        if (story.getFullPhoto().length() > 0)
            Picasso.with(getContext()).load(story.getFullPhoto()).placeholder(R.mipmap.ic_def).into(holder.photo);
        else if (story.getPhoto().length() > 0)
            Picasso.with(getContext()).load(story.getPhoto()).placeholder(R.mipmap.ic_def).into(holder.photo);
        else
            holder.photo.setImageResource(R.mipmap.ic_def);

        if (selected == position)
            holder.root.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.colorAccent));
        else
            holder.root.setBackgroundResource(R.drawable.dynamic_bg);

        return convertView;
    }

    private static class ViewHolder {
        TextView content, date;
        ImageView photo;
        View root;
    }

    public void setSelectedIndex(int pos) {
        selected = pos;
        notifyDataSetChanged();
    }

    public void clearThenAddAll(ArrayList<Story> list) {
        setNotifyOnChange(false);
        clear();
        int offset;
        for (Story story : list) {
            offset = Math.min(story.getContent().length(), 25);
            super.add(new StoryList(story.getId(),
                    story.getPhoto(), story.getFullPhoto(), story.getContent().substring(0,
                    Math.max(offset, story.getContent().indexOf(' ', offset))), story.getDate()));
        }
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Story> list) {
        setNotifyOnChange(false);
        int offset;
        for (Story story : list) {
            offset = Math.min(story.getContent().length(), 25);
            super.add(new StoryList(story.getId(),
                    story.getPhoto(), story.getFullPhoto(), story.getContent().substring(0,
                    Math.max(offset, story.getContent().indexOf(' ', offset))), story.getDate()));
        }
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public void appendAllOnTop(ArrayList<Story> list) {
        setNotifyOnChange(false);
        int length = list.size();
        Story story;
        int offset;
        for (int i = length - 1; i > -1; i--) {
            story = list.get(i);
            offset = Math.min(story.getContent().length(), 25);
            super.insert(new StoryList(story.getId(), story.getPhoto(), story.getFullPhoto(), story.getContent().substring(0,
                    Math.max(offset, story.getContent().indexOf(' ', offset))), story.getDate()), 0);
        }
        setNotifyOnChange(true);
        notifyDataSetChanged();

    }
}