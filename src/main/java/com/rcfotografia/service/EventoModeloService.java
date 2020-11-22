package com.rcfotografia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rcfotografia.entity.EventoModelo;
import com.rcfotografia.repository.EventoModeloRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoModeloService {
	private final EventoModeloRepository repository;

	public EventoModelo save(EventoModelo eventoModelo) {
		return repository.save(eventoModelo);
	}

	public EventoModelo findById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(int id) {
		// TODO Auto-generated method stub
		
	}

	public List<EventoModelo> findByEventoId(Integer eventoId) {
		// TODO
		return null;
	}


}
