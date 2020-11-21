package com.rcfotografia.dominio.tipoevento;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rcfotografia.entity.EventoTipo;
import com.rcfotografia.repository.EventoTipoRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class TipoEventoRepositoryTest {
	
	@Autowired
	private EventoTipoRepository repository;
	
	@Test
	@DisplayName("Deve retornar que existe")
	void existsByNomeAndIdNot() {
		EventoTipo entity = EventoTipo.builder().id(1).nome("Descrição").build();
		repository.save(entity);
		
		boolean exists = repository.existsByNomeAndIdNot("Descrição", 2);
		
		assertTrue(exists);
	}
	
	@Test
	@DisplayName("Deve retornar que não existe, pois está sendo utilizado o mesmo ID")
	void existsByNomeAndIdNotSameId() {
		EventoTipo entity = EventoTipo.builder().id(1).nome("Descrição").build();
		int id = repository.save(entity).getId();
		
		boolean exists = repository.existsByNomeAndIdNot("Descrição", id);
		
		assertFalse(exists);
	}
	
	@Test
	@DisplayName("Deve gerar um ID")
	void uniqueNome() {
		EventoTipo entity = EventoTipo.builder().nome("Descrição").build();
		EventoTipo saved = repository.save(entity);
		
		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isNotNull().isPositive();
	}
}
