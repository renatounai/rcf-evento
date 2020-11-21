package com.rcfotografia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rcfotografia.entity.EventoTipo;
import com.rcfotografia.exception.DuplicatedException;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.repository.EventoTipoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoEventoService{	
	public static final String NOME_REPEATED = "Já existe um tipo de ensaio com este nome";
	
	private final EventoTipoRepository repository;
	
	public EventoTipo insert(EventoTipo tipoEnsaio) {
		tipoEnsaio.setId(null);
		
		if (repository.existsByNome(tipoEnsaio.getNome())) {
			throw new DuplicatedException(NOME_REPEATED);
		}
		
		return repository.save(tipoEnsaio);
	}

	public EventoTipo update(EventoTipo tipoEnsaio) {
		if (repository.existsByNomeAndIdNot(tipoEnsaio.getNome(), tipoEnsaio.getId())) {
			throw new DuplicatedException(NOME_REPEATED);
		}
		
		return repository.save(tipoEnsaio);
	}
	
	public EventoTipo findById(Integer id) {
		return repository.findById(id).orElseThrow(NotFoundException::new);
	}

	public void delete(int tipoEnsaioId) {
		repository.delete(findById(tipoEnsaioId));		
	}

	public List<EventoTipo> findAll() {
		return repository.findAll();
	}
	
	
}
