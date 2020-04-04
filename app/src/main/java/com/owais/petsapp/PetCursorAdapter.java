package com.owais.petsapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.owais.petsapp.data.PetContract.PetEntry;

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // for creating a new list item view if required
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    // populating the new list item view by binding it
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textView1 = (TextView) view.findViewById(R.id.name);
        TextView textView2 = (TextView) view.findViewById(R.id.breed);

        String Name = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME));
        String Breed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED));

        if (TextUtils.isEmpty(Breed)) {
            Breed = context.getString(R.string.unknown_breed);
        }
        textView1.setText(Name);
        textView2.setText(Breed);
    }
}
