package it.uniroma3.siw.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .authoritiesByUsernameQuery("SELECT username, role from credentials WHERE username=?")
                .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }
    
    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf().and().cors().disable()
                .authorizeHttpRequests()
                // chiunque (autenticato o no) può accedere alle pagine index, login, register, ai css e alle immagini
                .requestMatchers(HttpMethod.GET,"/","/index","/register","/css/**", "/images/**", "/uploaded-images/**", "favicon.ico").permitAll()
        		// chiunque (autenticato o no) può mandare richieste POST al punto di accesso per login e register 
                .requestMatchers(HttpMethod.POST,"/register", "/login").permitAll()
                
                
                // chiunque può accedere alle pagine atte a visionare (e cercare) entita'
                .requestMatchers(HttpMethod.GET,"/indexArtist","/artists","/artists/**"
                		                       ,"/indexMovie","/movies","movies/**"
                		                       ,"/indexNews","/news","/movieNews","artistNews","/news/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/searchArtists"
                		                        ,"/searchMovies"
                		                        ,"/searchNews","/searchMovieNews","searchArtistNews").permitAll()
                
                
                // solo gli utenti registrati possono visitare pagine utente e scrivere recensioni
                .requestMatchers(HttpMethod.GET,"/userPage/**"
                		                       ,"/userReviews/**").authenticated()
                .requestMatchers(HttpMethod.POST,"/setAvatar/**"
                		                        ,"/addReview/**").authenticated()
                			
                
                // solo gli amministratori possono aggiungere/modificare entita'
                .requestMatchers(HttpMethod.GET,"/indexArtistAdmin.html"
                		                       ,"/formNewArtist"
                		                       ,"/manageArtist","/formUpdateArtist/**","/changeArtistData/**"
                		                       ,"/changeArtistImage/**"
                		                       
                		                       ,"/indexMovieAdmin.html"
                                               ,"/formNewMovie"
                                               ,"/manageMovies","/formUpdateMovie/**","/changeMovieData/**"
                                               ,"/manageMovieImages/**"
                                               ,"/updateDirectorOfMovie/**"
                                               ,"/updateActorsOnMovie/**"
                                               
                                               ,"/indexNewsAdmin.html"
                		                       ,"/formNewNews"
                		                       ,"/manageNews","/formUpdateNews/**").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(HttpMethod.POST,"/newArtist"
                		                        ,"/searchManageArtists"
                		                        ,"/setArtistName/**","/setArtistSurame/**","/setArtistBirth/**","/setArtistDeath/**"
                		                        ,"/setArtistImage/**"
                		                        ,"/deleteArtist/**"
                		                        
                		                        ,"/newMovie"
                		                        ,"/searchManageMovies"
                		                        ,"/setMovieTitle/**","/setMovieYear/**"
                		                        ,"/addImageToMovie/**","/deleteImageFromMovie/**"
                		                        ,"/setDirectorToMovie/**","/removeDirectorFromMovie/"
                		                        ,"/addActorToMovie/**","/removeActorFromMovie/**"
                		                        ,"/deleteMovie/**"
                		                        
                		                        ,"/deleteReview/**","/deleteReviewFromMovie/**"
                		                        
                		                        ,"/newNews"
                		                        ,"/searchManageNews"
                		                        ,"/deleteNews/**").hasAnyAuthority(ADMIN_ROLE)
                
                
                // Oltre ad accedere alla lista utenti e cancellare account
                .requestMatchers(HttpMethod.GET,"/userList").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(HttpMethod.POST,"/deleteUser/**").hasAnyAuthority(ADMIN_ROLE)
                
                				
        		// tutti gli utenti autenticati possono accere alle pagine rimanenti 
                .anyRequest().authenticated()
                
                
                .and().formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/success", true)
                .failureUrl("/login?error=true")
                
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .clearAuthentication(true).permitAll();
        return httpSecurity.build();
    }
}