package it.uniroma3.prs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.prs.model.Score;
import it.uniroma3.prs.repository.ScoreRepository;

@Service
public class ScoreService {

	@Autowired ScoreRepository scoreRepository;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Get Score(s)
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Iterable<Score> getScores() {
		return this.scoreRepository.findAllOrderedByPoints();
	}

	@Transactional
	public Iterable<Score> getTop10() {
		return this.scoreRepository.findTop10OrderedByPoints();
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// New Score
	//'''''''''''''''''''''''''''''''''''''''''''''''''''

	@Transactional
	public Score newScore(Integer points, String username) {
		Score score = new Score();
		score.setPlayer(username);
	    score.setPoints(points);
	    this.scoreRepository.save(score);
	    
	    return score;
	}
	
}
