package com.rcfotografia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rcfotografia.entity.EventoModelo;

@Repository
public interface EventoModeloRepository extends JpaRepository<EventoModelo, Integer>{

}
