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

import com.rcfotografia.dto.TipoEventoDto;
import com.rcfotografia.entity.EventoTipo;
import com.rcfotografia.service.TipoEventoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tipo-ensaio")
@RequiredArgsConstructor
public class EventoTipoRestController extends BaseRestController {
	private final TipoEventoService service;
	private final ModelMapper modelMapper;
	
	@PostMapping
	public ResponseEntity<TipoEventoDto> save(@Valid @RequestBody TipoEventoDto dto) {
		EventoTipo tipoEnsaio = modelMapper.map(dto, EventoTipo.class);
		tipoEnsaio = service.insert(tipoEnsaio);
		return new ResponseEntity<>(modelMapper.map(tipoEnsaio, TipoEventoDto.class), HttpStatus.CREATED);
	}
	
	@PutMapping("/{tipoEnsaioId}")
	public ResponseEntity<TipoEventoDto> update(@Valid @RequestBody TipoEventoDto dto, @PathVariable Integer tipoEnsaioId) {
		EventoTipo tipoEnsaio = service.findById(tipoEnsaioId);
		modelMapper.map(dto, tipoEnsaio);
		tipoEnsaio = service.update(tipoEnsaio);
		return new ResponseEntity<>(modelMapper.map(tipoEnsaio, TipoEventoDto.class), HttpStatus.OK);
	}
	
	@DeleteMapping("/{tipoEnsaioId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer tipoEnsaioId) {
		service.delete(tipoEnsaioId);		
	}
	
	@GetMapping("/{tipoEnsaioId}")	
	public ResponseEntity<TipoEventoDto> findById(@PathVariable Integer tipoEnsaioId) {
		return new ResponseEntity<>(modelMapper.map(service.findById(tipoEnsaioId), TipoEventoDto.class), HttpStatus.OK);
	}
	
	@GetMapping	
	public ResponseEntity<List<TipoEventoDto>> findAll() {
		return write(service.findAll().stream().map(t -> modelMapper.map(t, TipoEventoDto.class)).collect(Collectors.toList()));
	}
	
}
