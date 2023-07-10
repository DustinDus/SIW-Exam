package it.uniroma3.prs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.prs.model.Artist;

public interface ArtistRepository extends CrudRepository<Artist, Long> {
	
	public List<Artist> findAllByOrderByBirthAsc();
	public List<Artist> findByNameContainingIgnoreCase(String input);
	
	public boolean existsByNameAndBirth(String name, Integer birth);
	
	// Gioco
	@Query(value="SELECT * "
			   + "FROM artist a "
			   + "ORDER BY random() "
			   + "LIMIT 1", nativeQuery=true)
	public List<Artist> findRandomArtist(); 
	
}
