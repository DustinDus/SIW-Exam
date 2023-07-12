package it.uniroma3.prs.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames={"name","startDate"}) })
public class Movement {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@NotBlank
	@Size(max=30)
	private String name;
	@NotNull
	@Max(2023)
	private Integer startDate;
	@Column(nullable = true)
	@Max(2023)
	private Integer endDate;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@OneToMany(mappedBy="movement")
	private List<Work> works;
	@ManyToMany
	private List<Artist> artists;
	
	@OneToMany
	private List<Work> mainWorks;
	
	
	
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
	
	public Integer getStartDate() {
		return this.startDate;
	}
	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}
	
	public Integer getEndDate() {
		return this.endDate;
	}
	public void setEndDate(Integer endDate) {
		this.endDate = endDate;
	}
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Work> getWorks(){
		return this.works;
	}
	public void setWorks(List<Work> works) {
		this.works = works;
	}
	
	public List<Artist> getArtists(){
		return this.artists;
	}
	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}
	
	public List<Work> getMainWorks() {
		return this.mainWorks;
	}
	public void setMainWorks(List<Work> mainWorks) {
		this.mainWorks = mainWorks;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movement movement = (Movement) obj;
		return this.name == movement.getName() && this.startDate == movement.getStartDate() && this.endDate == movement.getEndDate();
	}
	@Override
	public int hashCode() {
		return this.name.hashCode() + this.startDate + this.endDate;
	}
}
