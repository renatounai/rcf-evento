package com.rcfotografia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DuplicatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public DuplicatedException(String mensagem) {
		super(mensagem);
	}

}
