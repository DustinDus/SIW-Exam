package it.uniroma3.prs.configuration;

import static it.uniroma3.prs.model.Credentials.ADMIN_ROLE;

import javax.sql.DataSource;

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
                
                
                // chiunque può accedere alle pagine atte a visionare artisti/opere/movimenti registrati, inclusa la galleria
                .requestMatchers(HttpMethod.GET,"/gallery"
                		                       ,"/artists","/artists/**","/artistsIndex"
                		                       ,"/movements","/movements/**","/movementsIndex"
                		                       ,"/works","/works/**","/worksIndex").permitAll()
                .requestMatchers(HttpMethod.POST,"/searchArtists"
                		                        ,"/searchMovements"
                		                        ,"/searchWorks").permitAll()
                
                
                // solo gli utenti registrati possono giocare...
                .requestMatchers(HttpMethod.GET,"/gameMenu","/loading","/intermission","/leaderboard","/leaderboardTop10").authenticated()
                .requestMatchers(HttpMethod.POST,"/start","/game","/correctChoice","/wrongChoice","/newScore").authenticated()
                // ...Gestire user page/galleria personale
                .requestMatchers(HttpMethod.GET,"/userPage/**"
                		                       ,"/userGallery/**").authenticated()
                .requestMatchers(HttpMethod.POST,"/setAvatar/**"
                		                        ,"/addArtistToFavorites/**","/removeArtistFromFavorites/**"
                		                        ,"/addMovementToFavorites/**","/removeMovementFromFavorites/**"
                		                        ,"/addWorkToFavorites/**","/removeWorkFromFavorites/**").authenticated()
                // ...Commentare
                .requestMatchers(HttpMethod.GET,"/userComments/**").authenticated()
                .requestMatchers(HttpMethod.POST,"addCommentToArtist/**"
                		                        ,"addCommentToMovement/**"
                		                        ,"addCommentToWork/**").authenticated()
                

                // solo gli amministratori possono aggiungere nuovi artisti/opere/movimenti...
                .requestMatchers(HttpMethod.GET,"/formNewArtist"
                		                       ,"/formNewMovement"
                		                       ,"/formNewWork").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(HttpMethod.POST,"/newArtist"
                		                        ,"/newMovement"
                		                        ,"/newWork").hasAnyAuthority(ADMIN_ROLE)
                // ...O modificare quelli esistenti...
                .requestMatchers(HttpMethod.POST,"/manageArtistMovements/**","/addMovementToArtist/**","/removeMovementFromArtist/**"
                		                        ,"/manageArtistWorks/**","/addWorkToArtist/**","/removeWorkFromArtist/**"
                		                        ,"/manageMovementWorks/**","/addWorkToMovement/**","/removeWorkFromMovement/**"
                		                        ,"/manageMovementArtists/**","/addArtistToMovement/**","/removeArtistFromMovement/**"
                		                        ,"/manageWorkArtists/**","/addArtistToWork/**","/removeArtistFromWork/**"
                		                        ,"/changeWorkMovement/**","/setMovementToWork/**","/removeMovementFromWork/**").hasAnyAuthority(ADMIN_ROLE)
                // ...O accedere alla lista utenti e cancellare utenti registrati (amministratori esclusi)
                .requestMatchers(HttpMethod.GET,"/userList").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(HttpMethod.POST,"/deleteArtist/**").hasAnyAuthority(ADMIN_ROLE)
                
                
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