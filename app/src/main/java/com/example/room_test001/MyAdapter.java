package com.example.room_test001;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room_test001.database.DataBase;
import com.example.room_test001.database.MyData;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<com.example.room_test001.MyAdapter.ViewHolder> {
    private List<MyData> myData;
    private Activity activity;
    private MyAdapter.OnItemClickListener onItemClickListener;

    public MyAdapter(Activity activity) {
        this.activity = activity;


    }
    public void setData(List<MyData> myData){this.myData = myData;notifyDataSetChanged();}
    /**建立對外接口*/
    public void setOnItemClickListener(MyAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(android.R.id.text1);
            view = itemView;
        }
    }
    /**更新資料*/
    public void refreshView() {
        new Thread(()->{
            List<MyData> data = DataBase.getInstance(activity).getDataUao().displayAll();
            this.myData = data;
            activity.runOnUiThread(() -> {
                notifyDataSetChanged();
            });
        }).start();
    }
    /**刪除資料*/
    public void deleteData(int position){
        new Thread(()->{
            DataBase.getInstance(activity).getDataUao().deleteData(myData.get(position).getId());
            activity.runOnUiThread(()->{
                notifyItemRemoved(position);
                refreshView();
            });
        }).start();
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, null);
        return new com.example.room_test001.MyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        holder.tvTitle.setText(myData.get(position).getName());
        holder.view.setOnClickListener((v)->{
            onItemClickListener.onItemClick(myData.get(position));
        });

    }
    @Override
    public int getItemCount() {
        return myData.size();
    }
    /**建立對外接口*/
    public interface OnItemClickListener {
        void onItemClick(MyData myData);
    }

}
