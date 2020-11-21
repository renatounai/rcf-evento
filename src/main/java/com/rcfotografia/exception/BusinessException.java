package com.rcfotografia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException{
	
	public BusinessException(String mensagem) {
		super(mensagem);
	}

	private static final long serialVersionUID = 1L;

}
