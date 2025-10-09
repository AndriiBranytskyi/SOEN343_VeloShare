package com.veloshare.application;

public final class Result<T> {

    private final T value;
    private final String error;

    private Result(T value, String error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> ok(T v) {
        return new Result<>(v, null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(null, msg);
    }

    public boolean isOk() {
        return error == null;
    }

    public T getValue() {
        return value;
    }

    public String getError() {
        return error;
    }
}
