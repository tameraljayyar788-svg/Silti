package com.example.silti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            // رسالة المستخدم
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
        } else {
            // رسالة المساعد
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_assistant, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.tvMessage.setText(message.getMessage());
        holder.tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                tvMessage = itemView.findViewById(R.id.tv_user_message);
                tvTime = itemView.findViewById(R.id.tv_user_time);
            } else {
                tvMessage = itemView.findViewById(R.id.tv_assistant_message);
                tvTime = itemView.findViewById(R.id.tv_assistant_time);
            }
        }
    }
}
