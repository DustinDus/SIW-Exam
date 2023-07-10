package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class News {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotBlank
	@Size(max=100)
    private String headline;
	private LocalDate time;
	
	@NotBlank
	@Column(columnDefinition = "TEXT")
	private String text;
	private String image;
	
	@OneToMany
	private List<Movie> taggedMovies;
	@OneToMany
	private List<Artist> taggedArtists;
	
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getHeadline() {
		return this.headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	public LocalDate getTime() {
		return this.time;
	}
	public void setTime(LocalDate time) {
		this.time = time;
	}
	
	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public List<Movie> getTaggedMovies() {
		return this.taggedMovies;
	}
	public void setTaggedMovies(List<Movie> taggedMovies) {
		this.taggedMovies = taggedMovies;
	}
	
	public List<Artist> getTaggedArtists() {
		return this.taggedArtists;
	}
	public void setTaggedArtists(List<Artist> taggedArtists) {
		this.taggedArtists = taggedArtists;
	}
	
	public String getImage() {
    	return this.image;
    }
    public void setImage(String image) {
    	this.image = image;
    }
    
    @Transient
    public String getImagePath() {
        if (this.image == null || this.id == null)
        	return null;
        else
            return "/src/main/resources/static/uploaded-images/news/" + this.id + "/" + this.image;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		News news = (News) obj;
    	return this.id==news.getId();
    }
    @Override
    public int hashCode() {
    	return this.headline.hashCode() + this.time.hashCode();
    }
	
}
