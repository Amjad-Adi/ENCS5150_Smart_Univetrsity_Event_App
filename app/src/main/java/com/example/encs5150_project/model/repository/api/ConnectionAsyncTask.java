package com.example.encs5150_project.model.repository.api;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.repository.EventRepository;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;

import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String,String ,String> {
    Activity activity;
    public ConnectionAsyncTask(Activity activity) {
        this.activity = activity;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        return HttpManager.fetchData(strings[0]);

    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
       List<Event>eventList= EventJsonParser.ParseEvent(string);
       EventRepository eventRepository=new EventRepository(DataBaseHelper.getInstance(activity));
       for (Event event:eventList)
           eventRepository.insert(event);
    }
}
