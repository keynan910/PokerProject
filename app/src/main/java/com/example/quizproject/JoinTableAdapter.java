package com.example.quizproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class JoinTableAdapter extends ArrayAdapter<User> {
    private Context context;
    List<User> objects;
    View view;
    ImageView ivUser;

    public JoinTableAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.rowlistviewlayout, parent, false);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSubTitle = view.findViewById(R.id.tvSubTitle);
        TextView tvPoints = view.findViewById(R.id.tvPoints);
        ivUser = view.findViewById(R.id.ivUser);
        User user = objects.get(position);
        if (user.getUserName().equals("")){
            tvPoints.setText("joined");
            tvTitle.setText(user.getEmail());
            return view;
        }
        else {
            tvPoints.setText("joined");
            tvTitle.setText(user.getUserName());
            tvSubTitle.setText(user.getEmail());
            Glide.with(context).load(user.getProfileImageUri()).into(ivUser);
            return view;
        }
    }
}