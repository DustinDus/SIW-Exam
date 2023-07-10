package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.News;

public interface NewsRepository extends CrudRepository<News, Long> {

	public Iterable<News> findAllByOrderByTimeDesc();
	public Iterable<News> findByHeadlineContainingIgnoreCase(String headline);
	
	public Iterable<News>  findAllByTaggedMoviesNotNullOrderByTimeDesc();
	public Iterable<News> findByTaggedMoviesNotNullAndHeadlineContainingIgnoreCase(String headline);
	
	public Iterable<News>  findAllByTaggedArtistsNotNullOrderByTimeDesc();
	public Iterable<News> findByTaggedArtistsNotNullAndHeadlineContainingIgnoreCase(String headline);
	
}
