package br.com.igor.registration.controller;

import java.net.URI;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import br.com.igor.registration.entities.dto.UserDTO;
import br.com.igor.registration.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/user")
public class UserController {

	private static final String ID = "/{id}";
	
	@Autowired 
	UserService userService;
	
	
	@GetMapping(value = ID)
	public ResponseEntity<UserDTO> findById(@PathVariable Integer id){
		UserDTO userDTO = userService.findById(id);
		
		return ResponseEntity.ok().body(userDTO);
	}

	@GetMapping (value = {"/", ""}) // aceitar com ou sem barra / a requisição
	public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable){
		// Buscar paginação no service
		Page <UserDTO> page = userService.findAllPaged(pageable);
		
		// Retornar resposta paginada
		return ResponseEntity.ok().body(page);
	}
	
	@PostMapping
	public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserDTO userDTO){
		// Inserir pelo service no banco de dados
		UserDTO newUser = userService.insert(userDTO);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getId()).toUri();
		
		return ResponseEntity.created(uri).build(); // retornar status created 201 com uri do objeto criado
	}
	
	@PutMapping
	public ResponseEntity<UserDTO> update(@Valid @RequestParam(name = "user_id") String id, @Valid @RequestBody UserDTO userDTO){
		// Inserir pelo service no banco de dados
		UserDTO updatedUser = userService.update(id, userDTO);

		return ResponseEntity.ok().body(updatedUser); // retornar o usuário atualizado
	}
	
	@DeleteMapping (value = ID) // id no path da url
	public ResponseEntity<Void> delete(@Valid @PathVariable Integer id, @RequestBody UserDTO userDTO){
		userService.deleteById(id, userDTO);
		
		return ResponseEntity.noContent().build(); // retornar status created 201 com uri do objeto criado
	}
}
