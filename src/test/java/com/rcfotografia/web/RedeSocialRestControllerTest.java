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
import com.rcfotografia.dto.RedeSocialDto;
import com.rcfotografia.entity.RedeSocial;
import com.rcfotografia.exception.DuplicatedException;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.service.RedeSocialService;
import com.rcfotografia.web.controller.RedeSocialRestController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RedeSocialRestController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RedeSocialRestControllerTest {
	private static String API = "/redes-sociais";
	
	@MockBean
	private RedeSocialService service;
	
	@Autowired
    MockMvc mvc;
	
	@Test
	@DisplayName("Deve criar uma rede social")	
	void create() throws Exception {
		RedeSocialDto dto = createNewRedeSocial();		
		RedeSocial redeSocialSaved = RedeSocial.builder().id(1).nome(dto.getNome()).build();		

		BDDMockito.given(service.insert(Mockito.any(RedeSocial.class))).willReturn(redeSocialSaved);
		
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
	@DisplayName("Deve alterar uma rede social existente")	
	void update() throws Exception {
		RedeSocialDto dto = createNewRedeSocial();		
		RedeSocial redeSocialSaved = RedeSocial.builder().id(1).nome("Facebook").build();
		RedeSocial entity = RedeSocial.builder().id(1).nome(dto.getNome()).build();

		BDDMockito.given(service.update(Mockito.any(RedeSocial.class))).willReturn(redeSocialSaved);
		BDDMockito.given(service.findById(1)).willReturn(entity);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.put(API + "/{id}", 1)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value(redeSocialSaved.getNome()));
		
		verify(service, times(1)).update(Mockito.any());		
	}
	
	@Test
	@DisplayName("Deve retornar 400 ao tentar criar uma rede social inválido")
	void createInvalid() throws Exception{
		String json = new ObjectMapper().writeValueAsString(new RedeSocialDto(null, ""));
		
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
	@DisplayName("Deve retornar 400 ao tentar criar uma rede social com nome duplicado")
	void createDuplicated() throws Exception{
		RedeSocialDto dto = createNewRedeSocial();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(service.insert(Mockito.any(RedeSocial.class))).willThrow(DuplicatedException.class);
		
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
	@DisplayName("Deve retornar 400 ao tentar alterar uma rede social com nome duplicado")
	void editDuplicated() throws Exception{
		RedeSocialDto dto = createNewRedeSocial();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(service.update(Mockito.any(RedeSocial.class))).willThrow(DuplicatedException.class);
		BDDMockito.given(service.findById(1)).willReturn(RedeSocial.builder().id(1).nome("nome").build());
		
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
	@DisplayName("Deve retornar 404 ao tentar alterar uma rede social não existente")	
	void updateNaoExistente() throws Exception {
		RedeSocialDto dto = createNewRedeSocial();	
		dto.setId(9);
		

		BDDMockito.given(service.insert(Mockito.any(RedeSocial.class))).willThrow(NotFoundException.class);
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
	@DisplayName("Deve excluir uma rede social")
	void delete() throws Exception {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(String.format("%s/%d", API, 9));
		
		mvc.perform(request)
		   .andExpect(status().isNoContent());
		
		Mockito.verify(service, times(1)).delete(9);			
	}

	@Test
	@DisplayName("Deve lançar 404 ao tentar excluir uma rede social não existente")
	void deleteNaoExistente() throws Exception {
		BDDMockito.willThrow(NotFoundException.class).given(service).delete(9);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(String.format("%s/%d", API, 9));
		
		mvc.perform(request)
		   .andExpect(status().isNotFound());
		
		Mockito.verify(service, times(1)).delete(9);			
	}
	
	@Test
	@DisplayName("Deve obter uma rede social pelo ID")
	void findById() throws Exception {
		BDDMockito.given(service.findById(1)).willReturn(RedeSocial.builder().id(1).nome("Facebook").build());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/{redeSocialId}", 1);
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value("Facebook"));
		
		verify(service, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Deve retornar 404 ao tentar obter uma rede social inexistente pelo ID")
	void findByIdInexistente() throws Exception {
		BDDMockito.given(service.findById(1)).willThrow(NotFoundException.class);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/{redeSocialId}", 1);
		
		mvc.perform(request)
		   .andExpect(status().isNotFound());
		
		verify(service, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Deve retornar uma lista com todas as rede sociais cadastradas")
	void findAll() throws Exception {
		BDDMockito.given(service.findAll()).willReturn(Arrays.asList(
			RedeSocial.builder().id(1).nome("Facebook").build(),
			RedeSocial.builder().id(2).nome("Instagram").build(),
			RedeSocial.builder().id(3).nome("Tik Tok").build()
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
	@DisplayName("Deve retornar 204 caso não encontre nenhuma rede social cadastrado")
	void findAllAndFindsNothing() throws Exception {
		BDDMockito.given(service.findAll()).willReturn(Collections.emptyList());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API);
		
		mvc.perform(request)
		   .andExpect(status().isNoContent());
		
		verify(service, times(1)).findAll();
	}


	
	public static RedeSocialDto createNewRedeSocial()  {
        return RedeSocialDto.builder().nome("Facebook").build();
    }
}
