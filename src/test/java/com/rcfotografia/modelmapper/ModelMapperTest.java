package com.rcfotografia.modelmapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.rcfotografia.dto.ModeloDto;
import com.rcfotografia.entity.Modelo;
import com.rcfotografia.entity.embeddable.Usuario;

public class ModelMapperTest {
	
	@Test
	void deveMapearModeloParaModeloDto() {
		Modelo modelo = Modelo.builder()
			.id(1)
			.nome("Minha Modelo")
			.telefone("(38) 98807-5494")
			.usuario(Usuario.builder().email("modelo@gmail.com").senha("123").build())
			.build();
			
		ModeloDto modeloDto = new ModelMapper().map(modelo, ModeloDto.class);
		
		assertThat(modeloDto).isNotNull();
		assertThat(modelo.getNome()).isEqualTo(modeloDto.getNome());
		assertThat(modelo.getUsuario().getEmail()).isEqualTo(modeloDto.getUsuarioEmail());
	}
}
