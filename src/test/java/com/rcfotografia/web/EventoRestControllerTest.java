package com.rcfotografia.web;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.rcfotografia.service.EventoService;
import com.rcfotografia.web.controller.EventoTipoRestController;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventoTipoRestController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventoRestControllerTest {
	private static String API = "/evento";
	
	@MockBean
	private EventoService service;
	
	@Autowired
    MockMvc mvc;
	
/*	@Test
	@DisplayName("Deve criar um evento")	
	void create() throws Exception {
		TipoEventoDto dto = createNewEvento();		
		TipoEvento eventoSaved = Evento.builder().id(1).nome(dto.getNome()).build();		

		BDDMockito.given(service.insert(Mockito.any(TipoEvento.class))).willReturn(tipoEnsaioSaved);
		
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
*/
}
