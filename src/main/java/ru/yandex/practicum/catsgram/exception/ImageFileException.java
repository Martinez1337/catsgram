package ru.yandex.practicum.catsgram.exception;

public class ImageFileException extends RuntimeException {
    public ImageFileException(String msg) {
        super(msg);
    }

    public ImageFileException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
