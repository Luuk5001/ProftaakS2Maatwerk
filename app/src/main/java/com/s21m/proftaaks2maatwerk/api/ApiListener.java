package com.s21m.proftaaks2maatwerk.api;

public interface ApiListener<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
