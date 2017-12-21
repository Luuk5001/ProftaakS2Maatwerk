package com.s21m.proftaaks2maatwerk.api;

import com.s21m.proftaaks2maatwerk.data.PhotoResult;

public interface ApiListener<T> {
    void sendPhotoSuccess(T result);
    void sendPhotoFailure(Exception e);
}
