package com.rcfotografia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rcfotografia.entity.RedeSocial;
import com.rcfotografia.exception.DuplicatedException;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.repository.RedeSocialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedeSocialService {
	public static final String NOME_REPEATED = "Já existe uma rede social com este nome";
	
	private final RedeSocialRepository repository;
	
	public RedeSocial insert(RedeSocial redeSocial) {
		redeSocial.setId(null);
		
		if (repository.existsByNome(redeSocial.getNome())) {
			throw new DuplicatedException(NOME_REPEATED);
		}
		
		return repository.save(redeSocial);
	}

	public RedeSocial update(RedeSocial redeSocial) {
		if (repository.existsByNomeAndIdNot(redeSocial.getNome(), redeSocial.getId())) {
			throw new DuplicatedException(NOME_REPEATED);
		}
		
		return repository.save(redeSocial);
	}
	
	public RedeSocial findById(Integer id) {
		return repository.findById(id).orElseThrow(NotFoundException::new);
	}

	public void delete(int redeSocialId) {
		repository.delete(findById(redeSocialId));		
	}

	public List<RedeSocial> findAll() {
		return repository.findAll();
	}

}
