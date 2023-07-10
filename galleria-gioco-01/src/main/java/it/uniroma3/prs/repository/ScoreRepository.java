package it.uniroma3.prs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.prs.model.Score;

public interface ScoreRepository extends CrudRepository<Score, Long> {
	
	@Query(value="select * "
			   + "from score s "
			   + "order by s.points desc", nativeQuery=true)
	public List<Score> findAllOrderedByPoints();
	
	@Query(value="select * "
			   + "from score s "
			   + "order by s.points desc "
			   + "limit 10", nativeQuery=true)
	public List<Score> findTop10OrderedByPoints();
	
	public boolean existsByPlayerAndPoints(String player, Integer points);
	
}
