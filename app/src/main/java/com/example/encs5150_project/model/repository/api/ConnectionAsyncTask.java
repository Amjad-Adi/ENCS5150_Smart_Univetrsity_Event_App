package com.example.encs5150_project.model.repository.api;

import static com.example.encs5150_project.view.constants.Introduction.API_FAIL;

import android.os.AsyncTask;

import com.example.encs5150_project.model.observer.APIFetchObserver;
import com.example.encs5150_project.model.entity.Event;

import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String,String ,String> {
    //Better than slides implementation, if other ActivityView needs to use ConnectionAsyncTask, then it won't need to strictly use the same widgets, loosely coupled classes
    private APIFetchObserver observer;

    public ConnectionAsyncTask(APIFetchObserver observer) {
        this.observer = observer;
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
        if (string == null || string.isEmpty()) {
            if (observer != null) {
                observer.onFetchFailure(API_FAIL);
            }
            return;
        }
        super.onPostExecute(string);
        List<Event> eventList = EventJsonParser.ParseEvent(string);
        if(observer!=null)
            observer.onFetchSuccess(eventList);
    }
}
