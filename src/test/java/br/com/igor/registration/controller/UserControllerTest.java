package br.com.igor.registration.controller;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import br.com.igor.registration.repositories.UserRespository;
import br.com.igor.registration.entities.User;
import br.com.igor.registration.entities.dto.UserDTO;
import br.com.igor.registration.services.UserService;
import br.com.igor.registration.services.UserServiceImpl;

@SpringBootTest
public class UserControllerTest {
	
	@InjectMocks
	private UserController userController;
	
	@Mock
	private UserRespository userRespository;
	@Mock
	private UserServiceImpl userService;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	
	private static final Integer ID = 3;
	private static final String NAME = "Andressa",
								EMAIL = "andressa@gmail.com",
								PASSWORD = "123456";
	private UserDTO userDTOExpected;
	private Page<UserDTO> page;
	private Optional<User> optionalUser;
	
	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		
		buildUser();
	}
	
	private void buildUser() {
		this.userDTOExpected = new UserDTO(ID, NAME, EMAIL, PASSWORD);
		this.optionalUser = Optional.of(new User(ID, NAME, EMAIL, PASSWORD));
	}
	
	@Test
	void findByIdUserPageableThenReturnUser() {  
		Mockito.when(userService.findById(Mockito.anyInt())).thenReturn(userDTOExpected);
		ResponseEntity<UserDTO> userDTOResult = userController.findById(ID);
		
		Assertions.assertEquals(this.userDTOExpected.getClass(), userDTOResult.getBody().getClass());
		Assertions.assertEquals(this.userDTOExpected, userDTOResult.getBody());
		Assertions.assertEquals(this.userDTOExpected.getId(), userDTOResult.getBody().getId());
		Assertions.assertEquals(this.userDTOExpected.getEmail(), userDTOResult.getBody().getEmail());
		Assertions.assertEquals(this.userDTOExpected.getName(), userDTOResult.getBody().getName());
		Assertions.assertEquals(this.userDTOExpected.getPassword(), userDTOResult.getBody().getPassword());
	}
}
