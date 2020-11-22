package com.rcfotografia.repository;

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

import com.rcfotografia.entity.RedeSocial;
import com.rcfotografia.repository.RedeSocialRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class RedeSocialRepositoryTest {
	
	@Autowired
	private RedeSocialRepository repository;
	
	@Test
	@DisplayName("Deve retornar que existe")
	void existsByNomeAndIdNot() {
		RedeSocial entity = RedeSocial.builder().id(1).nome("Descrição").build();
		repository.save(entity);
		
		boolean exists = repository.existsByNomeAndIdNot("Descrição", 2);
		
		assertTrue(exists);
	}
	
	@Test
	@DisplayName("Deve retornar que não existe, pois está sendo utilizado o mesmo ID")
	void existsByNomeAndIdNotSameId() {
		RedeSocial entity = RedeSocial.builder().id(1).nome("Descrição").build();
		int id = repository.save(entity).getId();
		
		boolean exists = repository.existsByNomeAndIdNot("Descrição", id);
		
		assertFalse(exists);
	}
	
	@Test
	@DisplayName("Deve gera um ID")
	void uniqueNome() {
		RedeSocial entity = RedeSocial.builder().nome("Descrição").build();
		RedeSocial saved = repository.save(entity);
		
		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isNotNull().isPositive();
	}
}