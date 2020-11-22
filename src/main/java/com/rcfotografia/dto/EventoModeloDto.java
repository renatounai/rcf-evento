package com.rcfotografia.dto;

import javax.validation.constraints.NotNull;

import com.rcfotografia.entity.Evento;
import com.rcfotografia.entity.Modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class EventoModeloDto {
	private Integer id;
	
	@NotNull
	private Modelo modelo;
	
	@NotNull
	private Evento evento;
}
