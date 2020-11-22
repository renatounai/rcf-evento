package com.rcfotografia.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseRestController {

    protected <T> ResponseEntity<List<T>> write(List<T> list) {
        if (list.isEmpty()) {
            return new ResponseEntity<List<T>>(list, HttpStatus.NO_CONTENT) ;
        } else {
            return new ResponseEntity<List<T>>(list, HttpStatus.OK) ;
        }

    }
    
	
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(
	  MethodArgumentNotValidException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getAllErrors().forEach(error -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();
	        errors.put(fieldName, errorMessage);
	    });
	    
	    log.info(errors.toString());
	    return errors;
	}

}
