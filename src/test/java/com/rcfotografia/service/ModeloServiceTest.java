package com.rcfotografia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rcfotografia.entity.Modelo;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.repository.ModeloRepository;
import com.rcfotografia.service.ModeloService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ModeloServiceTest {
	
	@MockBean
	private ModeloRepository repository;
	
	private ModeloService service;
	
	@BeforeEach
	void setup() {
		this.service = new ModeloService(repository);
	}
	
	
	@Test
	@DisplayName("Deve incluir um registro")
	void insert() {
		Modelo modelo = Modelo.builder().id(1).nome("Mariana").build();
		
		service.save(modelo);
		
		verify(repository, Mockito.times(1)).save(modelo);		
	}
	
	
	@Test
	@DisplayName("Deve alterar um registro existente")	
	void update() throws Exception {
		Modelo modelo = Modelo.builder().id(1).nome("Nome alterado").build();	
		
		service.save(modelo);
		
		verify(repository, Mockito.times(1)).save(modelo);
	}
	
	@Test
	@DisplayName("Deve encontrar um registro pelo Id")
	void findById() {
		Modelo modeloFound = Modelo.builder().id(1).nome("Evento Boudoir").build();
		when(repository.findById(1)).thenReturn(Optional.of(modeloFound));
		
		Modelo modelo = service.findById(1);
		
		assertThat(modelo).isNotNull();
		assertThat(modelo.getNome()).isEqualTo(modeloFound.getNome());		
		verify(repository, Mockito.times(1)).findById(1);
	}
	
	
	@Test
	@DisplayName("Deve lançar erro ao buscar inexistente")
	void deveLancarErroBuscarTipoDeEventoInexistente() {
		when(repository.findById(1)).thenReturn(Optional.empty());
		
		NotFoundException e = assertThrows(NotFoundException.class, () -> service.findById(1));
		
		assertThat(e).isNotNull();
	}
	
	@Test
	@DisplayName("Deve excluir um registro pelo Id")
	void delete() {
		Modelo modeloFound = Modelo.builder().id(1).nome("Evento Boudoir").build();
		when(repository.findById(1)).thenReturn(Optional.of(modeloFound));
		
		service.delete(1);
		
		verify(repository, Mockito.times(1)).delete(Mockito.any());
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar excluir um registro inexistente")
	void deleteInexistente() {
		when(repository.findById(1)).thenThrow(NotFoundException.class);
		
		NotFoundException e = assertThrows(NotFoundException.class, () -> service.delete(1));
		assertThat(e).isNotNull();
		
		
		verify(repository, Mockito.never()).delete(Mockito.any());
	}
	
}
