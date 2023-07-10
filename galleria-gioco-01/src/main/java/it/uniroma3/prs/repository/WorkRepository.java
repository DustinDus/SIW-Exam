package it.uniroma3.prs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.prs.model.Work;

public interface WorkRepository extends CrudRepository<Work, Long> {
	
	public List<Work> findAllByOrderByDateAsc();
	public List<Work> findByNameContainingIgnoreCase(String input);
	
	public boolean existsByNameAndDate(String name, Integer date);
	public boolean existsByMovementNull();
	
	// Galleria
	public List<Work> findByMovementNullOrderByDateAsc();
	
	// Gioco
	@Query(value="SELECT * "
			   + "FROM work a "
			   + "ORDER BY random() "
			   + "LIMIT 1", nativeQuery=true)
	public List<Work> findRandomWork(); 
	
}
