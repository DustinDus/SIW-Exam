package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;

public interface ArtistRepository extends CrudRepository<Artist, Long> {
    
	public List<Artist> findByNameContainingIgnoreCase(String name);
	
	public List<Artist> findBySurnameContainingIgnoreCase(String surname);
	
	@Query("SELECT a FROM Artist a WHERE LOWER(CONCAT(a.name, ' ', a.surname)) LIKE LOWER(CONCAT('%', ?1, '%'))")
	public List<Artist> findByNameAndSurname(String nameAndSurname);
	
	@Query("SELECT a FROM Artist a WHERE LOWER(CONCAT(a.surname, ' ', a.name)) LIKE LOWER(CONCAT('%', ?1, '%'))")
	public List<Artist> findBySurnameAndName(String surnameAndName);
	
	
	public boolean existsByNameAndSurnameAndBirth(String name, String surname, Integer birth);
	
}
