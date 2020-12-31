package com.kubehelper.configs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @author JDev
 */
@ControllerAdvice
public class KubeHelperExceptionHandler{

    //TODO working, check
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleExceptions(RuntimeException exception, WebRequest webRequest) {
        ResponseEntity<Object> entity = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return entity;
    }
}
