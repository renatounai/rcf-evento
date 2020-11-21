package com.rcfotografia.entity.embeddable;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Periodo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LocalDateTime inicio;
	private LocalDateTime fim;
}
