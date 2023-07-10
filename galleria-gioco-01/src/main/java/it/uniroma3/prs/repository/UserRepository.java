package it.uniroma3.prs.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.prs.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
