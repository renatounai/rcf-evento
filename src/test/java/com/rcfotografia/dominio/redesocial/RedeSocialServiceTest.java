package com.rcfotografia.dominio.redesocial;

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

import com.rcfotografia.entity.RedeSocial;
import com.rcfotografia.exception.DuplicatedException;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.repository.RedeSocialRepository;
import com.rcfotografia.service.RedeSocialService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class RedeSocialServiceTest {
	
	@MockBean
	private RedeSocialRepository repository;
	
	private RedeSocialService service;
	
	@BeforeEach
	void setup() {
		this.service = new RedeSocialService(repository);
	}
	
	
	@Test
	@DisplayName("Deve incluir uma rede social")
	void insert() {
		String nome = "Evento Boudoir";
		when(repository.existsByNome(nome)).thenReturn(false);
		
		RedeSocial redeSocial = RedeSocial.builder().id(1).nome(nome).build();		
		service.insert(redeSocial);
		
		verify(repository, Mockito.times(1)).save(redeSocial);		
	}
	
	@Test
	@DisplayName("Não deve incluir uma rede social com nome repetido")
	void insertRepeated() {
		String nome = "Evento Boudoir";
		when(repository.existsByNome(nome)).thenReturn(true);
		
		RedeSocial redeSocial = RedeSocial.builder().id(1).nome(nome).build();		
		
		DuplicatedException exception = assertThrows(DuplicatedException.class, () -> service.insert(redeSocial));
		
		assertThat(exception).isNotNull().hasMessage(RedeSocialService.NOME_REPEATED);		
		verify(repository, Mockito.never()).save(redeSocial);		
	}

	
	@Test
	@DisplayName("Deve alterar uma rede social existente")	
	void update() throws Exception {
		RedeSocial redeSocial = RedeSocial.builder().id(1).nome("Nome alterado").build();	
		
		service.update(redeSocial);
		
		verify(repository, Mockito.times(1)).save(redeSocial);
	}
	
	@Test
	@DisplayName("Não deve alterar uma rede social existente com nome repetido")	
	void updateNomeRepetido() throws Exception {
		RedeSocial redeSocial = RedeSocial.builder().id(1).nome("Evento Boudoir").build();
		when(repository.existsByNomeAndIdNot(redeSocial.getNome(), 1)).thenReturn(true);
		
		DuplicatedException exception = assertThrows(DuplicatedException.class, () -> service.update(redeSocial));
		
		assertThat(exception).isNotNull().hasMessage(RedeSocialService.NOME_REPEATED);		
		verify(repository, Mockito.never()).save(redeSocial);
	}
	
	@Test
	@DisplayName("Deve encontrar uma rede social pelo Id")
	void findById() {
		RedeSocial redeSocialFound = RedeSocial.builder().id(1).nome("Evento Boudoir").build();
		when(repository.findById(1)).thenReturn(Optional.of(redeSocialFound));
		
		RedeSocial redeSocial = service.findById(1);
		
		assertThat(redeSocial).isNotNull();
		assertThat(redeSocial.getNome()).isEqualTo(redeSocialFound.getNome());		
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
	@DisplayName("Deve excluir uma rede social pelo Id")
	void delete() {
		RedeSocial redeSocialFound = RedeSocial.builder().id(1).nome("Evento Boudoir").build();
		when(repository.findById(1)).thenReturn(Optional.of(redeSocialFound));
		
		service.delete(1);
		
		verify(repository, Mockito.times(1)).delete(Mockito.any());
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar excluir uma rede social inexistente")
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
			RedeSocial.builder().id(1).nome("Facebook").build(),
			RedeSocial.builder().id(2).nome("Instagram").build(),
			RedeSocial.builder().id(3).nome("Tik Tok").build()
		));
		
		List<RedeSocial> lista = service.findAll();
		
		assertThat(lista).isNotNull().hasSize(3);
	}
	
	@Test
	@DisplayName("Não traz nenhum registro")
	void findAllReturnsNothing() {
		when(repository.findAll()).thenReturn(Collections.emptyList());
		
		List<RedeSocial> lista = service.findAll();
		
		assertThat(lista).isNotNull().isEmpty();
	}
	
}
