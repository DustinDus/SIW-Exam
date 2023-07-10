package it.uniroma3.prs.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.prs.model.Score;
import it.uniroma3.prs.service.GameService;
import it.uniroma3.prs.service.ScoreService;

@Controller
public class GameController {

	@Autowired private GameService gameService;
	@Autowired private ScoreService scoreService;
	private int score;
	private int check;
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Metodi per giocare
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/gameMenu")
	public String gameMenu(Model model) {
		if(this.score>0)
		    model.addAttribute("currentScore", this.score);
		if(!this.gameService.enoughEntities())
			return "notEnoughWorks.html";
		else
		    return "gameMenu.html";
	}
	
	@PostMapping("start")
	public String start(Model model) {
		this.score = 0;
		this.check = -1;
		return loading(model);
	}
	
	@PostMapping("/game")
	public String game(Model model) {
		// Check for reload
		this.check++;
		if(this.check!=this.score)
			return wrongChoice(model);
		
		this.gameService.newRound(model);
		
		return "round.html";
	}
	
	@PostMapping("/correctChoice")
	public String correctChoice(Model model) {
		this.score++;
		return loading(model);
	}
	
	@GetMapping("/loading")
	public String loading(Model model) {
		return "loading.html";
	}
	
	@GetMapping("/intermission")
	public String intermission(Model model) {
		model.addAttribute("currentScore", this.score);
		return "intermission.html";
	}
	
	@PostMapping("/wrongChoice")
	public String wrongChoice(Model model) {
		// Registra il punteggio se >0
		if(this.score>0) {
		    UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		    Score score = this.scoreService.newScore(this.score, userDetails.getUsername());
		    model.addAttribute("newScore", score);
		}
		
		// Mostro la top 10
		model.addAttribute("scores", this.scoreService.getTop10());
		
		this.score = 0;
		return "endGame.html";
	}
	
	//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	// Metodi per gestire punteggi
	//'''''''''''''''''''''''''''''''''''''''''''''''''''
	
	@GetMapping("/leaderboard")
	public String leaderboard(Model model) {
		model.addAttribute("scores", this.scoreService.getScores());
		return "leaderboard.html";
	}
	
	/*
	@PostMapping("/newScore")
	public String newScore(@Valid @ModelAttribute("score") Score score, Model model) {
		this.scoreRepository.save(score);
		model.addAttribute("scores", this.scoreRepository.findAllOrderedByPoints());
		return "leaderboard.html";
	}
	
	@GetMapping("/leaderboardTop10")
	public String leaderboardTop10(Model model) {
		model.addAttribute("scores", this.scoreRepository.findTop10OrderedByPoints());
		return "leaderboard.html";
	}
	*/
	
}
