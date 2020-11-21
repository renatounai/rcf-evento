package com.rcfotografia.service;

import org.springframework.stereotype.Service;

import com.rcfotografia.entity.Modelo;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.repository.ModeloRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModeloService {
	private final ModeloRepository repository;
	

	public Modelo save(Modelo modelo) {
		return repository.save(modelo);
	}

	public Modelo findById(int id) {
		return repository.findById(id).orElseThrow(NotFoundException::new);
	}

	public void delete(int id) {
		repository.delete(findById(id));
	}

}
