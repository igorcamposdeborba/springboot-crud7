package br.com.igor.registration.services;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.igor.registration.entities.User;
import br.com.igor.registration.entities.dto.UserDTO;
import br.com.igor.registration.exceptions.DataIntegrityViolationException;
import br.com.igor.registration.exceptions.ObjectNotFoundException;
import br.com.igor.registration.repositories.UserRespository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRespository userRespository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;// criptografar senha
	
	@Override
	@Transactional(readOnly = true) // ReadOnly true não trava o banco (boa prática em operações de leitura). Transação sempre executa esta operação no banco de dados se for 100% de sucesso. 
	@Cacheable(value = "userById")
	public UserDTO findById(Integer id) {
		// Buscar no banco de dados
		Optional<User> userDTO = userRespository.findById(id); 
		
		// Exception de validação
		User entity = userDTO.orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
		
		return new UserDTO(entity); // retornar somente dados permitidos (mapeados) pelo DTO
	}

	@Override
	@Transactional(readOnly = true) // ReadOnly true não trava o banco (boa prática em operações de leitura)
	@Cacheable(value = "allUsers") // habilita cache no lado do servidor
	@CacheEvict(value = "allUsers", allEntries = true)
	public Page<UserDTO> findAllPaged(Pageable pageable){

		// Consultar banco de dados para User
		Page<User> list = userRespository.findAll(pageable);

		// Converter User em DTO
		List<UserDTO> listDTO = list.stream().map(UserDTO::new).collect(Collectors.toList());

		// Converter DTO em Page
		Page<UserDTO> page = new PageImpl<>(listDTO, pageable, list.getTotalElements());

		// Retornar paginado o Page
		return page;
	}
	
	@Override
	@Transactional
	@Cacheable(value = "insertUser")
	public UserDTO insert(UserDTO userDTO) {
		// Exception para validar se já existe no banco de dados
		validateDuplicatedEmail(userDTO.getEmail());
		
		// Mapear DTO para classe
		User entity = new User(userDTO);
		
		// Criptografar senha
		String encoded = passwordEncoder.encode(userDTO.getPassword()); // criptografar senha que será salva no banco. Decriptografia pela biblioteca BCrypt neste service

		if (Objects.isNull(encoded)) { // gerar senha aleatória se não vier a senha. Usuário deverá usar serviço de recuperar senha
			SecureRandom randomPassword = new SecureRandom();
			encoded = randomPassword.generateSeed(1000).toString();
		}
		entity.setPassword(encoded);
		
		entity = userRespository.save(entity); // salvar no banco de dados
		return new UserDTO(entity); // retornar o que foi salvo no banco de dados
	}
	
	@Override
	@Transactional
	@CachePut(value = "userById", key = "#idInput") // CachePut: atualiza cache
	public UserDTO update(String idInput, UserDTO userDTO) {
		// Converter String para Integer id
		Integer id = Integer.parseInt(idInput);

		// Busca lazy no banco de dados via endereço de memória, sem retornar toda a linha do banco
		Optional<User> user = validateUpdateEmail(userDTO.getEmail(), id);

		// Validar se o id passado é o mesmo que está no banco de dados para evitar que o usuário altere o id
		if (user.get().getId().equals(id)) {
			userDTO.setId(id);
		}

		// Mapear DTO para classe
		User entity = new User(userDTO);

		// Salvar no banco de dados
		userRespository.save(entity);

		// Retornar para a requisição o User atualizado
		return new UserDTO(entity);
	}
	
	@Transactional
	@CacheEvict(value = "userById", key = "#id") // CacheEvict invalida (deleta) o cache para não entregar a versão desatualizada
	public void deleteById(Integer id, UserDTO userDTO) {
		
		// Validar com exception se id não for encontrado
		UserDTO userEntity = findById(id);
		
		if ( !Objects.equals(userEntity.getEmail(), userDTO.getEmail())) {
			throw new IllegalArgumentException("O id informado não corresponde ao registrado no sistema");
		}
		
		// Deletar no banco de dados
		userRespository.deleteById(id);
	}
	
	
	private void validateDuplicatedEmail(String email) {
		Optional<User> user = userRespository.findByEmail(email);
		
		if (user.isPresent()) {
			throw new DataIntegrityViolationException("E-mail já presente no banco de dados");
		}
	}
	
	private Optional<User> validateUpdateEmail(String email, Integer id) {
		Optional<User> user = userRespository.findByEmail(email);
		
		if (user.isEmpty() || ! Objects.equals(user.get().getId(), id)) {
			throw new IllegalArgumentException("O id informado não corresponde ao registrado no sistema");
		}
		
		return user;
	}
}
