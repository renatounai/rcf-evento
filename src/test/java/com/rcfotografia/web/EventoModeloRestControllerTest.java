package com.rcfotografia.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcfotografia.dto.EventoModeloDto;
import com.rcfotografia.entity.Evento;
import com.rcfotografia.entity.EventoModelo;
import com.rcfotografia.entity.Modelo;
import com.rcfotografia.entity.embeddable.Periodo;
import com.rcfotografia.exception.DuplicatedException;
import com.rcfotografia.exception.NotFoundException;
import com.rcfotografia.service.EventoModeloService;
import com.rcfotografia.web.controller.BaseRestController;
import com.rcfotografia.web.controller.EventoModeloRestController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventoModeloRestController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EventoModeloRestControllerTest {
	private static String API = "/eventos-modelo";
	
	@MockBean
	private EventoModeloService service;
	
	@Autowired
    MockMvc mvc;
	
	@Test
	void metadata() {
		RequestMapping requestMapping = EventoModeloRestController.class.getAnnotation(RequestMapping.class);
		
		assertThat(new EventoModeloRestController(null, null)).isInstanceOf(BaseRestController.class);
		assertThat(EventoModeloRestController.class)
			.hasAnnotation(RestController.class);
		
		assertThat(requestMapping).isNotNull();
		assertThat(requestMapping.value()).hasSize(1).allMatch(value -> value.equals(API));
	}
	
	@Test
	@DisplayName("Deve criar um registro")	
	void create() throws Exception {
		EventoModeloDto dto = createNewEventoModelo();		
		
		Modelo modelo = Modelo.builder().id(1).nome("Mariana").build();
		Evento evento = Evento.builder().id(1).build();
		EventoModelo modeloEventoSaved = EventoModelo.builder()
				.id(1)
				.modelo(modelo)
				.evento(evento)
				.build();		

		BDDMockito.given(service.save(Mockito.any(EventoModelo.class))).willReturn(modeloEventoSaved);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("evento").exists())
			.andExpect(jsonPath("modelo.nome").value("Mariana"));
		
		verify(service, times(1)).save(Mockito.any());		
	}
	
	@Test
	@DisplayName("Deve alterar um registro existente")	
	void update() throws Exception {
		EventoModeloDto dto = createNewEventoModelo();		
		Modelo modelo = Modelo.builder().id(2).nome("Juliana").build();
		Evento evento = Evento.builder().id(1).build();
		EventoModelo eventoModeloSaved = EventoModelo.builder()
				.id(1)
				.modelo(modelo)
				.evento(evento)
				.build();
		
		EventoModelo entity = EventoModelo.builder().id(1).modelo(modelo).evento(evento).build();

		BDDMockito.given(service.save(Mockito.any(EventoModelo.class))).willReturn(eventoModeloSaved);
		BDDMockito.given(service.findById(1)).willReturn(entity);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.put(API + "/{id}", 1)
			.contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto));
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("modelo.nome").value(eventoModeloSaved.getModelo().getNome()))
			.andExpect(jsonPath("modelo.id").value(eventoModeloSaved.getModelo().getId()));
		
		verify(service, times(1)).save(Mockito.any());		
	}
	
	@Test
	@DisplayName("Deve retornar 400 ao tentar criar um registro inválido")
	void createInvalid() throws Exception{
		String json = new ObjectMapper().writeValueAsString(EventoModeloDto.builder().build());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API)
				.contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .content(json);
		
		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("modelo").exists())
			.andExpect(jsonPath("modelo").value("must not be null"))
			.andExpect(jsonPath("evento").exists())
			.andExpect(jsonPath("evento").value("must not be null"));
	}
	
	
	
	@Test
	@DisplayName("Deve retornar 400 ao tentar criar um registro com evento e modelo duplicado")
	void createDuplicated() throws Exception{
		EventoModeloDto dto = createNewEventoModelo();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(service.save(Mockito.any(EventoModelo.class))).willThrow(DuplicatedException.class);
		
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
	@DisplayName("Deve retornar 400 ao tentar alterar um registro com modelo e evento duplicado")
	void editDuplicated() throws Exception{
		EventoModeloDto dto = createNewEventoModelo();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(service.save(Mockito.any(EventoModelo.class))).willThrow(DuplicatedException.class);
		BDDMockito.given(service.findById(1)).willReturn(EventoModelo.builder().id(1).build());
		
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
	@DisplayName("Deve retornar 404 ao tentar alterar um registro não existente")	
	void updateNaoExistente() throws Exception {
		EventoModeloDto dto = createNewEventoModelo();	
		dto.setId(9);
		

		BDDMockito.given(service.save(Mockito.any(EventoModelo.class))).willThrow(NotFoundException.class);
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
				.delete(API + "/{id}", 9);
		
		mvc.perform(request)
		   .andExpect(status().isNoContent());
		
		Mockito.verify(service, times(1)).delete(9);			
	}
	
	

	@Test
	@DisplayName("Deve lançar 404 ao tentar excluir um reistro não existente")
	void deleteNaoExistente() throws Exception {
		BDDMockito.willThrow(NotFoundException.class).given(service).delete(9);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(API + "/{id}", 9);
		
		mvc.perform(request)
		   .andExpect(status().isNotFound());
		
		Mockito.verify(service, times(1)).delete(9);			
	}
	
	
	@Test
	@DisplayName("Deve obter um registro pelo ID")
	void findById() throws Exception {
		LocalDateTime inicio = LocalDateTime.now();
		LocalDateTime fim = inicio.plusHours(2);
		BDDMockito.given(service.findById(1)).willReturn(
				EventoModelo.builder()
					.id(1)
					.evento(Evento.builder().id(1).dataPrevista(
							Periodo.builder().inicio(inicio).fim(fim).build()).build())
					.modelo(Modelo.builder().id(1).nome("Mariana").build())
					.build());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/{id}", 1);
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(1))
			.andExpect(jsonPath("modelo.nome").value("Mariana"))			
			.andExpect(jsonPath("evento.dataPrevista.inicio").value(inicio.format(DateTimeFormatter.ISO_DATE_TIME)))
			.andExpect(jsonPath("evento.dataPrevista.fim").value(fim.format(DateTimeFormatter.ISO_DATE_TIME)));
		
		verify(service, times(1)).findById(1);
	}
	
	
	
	@Test
	@DisplayName("Deve retornar 404 ao tentar obter um registro inexistente pelo ID")
	void findByIdInexistente() throws Exception {
		BDDMockito.given(service.findById(1)).willThrow(NotFoundException.class);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/{id}", 1);
		
		mvc.perform(request)
		   .andExpect(status().isNotFound());
		
		verify(service, times(1)).findById(1);
	}
	
	
	
	@Test
	@DisplayName("Deve retornar uma lista com os modelos do evento filtrados pelo ID do evento")
	void findAll() throws Exception {
		BDDMockito.given(service.findByEventoId(1)).willReturn(Arrays.asList(
			EventoModelo.builder().id(1).build(),
			EventoModelo.builder().id(2).build(),
			EventoModelo.builder().id(3).build()
		));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/evento/{eventoId}", 1);
		
		mvc.perform(request)
		   .andExpect(status().isOk())
		   .andExpect(jsonPath("$").isArray())
		   .andExpect(jsonPath("$", Matchers.hasSize(3)));
		
		verify(service, times(1)).findByEventoId(1);
	}
	
	
	
	@Test
	@DisplayName("Deve retornar 204 caso não encontre nenhuma rede social cadastrado")
	void findAllAndFindsNothing() throws Exception {
		BDDMockito.given(service.findByEventoId(1)).willReturn(Collections.emptyList());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(API + "/evento/{eventoId}", 1);
		
		mvc.perform(request)
		   .andExpect(status().isNoContent());
		
		verify(service, times(1)).findByEventoId(1);
	}
	
	public static EventoModeloDto createNewEventoModelo()  {
        return EventoModeloDto.builder()
        		.modelo(Modelo.builder().id(1).nome("Mariana").build())
        		.evento(Evento.builder().id(1).build())
        		.build();
    }
}
