package com.example.encs5150_project.controller;

import static com.example.encs5150_project.view.constants.Introduction.DATA_FAIL;

import android.database.sqlite.SQLiteException;

import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.observer.APIFetchObserver;
import com.example.encs5150_project.model.repository.EventRepository;
import com.example.encs5150_project.model.repository.api.ConnectionAsyncTask;
import com.example.encs5150_project.model.observer.FetchStatus;

import java.util.List;

public class IntroductionController implements APIFetchObserver {
    private final String SERVER_URL="https://mocki.io/v1/af1b6be0-61a9-464f-9d02-178bc5617b28";
    private EventRepository eventRepository;
    private FetchStatus fetchStatus;
    public static boolean successFetch=true;
    public IntroductionController(EventRepository eventRepository,FetchStatus fetchStatus) {
        this.eventRepository=eventRepository;
        this.fetchStatus = fetchStatus;
    }
    public void fetchData(){
            ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(this);
            connectionAsyncTask.execute(SERVER_URL);
    }
    public void storeData(List<Event> list){
        for(Event event:list)
            eventRepository.insert(event);
    }
    @Override
    public void onFetchSuccess(List<Event> list){
        try {
            storeData(list);
        }catch (SQLiteException e){
            fetchStatus.fetchFailure(DATA_FAIL);
        }
        fetchStatus.fetchSuccess();
    }
    @Override
    public void onFetchFailure(int error){
        fetchStatus.fetchFailure(error);
    }
}
