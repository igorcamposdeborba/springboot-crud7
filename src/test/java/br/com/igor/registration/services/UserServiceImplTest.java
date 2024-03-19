package br.com.igor.registration.services;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import br.com.igor.registration.entities.User;
import br.com.igor.registration.entities.dto.UserDTO;
import br.com.igor.registration.exceptions.ObjectNotFoundException;
import br.com.igor.registration.repositories.UserRespository;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class UserServiceImplTest {
	
	@InjectMocks // Instância real da classe que to testando
	private UserServiceImpl userService;
	
	@Mock // mockar respostas do repository
	private UserRespository userRepository;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;// criptografar senha
	
	private User user;
	private UserDTO userDTO;
	private Page<UserDTO> page;
	private Optional<User> optionalUser;
	
	private static final Integer ID = 3;
	private static final String NAME = "Andressa",
								EMAIL = "andressa@gmail.com",
								PASSWORD = "123456";
	
	@BeforeEach
	void setup() {
		// Inicializar mocks
		MockitoAnnotations.openMocks(this);
		
		buildUser(); // mockar objetos
	}
	
	private void buildUser() {
		this.user = new User(ID, NAME, EMAIL, PASSWORD);
		this.userDTO = new UserDTO(ID, NAME, EMAIL, PASSWORD);
		this.optionalUser = Optional.of(new User(ID, NAME, EMAIL, PASSWORD));
	}
	
	@Test
	void whenFindByIdThenReturnAnUser() {
		Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(optionalUser);
		
		UserDTO response = userService.findById(ID);
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(UserDTO.class, response.getClass());
	    Assertions.assertAll(() -> Assertions.assertEquals(ID, response.getId()),
	    		() -> Assertions.assertEquals(NAME, response.getName()),
	    		() -> Assertions.assertEquals(EMAIL, response.getEmail()),
	    		() -> Assertions.assertEquals(PASSWORD, response.getPassword()));
	}
	
	@Test
	void WhenFindByIdInvalidReturnAnObjectNotFoundException() {
		
		Mockito.when(userRepository.findById(Mockito.anyInt())).thenThrow(new ObjectNotFoundException("Usuário não encontrado"));
		
		try {
			userService.findById(Mockito.anyInt());

		} catch(ObjectNotFoundException e) {
			Assertions.assertEquals(ObjectNotFoundException.class, e.getClass());
			Assertions.assertEquals("Usuário não encontrado", e.getMessage());
		}
	}
	
	@Test
	void whenFindAllPagedUsersThenReturnAllUsers() {
		Mockito.when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user)));
		
		Pageable page = Mockito.mock(Pageable.class);
		Page<UserDTO> response = userService.findAllPaged(page);
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(UserDTO.class, response.getContent().get(0).getClass());
		Assertions.assertEquals(1, response.getSize());
		Assertions.assertEquals(userDTO, response.getContent().get(0));
		Assertions.assertEquals(EMAIL, response.getContent().get(0).getEmail());
		Assertions.assertEquals(NAME, response.getContent().get(0).getName());
		Assertions.assertEquals(PASSWORD, response.getContent().get(0).getPassword());
		Assertions.assertEquals(ID, response.getContent().get(0).getId());
	}
	
	@Test
	void whenInsertUserThenReturn201Created() {
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
		
		UserDTO response = userService.insert(userDTO);
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(UserDTO.class, response.getClass());
		Assertions.assertEquals(user.getId(), response.getId());
		Assertions.assertEquals(user.getName(), response.getName());
		Assertions.assertEquals(user.getEmail(), response.getEmail());
		Assertions.assertEquals(PASSWORD, response.getPassword());
	}
	
	@Test
	void update() {
		
	}
	
	@Test
	void deleteById() {
		
	}
	
	@Test
	void validateDuplicatedEmail() {
		
	}
	
	@Test
	void validateUpdateEmail() {
		
	}
}
