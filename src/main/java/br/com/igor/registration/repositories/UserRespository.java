package br.com.igor.registration.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.igor.registration.entities.User;

public interface UserRespository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

}
