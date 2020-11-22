package com.rcfotografia.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcfotografia.dto.TipoEventoDto;
import com.rcfotografia.entity.EventoTipo;
import com.rcfotografia.exception.DuplicatedException;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.service.TipoEventoService;
import com.rcfotografia.web.controller.EventoTipoRestController;



@ExtendWith(SpringExtension.class)
@WebMvcTest(EventoTipoRestController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TipoEventoRestControllerTest {
	private static String API = "/tipo-ensaio";
	
	@MockBean
	private TipoEventoService service;
	
	@Autowired
    MockMvc mvc;
	
	@Test
	@DisplayName("Deve criar um tipo de evento")	
	void create() throws Exception {
		TipoEventoDto dto = createNewTipoEvento();		
		EventoTipo tipoEventoSaved = EventoTipo.builder().id(1).nome(dto.getNome()).build();		

		BDDMockito.given(service.insert(Mockito.any(EventoTipo.class))).willReturn(tipoEventoSaved);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value(dto.getNome()));
		
		verify(service, times(1)).insert(Mockito.any());		
	}
	
	@Test
	@DisplayName("Deve alterar um tipo de evento existente")	
	void update() throws Exception {
		TipoEventoDto dto = createNewTipoEvento();		
		EventoTipo tipoEventoSaved = EventoTipo.builder().id(1).nome("Noivado").build();
		EventoTipo entity = EventoTipo.builder().id(1).nome(dto.getNome()).build();

		BDDMockito.given(service.update(Mockito.any(EventoTipo.class))).willReturn(tipoEventoSaved);
		BDDMockito.given(service.findById(1)).willReturn(entity);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.put(API + "/{id}", 1)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value(tipoEventoSaved.getNome()));
		
		verify(service, times(1)).update(Mockito.any());		
	}
	
	@Test
	@DisplayName("Deve retornar 400 ao tentar criar um tipo de evento inválido")
	void createInvalid() throws Exception{
		String json = new ObjectMapper().writeValueAsString(new TipoEventoDto(null, ""));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API)
				.contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .content(json);
		
		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("nome").exists())
			.andExpect(jsonPath("nome").value("must not be blank"));
	}
	
	@Test
	@DisplayName("Deve retornar 400 ao tentar criar um tipo de evento com nome duplicado")
	void createDuplicated() throws Exception{
		TipoEventoDto dto = createNewTipoEvento();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(service.insert(Mockito.any(EventoTipo.class))).willThrow(DuplicatedException.class);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API)
				.contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .content(json);
		
		MvcResult result = mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andReturn();
		
		Exception resolvedException = result.getResolvedException();	
		assertThat(resolvedException)
			.isNotNull()
			.isInstanceOf(DuplicatedException.class);
			
	}
	
	@Test
	@DisplayName("Deve retornar 400 ao tentar alterar um tipo de evento com nome duplicado")
	void editDuplicated() throws Exception{
		TipoEventoDto dto = createNewTipoEvento();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(service.update(Mockito.any(EventoTipo.class))).willThrow(DuplicatedException.class);
		BDDMockito.given(service.findById(1)).willReturn(EventoTipo.builder().id(1).nome("nome").build());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(API + "/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .content(json);
		
		MvcResult result = mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andReturn();
		
		Exception resolvedException = result.getResolvedException();	
		assertThat(resolvedException)
			.isNotNull()
			.isInstanceOf(DuplicatedException.class);
			
	}
	

	
	@Test
	@DisplayName("Deve retornar 404 ao tentar alterar um evento não existente")	
	void updateNaoExistente() throws Exception {
		TipoEventoDto dto = createNewTipoEvento();	
		dto.setId(9);
		

		BDDMockito.given(service.insert(Mockito.any(EventoTipo.class))).willThrow(NotFoundException.class);
		BDDMockito.given(service.findById(dto.getId())).willThrow(NotFoundException.class);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.put(API + "/{id}", dto.getId())
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		MvcResult result = mvc.perform(request)
			.andExpect(status().isNotFound())
			.andReturn();
		
		Exception e = result.getResolvedException();		
		assertThat(e).isNotNull().isInstanceOf(NotFoundException.class);		
	}
	
	@Test
	@DisplayName("Deve excluir um tipo de evento")
	void delete() throws Exception {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(String.format("%s/%d", API, 9));
		
		mvc.perform(request)
		   .andExpect(status().isNoContent());
		
		Mockito.verify(service, times(1)).delete(9);			
	}

	@Test
	@DisplayName("Deve lançar 404 ao tentar excluir um tipo de evento não existente")
	void deleteNaoExistente() throws Exception {
		BDDMockito.willThrow(NotFoundException.class).given(service).delete(9);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(String.format("%s/%d", API, 9));
		
		mvc.perform(request)
		   .andExpect(status().isNotFound());
		
		Mockito.verify(service, times(1)).delete(9);			
	}
	
	@Test
	@DisplayName("Deve obter um tipo de evento pelo ID")
	void findById() throws Exception {
		BDDMockito.given(service.findById(1)).willReturn(EventoTipo.builder().id(1).nome("Evento Boudoir").build());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/{tipoEventoId}", 1);
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value("Evento Boudoir"));
		
		verify(service, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Deve retornar 404 ao tentar obter um tipo de evento inexistente pelo ID")
	void findByIdInexistente() throws Exception {
		BDDMockito.given(service.findById(1)).willThrow(NotFoundException.class);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/{tipoEventoId}", 1);
		
		mvc.perform(request)
		   .andExpect(status().isNotFound());
		
		verify(service, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Deve retornar uma lista com todos os tipos de evento cadastrados")
	void findAll() throws Exception {
		BDDMockito.given(service.findAll()).willReturn(Arrays.asList(
			EventoTipo.builder().id(1).nome("Boudoir").build(),
			EventoTipo.builder().id(2).nome("Casal").build(),
			EventoTipo.builder().id(3).nome("Gestante").build()
		));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API);
		
		mvc.perform(request)
		   .andExpect(status().isOk())
		   .andExpect(jsonPath("$").isArray())
		   .andExpect(jsonPath("$", Matchers.hasSize(3)));
		
		verify(service, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Deve retornar 204 caso não encontre nenhum tipo de evento cadastrado")
	void findAllAndFindsNothing() throws Exception {
		BDDMockito.given(service.findAll()).willReturn(Collections.emptyList());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API);
		
		mvc.perform(request)
		   .andExpect(status().isNoContent());
		
		verify(service, times(1)).findAll();
	}


	
	public static TipoEventoDto createNewTipoEvento()  {
        return TipoEventoDto.builder().nome("Evento Boudoir").build();
    }
}
