package it.uniroma3.prs.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Favorites {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@OneToMany
	private List<Artist> artists;
	@OneToMany
	private List<Movement> movements;
	@OneToMany
	private List<Work> works;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Artist> getArtists(){
		return this.artists;
	}
	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}
	
	public List<Movement> getMovements(){
		return this.movements;
	}
	public void setMovements(List<Movement> movements) {
		this.movements = movements;
	}
	
	public List<Work> getWorks(){
		return this.works;
	}
	public void setWorks(List<Work> works) {
		this.works = works;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Favorites favorites = (Favorites) obj;
		return this.id == favorites.getId();
	}
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}
