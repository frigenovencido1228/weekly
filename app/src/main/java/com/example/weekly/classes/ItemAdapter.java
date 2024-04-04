package com.example.weekly.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weekly.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    Context context;
    ArrayList<Item> itemArrayList;
    OnItemClick onItemClick;

    public ItemAdapter(Context context, ArrayList<Item> itemArrayList, OnItemClick onItemClick) {
        this.itemArrayList = itemArrayList;
        this.onItemClick = onItemClick;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_items, parent, false);
        return new ItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.MyViewHolder holder, int position) {

        holder.tvName.setText(itemArrayList.get(position).getName().toUpperCase());
        holder.tvPrice.setText(itemArrayList.get(position).getPrice());

        long millisec = Long.parseLong(itemArrayList.get(position).getDate());
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date(millisec);
        holder.tvDate.setText(dateFormat.format(date).toString());

        holder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onItemClick.onItemClick(itemArrayList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDate;
        CardView itemContainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemContainer = itemView.findViewById(R.id.itemContainer);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    public interface OnItemClick {
        void onItemClick(Item item);
    }
}
