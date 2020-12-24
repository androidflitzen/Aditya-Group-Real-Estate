package com.flitzen.adityarealestate_new.Task_Reminder.adapter;

import android.content.Context;
import android.database.Cursor;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Reminder.fragment.TaskListFragment;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnDeleteTask;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnEditTask;
import com.flitzen.adityarealestate_new.Task_Reminder.provider.TaskProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sumir on 26-05-2017.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>
{
    Cursor cursor;
    int titleColumnIndex;
    int notesColumnIndex;
    int idColumnIndex;
    int dateColumnIndex;


    public void swapCursor(Cursor c)
    {
        cursor = c;
        if(cursor != null) {
            cursor.moveToFirst();
            titleColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TITLE);
            notesColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_NOTES);
            idColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TASKID);
            dateColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_DATE_TIME);
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position)
    {
        final Context context = viewHolder.titleView.getContext();
        final long id = getItemId(position);

        cursor.moveToPosition(position);
        viewHolder.titleView.setText(cursor.getString(titleColumnIndex));
        viewHolder.notesView.setText(cursor.getString(notesColumnIndex));


       /* DateFormat dateFormat = DateFormat.getDateInstance();
        String dateForButton = dateFormat.format(cursor.getString(dateColumnIndex));
        viewHolder.dateView.setText(dateForButton);*/



        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(cursor.getString(dateColumnIndex)));


        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      //  Calendar cal = Calendar.getInstance();
        viewHolder.dateView.setText(dateFormat.format(calendar.getTime()));

        //viewHolder.dateView.setText(cursor.getString(dateColumnIndex));



        if(TaskListFragment.internetPresent)
           // Picasso.with(context).load(getImageUrlForTask(id)).into(viewHolder.imageView);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((OnEditTask) context).editTask(id);
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener(

        ) {
            @Override
            public boolean onLongClick(View view) {
                ((OnDeleteTask)context).showDialog(viewHolder.titleView.getText().toString(),id);
                return true;
            }
        });
    }


    @Override
    public long getItemId(int position)
    {
        cursor.moveToPosition(position);
        return cursor.getLong(idColumnIndex);
    }

    public static String getImageUrlForTask(long taskId) {
        return "http://lorempixel.com/600/400/nature/?fakeId=" + taskId;
    }

    @Override
    public int getItemCount() {
         return cursor!=null ? cursor.getCount() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView titleView;
        TextView notesView,dateView;
        ImageView imageView;

        public ViewHolder(CardView card)
        {
            super(card);
            cardView = card;
            titleView = (TextView)card.findViewById(R.id.text1);
            notesView = (TextView)card.findViewById(R.id.text2);
            dateView = (TextView)card.findViewById(R.id.text3);
            imageView = (ImageView)card.findViewById(R.id.image);
        }
    }
}
