package br.com.igor.registration.config;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.igor.registration.entities.User;
import br.com.igor.registration.repositories.UserRespository;

@Configuration
@Profile("test")
public class LocalConfig {
	
	@Autowired // injetar repository para usar os métodos para salvar no banco de dados
	UserRespository userRepository;
	
	@Bean // Spring gerencia método para ser instanciado/injetado (@Autowired) em qualquer classe
	public <T> Optional<T> startDB() throws Exception {
		User user1 = new User(null, "Igor", "igor@gmail.com", "$2a$12$NvlmLqNQrbiG/84Qw4kQYeC9g0YDyl9BvBko8FSu4hZbxk2H6aJh.");
		User user2 = new User(null, "Bruna", "bruna@hotmail.com", "$2a$12$NvlmLqNQrbiG/84Qw4kQYeC9g0YDyl9BvBko8FSu4hZbxk2H6aJh.");
		
	    return Optional.of((T) userRepository.saveAll(List.of(user1, user2)));
	}
}
