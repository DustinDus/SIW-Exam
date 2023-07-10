package it.uniroma3.prs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.prs.model.Movement;

public interface MovementRepository extends CrudRepository<Movement, Long> {
	
	public List<Movement> findAllByOrderByStartDateAsc();
	public List<Movement> findByNameContainingIgnoreCase(String input);
	
	public boolean existsByNameAndStartDate(String name, Integer startDate);
	
	// Gioco
	@Query(value="SELECT * "
			   + "FROM movement a "
			   + "ORDER BY random() "
			   + "LIMIT 1", nativeQuery=true)
	public List<Movement> findRandomMovement(); 
	
}
