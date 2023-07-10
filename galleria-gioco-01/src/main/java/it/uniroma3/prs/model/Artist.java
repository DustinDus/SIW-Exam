package it.uniroma3.prs.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Artist {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotBlank
	@Size(max=30)
	private String name;
	@NotNull
	@Max(2023)
	private Integer birth;
	@Column(nullable = true)
	@Max(2023)
	private Integer death;
	
	// Optional
	private String image;
	@Column(columnDefinition = "TEXT")
	private String shortBio;
	
	@ManyToMany(mappedBy="makers")
	private List<Work> works;
	@ManyToMany(mappedBy="artists")
	private List<Movement> movements;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getBirth() {
		return this.birth;
	}
	public void setBirth(Integer birth) {
		this.birth = birth;
	}
	
	public Integer getDeath() {
		return this.death;
	}
	public void setDeath(Integer death) {
		this.death = death;
	}
	
	public String getImage() {
		return this.image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	public String getShortBio() {
		return this.shortBio;
	}
	public void setShortBio(String shortBio) {
		this.shortBio = shortBio;
	}
	
	public List<Work> getWorks(){
		return this.works;
	}
	public void setWorks(List<Work> works) {
		this.works = works;
	}
	
	public List<Movement> getMovements(){
		return this.movements;
	}
	public void setMovements(List<Movement> movements) {
		this.movements = movements;
	}
	
	@Transient
    public String getImagePath() {
        if (this.image == null || this.id == null)
        	return null;
        else
            return "/src/main/resources/static/uploaded-images/artists/" + this.id + "/" + this.image;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist artist = (Artist) obj;
		return this.name == artist.getName() && this.birth == artist.getBirth();
	}
	@Override
	public int hashCode() {
		return this.name.hashCode() + this.birth;
	}
}
