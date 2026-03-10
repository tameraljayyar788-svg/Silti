package com.example.silti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.silti.databinding.RvNotificationsBinding;

import java.util.List;

public class Adapter_Notification extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private Context context;
private List<table_notifications> notificationModelList;

    public Adapter_Notification(Context context, List<table_notifications> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @NonNull
@Override
public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    RvNotificationsBinding itemBinding = RvNotificationsBinding.inflate(LayoutInflater.from(context), parent, false);
    return new MyHolder(itemBinding);
}

@Override
public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

}

@Override
public int getItemCount() {
    return notificationModelList != null ? notificationModelList.size() : 0;
}

public class MyHolder extends RecyclerView.ViewHolder {
    RvNotificationsBinding binding;

    public MyHolder(RvNotificationsBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
}
