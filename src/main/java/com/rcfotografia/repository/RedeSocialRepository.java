package com.rcfotografia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rcfotografia.entity.RedeSocial;

@Repository
public interface RedeSocialRepository extends JpaRepository<RedeSocial, Integer>{

	boolean existsByNome(String nome);

	boolean existsByNomeAndIdNot(String nome, Integer id);

}
