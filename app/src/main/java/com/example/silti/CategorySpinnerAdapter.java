package com.example.silti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CategorySpinnerAdapter<T> extends ArrayAdapter<T> {

    private List<T> items;
    private OnItemNameProvider<T> nameProvider;

    public interface OnItemNameProvider<T> {
        String getName(T item);
    }

    public CategorySpinnerAdapter(@NonNull Context context, @NonNull List<T> items, OnItemNameProvider<T> nameProvider) {
        super(context, android.R.layout.simple_spinner_item, items);
        this.items = items;
        this.nameProvider = nameProvider;
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        T item = getItem(position);
        if (item != null) {
            textView.setText(nameProvider.getName(item));
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        T item = getItem(position);
        if (item != null) {
            textView.setText(nameProvider.getName(item));
        }
        return convertView;
    }
}