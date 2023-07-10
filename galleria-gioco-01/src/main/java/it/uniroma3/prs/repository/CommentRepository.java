package it.uniroma3.prs.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.prs.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {
	
	public List<Comment> findByUsernameOrderByTimeDesc(String username);
	public List<Comment> findByTypeAndIdObjectOrderByTimeDesc(String type, Long idObject);
	
	public void deleteByTypeAndIdObject(String type, Long idObject);
	
}
