package com.rcfotografia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rcfotografia.entity.EventoTipo;

@Repository
public interface EventoTipoRepository extends JpaRepository<EventoTipo, Integer>{

	boolean existsByNome(String nome);
	
	boolean existsByNomeAndIdNot(String nome, Integer id);

}
