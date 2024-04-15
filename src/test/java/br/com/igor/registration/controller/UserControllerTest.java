package br.com.igor.registration.controller;
import java.util.List;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import br.com.igor.registration.entities.User;
import br.com.igor.registration.entities.dto.UserDTO;
import br.com.igor.registration.repositories.UserRespository;
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
	
	private static final Integer ID = 4;
	private static final String NAME = "Andressa",
								EMAIL = "andressa@gmail.com",
								PASSWORD = "123456";
	private UserDTO userDTOExpected;
	private Page<UserDTO> page;
	private Optional<User> optionalUser;
	private Pageable pageable;
	
	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		
		buildUser();
	}
	
	private void buildUser() {
		this.userDTOExpected = new UserDTO(ID, NAME, EMAIL, PASSWORD);
		this.optionalUser = Optional.of(new User(ID, NAME, EMAIL, PASSWORD));
		this.page = new PageImpl<>(List.of(userDTOExpected));
	}
	
	@Test
	void findByIdUserPageableThenReturnUser() {  
		Mockito.when(userService.findById(ID)).thenReturn(userDTOExpected);
		
		ResponseEntity<UserDTO> userDTOResult = userController.findById(ID);
		
		Assertions.assertAll(() -> Assertions.assertNotNull(userDTOResult),
							 () -> Assertions.assertEquals(this.userDTOExpected.getClass(), userDTOResult.getBody().getClass()),
							 () -> Assertions.assertEquals(this.userDTOExpected, userDTOResult.getBody()));
		Assertions.assertEquals(this.userDTOExpected.getId(), userDTOResult.getBody().getId());
		Assertions.assertEquals(this.userDTOExpected.getEmail(), userDTOResult.getBody().getEmail());
		Assertions.assertEquals(this.userDTOExpected.getName(), userDTOResult.getBody().getName());
		Assertions.assertEquals(this.userDTOExpected.getPassword(), userDTOResult.getBody().getPassword());
	}
	

	@Test
	void findAllUsersPageableThenReturnUserList() {  
		Mockito.when(userService.findAllPaged(Mockito.any())).thenReturn(page);
		
		ResponseEntity<Page<UserDTO>> userDTOResult = userController.findAll(PageRequest.of(0, 20));
		
		Assertions.assertAll(() -> Assertions.assertNotNull(userDTOResult),
							 () -> Assertions.assertTrue(this.page.getSize() > 0),
							 () -> Assertions.assertEquals(this.page.get().getClass(), userDTOResult.getBody().get().getClass()),
							 () -> Assertions.assertEquals(this.page, userDTOResult.getBody()));
		
		Assertions.assertEquals(this.page.get().findFirst().get().getId(), userDTOResult.getBody().get().findFirst().get().getId());
		Assertions.assertEquals(this.page.get().findFirst().get().getEmail(), userDTOResult.getBody().get().findFirst().get().getEmail());
		Assertions.assertEquals(this.page.get().findFirst().get().getName(), userDTOResult.getBody().get().findFirst().get().getName());
		Assertions.assertEquals(this.page.get().findFirst().get().getPassword(), userDTOResult.getBody().get().findFirst().get().getPassword());
	}
	
	
}
