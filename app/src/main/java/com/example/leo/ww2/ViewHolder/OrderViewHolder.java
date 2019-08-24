package com.example.leo.ww2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.leo.ww2.Interface.ItemClickListener;
import com.example.leo.ww2.R;

import org.w3c.dom.Text;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate;
    public Button btnDetail;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderDate = (TextView)itemView.findViewById(R.id.order_date);

        btnDetail = (Button) itemView.findViewById(R.id.btnDetail);
    }
}
