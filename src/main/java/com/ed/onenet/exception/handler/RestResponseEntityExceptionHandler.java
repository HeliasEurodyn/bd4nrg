package com.ed.onenet.exception.handler;

import com.ed.onenet.exception.common.SofiaException;
import com.ed.onenet.exception.login.LoginErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler{
    @ExceptionHandler(
            {LoginErrorException.class})
    public ResponseEntity<Map<String,String>> handleException(SofiaException exception){
        Map<String,String> response = new HashMap<>();
        response.put("code", exception.getCode());
        response.put("message", exception.getMessage());
        response.put("category",exception.getCategory());
        response.put("isVisible",Boolean.toString(exception.isVisible()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
