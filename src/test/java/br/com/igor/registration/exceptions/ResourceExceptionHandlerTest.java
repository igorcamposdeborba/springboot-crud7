package br.com.igor.registration.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest
public class ResourceExceptionHandlerTest {
	
	@InjectMocks // classe que vou testar
	private ResourceExceptionHandler controllerExceptionHandler;
	
	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void whenObjectNotFoundExceptionThenReturnAResponseEntity() {
		ResponseEntity<StandardError> response = controllerExceptionHandler.objectNotFound(
				new ObjectNotFoundException("Usuário não encontrado"), new MockHttpServletRequest());
	
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.getBody());
		Assertions.assertEquals(ResponseEntity.class, response.getClass());
		Assertions.assertEquals(StandardError.class, response.getBody().getClass());
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assertions.assertEquals("Usuário não encontrado", response.getBody().getError());
	}
	
	@Test
	void whenDataIntegrityViolationExceptionThenReturnAResponseEntity() {
		ResponseEntity<StandardError> response = controllerExceptionHandler.dataIntegrityViolationException(
				new DataIntegrityViolationException("E-mail já presente no banco de dados"), new MockHttpServletRequest());
	
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.getBody());
		Assertions.assertEquals(ResponseEntity.class, response.getClass());
		Assertions.assertEquals(StandardError.class, response.getBody().getClass());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assertions.assertEquals("E-mail já presente no banco de dados", response.getBody().getError());
	}
	
	@Test
	void whenIllegalArgumentExceptionThenReturnAResponseEntity() {
		ResponseEntity<StandardError> response = controllerExceptionHandler.illegalArgumentException(
				new IllegalArgumentException("O id informado não corresponde ao registrado no sistema"), new MockHttpServletRequest());
	
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.getBody());
		Assertions.assertEquals(ResponseEntity.class, response.getClass());
		Assertions.assertEquals(StandardError.class, response.getBody().getClass());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assertions.assertEquals("O id informado não corresponde ao registrado no sistema", response.getBody().getError());
	}
	
	
}
