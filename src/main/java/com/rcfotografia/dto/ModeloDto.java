package com.rcfotografia.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class ModeloDto {
	private Integer id;
	
	@NotBlank
	private String nome;
	
	private String telefone;
	
	private String usuarioEmail;
}
