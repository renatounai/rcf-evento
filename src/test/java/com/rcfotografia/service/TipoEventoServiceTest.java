package com.rcfotografia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rcfotografia.entity.EventoTipo;
import com.rcfotografia.exception.DuplicatedException;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.repository.EventoTipoRepository;
import com.rcfotografia.service.TipoEventoService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class TipoEventoServiceTest {
	
	@MockBean
	private EventoTipoRepository repository;
	
	private TipoEventoService service;
	
	@BeforeEach
	void setup() {
		this.service = new TipoEventoService(repository);
	}
	
	
	@Test
	@DisplayName("Deve incluir um tipo de evento")
	void insert() {
		String nome = "Evento Boudoir";
		when(repository.existsByNome(nome)).thenReturn(false);
		
		EventoTipo tipoEvento = EventoTipo.builder().id(1).nome(nome).build();		
		service.insert(tipoEvento);
		
		verify(repository, Mockito.times(1)).save(tipoEvento);		
	}
	
	@Test
	@DisplayName("Não deve incluir um tipo de evento com nome repetido")
	void insertRepeated() {
		String nome = "Evento Boudoir";
		when(repository.existsByNome(nome)).thenReturn(true);
		
		EventoTipo tipoEvento = EventoTipo.builder().id(1).nome(nome).build();		
		
		DuplicatedException exception = assertThrows(DuplicatedException.class, () -> service.insert(tipoEvento));
		
		assertThat(exception).isNotNull().hasMessage(TipoEventoService.NOME_REPEATED);		
		verify(repository, Mockito.never()).save(tipoEvento);		
	}

	
	@Test
	@DisplayName("Deve alterar um tipo de evento existente")	
	void update() throws Exception {
		EventoTipo tipoEvento = EventoTipo.builder().id(1).nome("Nome alterado").build();	
		
		service.update(tipoEvento);
		
		verify(repository, Mockito.times(1)).save(tipoEvento);
	}
	
	@Test
	@DisplayName("Não deve alterar um tipo de evento existente com nome repetido")	
	void updateNomeRepetido() throws Exception {
		EventoTipo tipoEvento = EventoTipo.builder().id(1).nome("Evento Boudoir").build();
		when(repository.existsByNomeAndIdNot(tipoEvento.getNome(), 1)).thenReturn(true);
		
		DuplicatedException exception = assertThrows(DuplicatedException.class, () -> service.update(tipoEvento));
		
		assertThat(exception).isNotNull().hasMessage(TipoEventoService.NOME_REPEATED);		
		verify(repository, Mockito.never()).save(tipoEvento);
	}
	
	@Test
	@DisplayName("Deve encontrar um tipo de evento pelo Id")
	void findById() {
		EventoTipo tipoEventoFound = EventoTipo.builder().id(1).nome("Evento Boudoir").build();
		when(repository.findById(1)).thenReturn(Optional.of(tipoEventoFound));
		
		EventoTipo tipoEvento = service.findById(1);
		
		assertThat(tipoEvento).isNotNull();
		assertThat(tipoEvento.getNome()).isEqualTo(tipoEventoFound.getNome());		
		verify(repository, Mockito.times(1)).findById(1);
	}
	
	
	@Test
	@DisplayName("Deve lançar erro ao buscar por ID inexistente")
	void deveLancarErroBuscarTipoDeEventoInexistente() {
		when(repository.findById(1)).thenReturn(Optional.empty());
		
		NotFoundException e = assertThrows(NotFoundException.class, () -> service.findById(1));
		
		assertThat(e).isNotNull();
	}
	
	@Test
	@DisplayName("Deve excluir um tipo de evento pelo Id")
	void delete() {
		EventoTipo tipoEventoFound = EventoTipo.builder().id(1).nome("Evento Boudoir").build();
		when(repository.findById(1)).thenReturn(Optional.of(tipoEventoFound));
		
		service.delete(1);
		
		verify(repository, Mockito.times(1)).delete(Mockito.any());
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar excluir tipo de evento inexistente")
	void deleteInexistente() {
		when(repository.findById(1)).thenThrow(NotFoundException.class);
		
		NotFoundException e = assertThrows(NotFoundException.class, () -> service.delete(1));
		assertThat(e).isNotNull();
		
		
		verify(repository, Mockito.never()).delete(Mockito.any());
	}
	
	@Test
	@DisplayName("Deve buscar todos os registros cadastrados")
	void findAll() {
		when(repository.findAll()).thenReturn(Arrays.asList(
			EventoTipo.builder().id(1).nome("Evento Boudoir").build(),
			EventoTipo.builder().id(2).nome("Noivado").build(),
			EventoTipo.builder().id(3).nome("Debutante").build()
		));
		
		List<EventoTipo> lista = service.findAll();
		
		assertThat(lista).isNotNull().hasSize(3);
	}
	
	@Test
	@DisplayName("Não traz nenhum registro")
	void findAllReturnsNothing() {
		when(repository.findAll()).thenReturn(Collections.emptyList());
		
		List<EventoTipo> lista = service.findAll();
		
		assertThat(lista).isNotNull().isEmpty();
	}
	
}
