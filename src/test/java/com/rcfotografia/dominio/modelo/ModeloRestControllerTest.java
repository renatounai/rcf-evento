package com.rcfotografia.dominio.modelo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.rcfotografia.dto.ModeloDto;
import com.rcfotografia.entity.Modelo;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.service.ModeloService;
import com.rcfotografia.web.controller.ModeloRestController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ModeloRestController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ModeloRestControllerTest {
	private static String API = "/modelos";
	
	@MockBean
	private ModeloService service;
	
	@Autowired
    MockMvc mvc;
	
	@Test
	@DisplayName("Deve criar um registro")	
	void create() throws Exception {
		ModeloDto dto = createNewModelo();		
		Modelo modeloSaved = Modelo.builder().id(1).nome(dto.getNome()).build();		

		BDDMockito.given(service.save(Mockito.any(Modelo.class))).willReturn(modeloSaved);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value(dto.getNome()));
		
		verify(service, times(1)).save(Mockito.any());		
	}
	
	@Test
	@DisplayName("Deve ignorar o ID passado como parâmetro e criar um registro")	
	void createIgnoringId() throws Exception {
		ModeloDto dto = createNewModelo();		
		dto.setId(Integer.MAX_VALUE);
		Modelo modeloSaved = Modelo.builder().id(1).nome(dto.getNome()).build();		

		BDDMockito.given(service.save(Mockito.any(Modelo.class))).willReturn(modeloSaved);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value(dto.getNome()));
		
		verify(service, times(1)).save(Mockito.any());		
	}
	
	@Test
	@DisplayName("Deve alterar um registro existente")	
	void update() throws Exception {
		ModeloDto dto = createNewModelo();		
		Modelo modeloSaved = Modelo.builder().id(1).nome("Facebook").build();
		Modelo entity = Modelo.builder().id(1).nome(dto.getNome()).build();

		BDDMockito.given(service.save(Mockito.any(Modelo.class))).willReturn(modeloSaved);
		BDDMockito.given(service.findById(1)).willReturn(entity);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.put(API + "/{id}", 1)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value(modeloSaved.getNome()));
		
		verify(service, times(1)).save(Mockito.any());		
	}
	
	@Test
	@DisplayName("Deve retornar 400 ao tentar criar um registro inválido inválido")
	void createInvalid() throws Exception{
		String json = new ObjectMapper().writeValueAsString(new ModeloDto());
		
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
	@DisplayName("Deve retornar 404 ao tentar alterar um registro não existente")	
	void updateNaoExistente() throws Exception {
		ModeloDto dto = createNewModelo();	
		dto.setId(9);
		
		BDDMockito.given(service.save(Mockito.any(Modelo.class))).willThrow(NotFoundException.class);
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
	@DisplayName("Deve excluir um registro")
	void delete() throws Exception {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(String.format("%s/%d", API, 9));
		
		mvc.perform(request)
		   .andExpect(status().isNoContent());
		
		Mockito.verify(service, times(1)).delete(9);			
	}

	@Test
	@DisplayName("Deve lançar 404 ao tentar excluir um registro não existente")
	void deleteNaoExistente() throws Exception {
		BDDMockito.willThrow(NotFoundException.class).given(service).delete(9);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(String.format("%s/%d", API, 9));
		
		mvc.perform(request)
		   .andExpect(status().isNotFound());
		
		Mockito.verify(service, times(1)).delete(9);			
	}
	
	@Test
	@DisplayName("Deve obter um registro pelo ID")
	void findById() throws Exception {
		BDDMockito.given(service.findById(1)).willReturn(Modelo.builder().id(1).nome("Facebook").build());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/{modeloId}", 1);
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("nome").value("Facebook"));
		
		verify(service, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Deve retornar 404 ao tentar obter um registro inexistente pelo ID")
	void findByIdInexistente() throws Exception {
		BDDMockito.given(service.findById(1)).willThrow(NotFoundException.class);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/{modeloId}", 1);
		
		mvc.perform(request)
		   .andExpect(status().isNotFound());
		
		verify(service, times(1)).findById(1);
	}
	
	public static ModeloDto createNewModelo()  {
        return ModeloDto.builder().nome("Facebook").build();
    }
}
