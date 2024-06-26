package br.com.igor.registration.services;

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
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import br.com.igor.registration.entities.User;
import br.com.igor.registration.entities.dto.UserDTO;
import br.com.igor.registration.exceptions.DataIntegrityViolationException;
import br.com.igor.registration.exceptions.ObjectNotFoundException;
import br.com.igor.registration.repositories.UserRespository;

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
	void whenFindByIdInvalidReturnAnObjectNotFoundException() {
		
		Mockito.when(userRepository.findById(1)).thenThrow(new ObjectNotFoundException("Usuário não encontrado"));
		
		try {
			userService.findById(66);

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
	void whenInsertDuplicatedUserThenThrowsDataIntegrityViolationException() {
		User userDuplicated = new User(2, NAME, EMAIL, PASSWORD);
		Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(userDuplicated));
		
		try {
			userService.insert(new UserDTO(userDuplicated));
			
		} catch (DataIntegrityViolationException e) {
			Assertions.assertEquals("E-mail já presente no banco de dados", e.getMessage());
		}
	}
	
	@Test
	void whenUpdateUserThenChangeDatabase() {
		Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
		
		UserDTO response = userService.update(ID.toString(), userDTO);
		
		Assertions.assertNotNull(response);
		Assertions.assertEquals(UserDTO.class, response.getClass());
		Assertions.assertEquals(userDTO, response);
		Assertions.assertEquals(ID, response.getId());
		Assertions.assertEquals(NAME, response.getName());
		Assertions.assertEquals(EMAIL, response.getEmail());
		Assertions.assertEquals(PASSWORD, response.getPassword());
	}
	
	@Test
	void WhenUpdateUserWrongIdThenChangeThrowsIllegalArgumentException() {
		Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
		
		String wrongId = "1";

		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.update(wrongId, userDTO), 
				"Review parameters and class's processing because should throw a Exception");
	}
	
	@Test
	void deleteByIdWithSuccess() {
		Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(optionalUser);
		Mockito.doNothing().when(userRepository).deleteById(Mockito.anyInt());
		
		userService.deleteById(ID, userDTO);
		
		Mockito.verify(userRepository, Mockito.times(1)).deleteById(ID);
	}
	
	@Test
	void deleteByIdWrongIdThenThrowsObjectNotFoundException() {
		Mockito.when(userRepository.findById(2))
			.thenThrow(new ObjectNotFoundException("O id informado não corresponde ao registrado no sistema"));
		
		Assertions.assertThrows(ObjectNotFoundException.class, () -> userService.deleteById(1, userDTO), 
				"Review parameters and class's processing because should throw a Exception");
	}
	
	@Test
	void deleteByIdWrongEmailThenThrowsIllegalArgumentException() {
		Mockito.when(userRepository.findById(Mockito.anyInt()))
        .thenReturn(Optional.of(new User(1, "Test", "test@test.com", "1234567")));
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> userService.deleteById(2, userDTO), 
				"Review parameters and class's processing because should throw a Exception");
	}
}
