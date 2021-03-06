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

import com.rcfotografia.dto.RedeSocialDto;
import com.rcfotografia.entity.RedeSocial;
import com.rcfotografia.service.RedeSocialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/redes-sociais")
@RequiredArgsConstructor
public class RedeSocialRestController extends BaseRestController {
	private final RedeSocialService service;
	private final ModelMapper modelMapper;
	
	@PostMapping
	public ResponseEntity<RedeSocialDto> save(@Valid @RequestBody RedeSocialDto dto) {
		RedeSocial redeSocial = modelMapper.map(dto, RedeSocial.class);
		redeSocial = service.insert(redeSocial);
		return new ResponseEntity<>(modelMapper.map(redeSocial, RedeSocialDto.class), HttpStatus.CREATED);
	}
	
	@PutMapping("/{redeSocialId}")
	public ResponseEntity<RedeSocialDto> update(@Valid @RequestBody RedeSocialDto dto, @PathVariable Integer redeSocialId) {
		RedeSocial redeSocial = service.findById(redeSocialId);
		modelMapper.map(dto, redeSocial);
		redeSocial = service.update(redeSocial);
		return new ResponseEntity<>(modelMapper.map(redeSocial, RedeSocialDto.class), HttpStatus.OK);
	}
	
	@DeleteMapping("/{redeSocialId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer redeSocialId) {
		service.delete(redeSocialId);		
	}
	
	@GetMapping("/{redeSocialId}")	
	public ResponseEntity<RedeSocialDto> findById(@PathVariable Integer redeSocialId) {
		return new ResponseEntity<>(modelMapper.map(service.findById(redeSocialId), RedeSocialDto.class), HttpStatus.OK);
	}
	
	@GetMapping	
	public ResponseEntity<List<RedeSocialDto>> findAll() {
		return write(service.findAll().stream().map(t -> modelMapper.map(t, RedeSocialDto.class)).collect(Collectors.toList()));
	}
	
	
	
}
