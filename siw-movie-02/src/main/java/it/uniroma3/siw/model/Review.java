package it.uniroma3.siw.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Review {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@NotBlank
	private String username; // Del commentatore
	
	@NotBlank
	@Size(max=100)
	private String headline;
	@Min(1)
	@Max(5)
	private Integer rating;
	@NotBlank
	@Column(columnDefinition = "TEXT")
	private String text;
	
	private LocalDate time;
	
	@OneToOne
	private User user;
	@OneToOne
	private Movie movie;
	
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getHeadline() {
		return this.headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	public Integer getRating() {
		return this.rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	
	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public LocalDate getTime() {
		return this.time;
	}
	public void setTime(LocalDate time) {
		this.time = time;
	}
	
	public User getUser() {
		return this.user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public Movie getMovie() {
		return this.movie;
	}
	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	@Transient
    public String getAvatarPath() {
        if (this.user == null || this.id == null)
        	return null;
        else
            return this.user.getAvatarPath();
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Review review = (Review) obj;
		return this.id==review.getId();
	}
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
}
