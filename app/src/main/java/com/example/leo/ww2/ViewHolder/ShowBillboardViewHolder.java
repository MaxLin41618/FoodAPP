package com.example.leo.ww2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.ww2.R;

public class ShowBillboardViewHolder extends RecyclerView.ViewHolder{

    public TextView billboard_date,billboard_name,billboard_title,billboard_comment;
    public ImageView billboard_image;


    public ShowBillboardViewHolder(View itemView) {
        super(itemView);

        billboard_image = (ImageView)itemView.findViewById(R.id.billboard_image);
        billboard_date = (TextView)itemView.findViewById(R.id.billboard_date);
        billboard_name = (TextView)itemView.findViewById(R.id.billboard_name);
        billboard_title = (TextView)itemView.findViewById(R.id.billboard_title);
        billboard_comment = (TextView)itemView.findViewById(R.id.billboard_comment);
    }
}
