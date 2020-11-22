package com.rcfotografia.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rcfotografia.dto.EventoModeloDto;
import com.rcfotografia.entity.EventoModelo;
import com.rcfotografia.service.EventoModeloService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eventos-modelo")
public class EventoModeloRestController extends BaseRestController {
	private final EventoModeloService service;
	private final ModelMapper modelMapper;
	
	@PostMapping
	public ResponseEntity<EventoModeloDto> save(@Valid @RequestBody EventoModeloDto dto) {
		EventoModelo eventoModelo = modelMapper.map(dto, EventoModelo.class);
		eventoModelo = service.save(eventoModelo);
		return new ResponseEntity<>(modelMapper.map(eventoModelo, EventoModeloDto.class), HttpStatus.CREATED);
	}
	
	@PutMapping("/{eventoModeloId}")
	public ResponseEntity<EventoModeloDto> update(@Valid @RequestBody EventoModeloDto dto, @PathVariable Integer eventoModeloId) {
		EventoModelo eventoModelo = service.findById(eventoModeloId);
		modelMapper.map(dto, eventoModelo);
		eventoModelo = service.save(eventoModelo);
		return new ResponseEntity<>(modelMapper.map(eventoModelo, EventoModeloDto.class), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer id) {
		service.delete(id);		
	}

	@GetMapping("/{id}")	
	public ResponseEntity<EventoModeloDto> findById(@PathVariable Integer id) {
		return new ResponseEntity<>(modelMapper.map(service.findById(id), EventoModeloDto.class), HttpStatus.OK);
	}
	
	@GetMapping("/evento/{eventoId}")	
	public ResponseEntity<List<EventoModeloDto>> findByEvento(@PathVariable Integer eventoId) {
		List<EventoModelo> lista = service.findByEventoId(eventoId);
		return write(lista.stream().map(em -> modelMapper.map(em, EventoModeloDto.class)).collect(Collectors.toList()));
	}


}
