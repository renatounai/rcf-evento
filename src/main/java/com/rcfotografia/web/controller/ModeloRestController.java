package com.rcfotografia.web.controller;

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

import com.rcfotografia.dto.ModeloDto;
import com.rcfotografia.entity.Modelo;
import com.rcfotografia.service.ModeloService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/modelos")
public class ModeloRestController extends BaseRestController {
	private final ModeloService service;
	private final ModelMapper modelMapper;
	
	@PostMapping
	public ResponseEntity<ModeloDto> save(@Valid @RequestBody ModeloDto dto) {
		Modelo modelo = modelMapper.map(dto, Modelo.class);
		modelo = service.save(modelo);
		return new ResponseEntity<>(modelMapper.map(modelo, ModeloDto.class), HttpStatus.CREATED);
	}
	
	@PutMapping("/{modeloId}")
	public ResponseEntity<ModeloDto> update(@Valid @RequestBody ModeloDto dto, @PathVariable Integer modeloId) {
		Modelo modelo = service.findById(modeloId);
		modelMapper.map(dto, modelo);
		modelo = service.save(modelo);
		return new ResponseEntity<>(modelMapper.map(modelo, ModeloDto.class), HttpStatus.OK);
	}
	
	@DeleteMapping("/{modeloId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer modeloId) {
		service.delete(modeloId);		
	}
	
	@GetMapping("/{modeloId}")	
	public ResponseEntity<ModeloDto> findById(@PathVariable Integer modeloId) {
		return new ResponseEntity<>(modelMapper.map(service.findById(modeloId), ModeloDto.class), HttpStatus.OK);
	}
	
}
