package br.com.igor.registration.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.igor.registration.entities.dto.UserDTO;

public interface UserService {
	
	UserDTO findById(Integer id);
	
	Page<UserDTO> findAllPaged(Pageable pageable);
	
	UserDTO insert(UserDTO userDTO);
	
	UserDTO update(String id, UserDTO userDTO);
	
	void deleteById(Integer id, UserDTO userDTO);
}
