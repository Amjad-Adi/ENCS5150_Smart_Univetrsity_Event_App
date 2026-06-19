package com.example.encs5150_project.model.observer;

public interface FetchStatus {
    void fetchSuccess();
    void fetchFailure(int error);
}
