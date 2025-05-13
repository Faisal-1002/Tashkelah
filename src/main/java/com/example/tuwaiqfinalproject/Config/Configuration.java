package com.example.tuwaiqfinalproject.Config;

import com.example.tuwaiqfinalproject.Service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@org.springframework.context.annotation.Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Configuration {

    private final MyUserDetailsService myUserDetailsService;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(myUserDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());

        return authenticationProvider;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests()
                // Permit All endpoints
                .requestMatchers(
                        "/api/v1/field/all",
                        "/api/v1/field/images/**",
                        "/api/v1/player/register",
                        "/api/v1/public-match/changeStatus/{publicMatchId}",
                        "/api/v1/organizer/register",
                        "/api/v1/sports/all",
                        "/api/v1/sports/get-by-id/{id}"
                        ).permitAll()
                // ADMIN endpoints
                .requestMatchers(
                        "/api/v1/auth/users",
                        "/api/v1/booking/all",
                        "/api/v1/booking/get-by-id/{id}",
                        "/api/v1/booking/delete/{id}",
                        "/api/v1/organizer/all",
                        "/api/v1/organizer/get-by-id/{id}",
                        "/api/v1/organizer/approve/{organizerId}",
                        "/api/v1/organizer/reject/{organizerId}",
                        "/api/v1/organizer/block/{organizerId}",
                        "/api/v1/player/all",
                        "/api/v1/player/get-by-id/{id}",
                        "/api/v1/player/delete/{id}",
                        "/api/v1/private-match/all",
                        "/api/v1/private-match/get-by-id/{id}",
                        "/api/v1/private-match/delete/{id}",
                        "/api/v1/public-match/all",
                        "/api/v1/public-match/getById/{id}",
                        "/api/v1/team/getAllTeam",
                        "/api/v1/team/get-by-id/{id}",
                        "/api/v1/slot/all",
                        "/api/v1/slot/getById/{id}",
                        "/api/v1/field/field/{id}",
                        "/api/v1/sports/add",
                        "/api/v1/sports/update/{id}",
                        "/api/v1/sports/delete/{id}"
                ).hasAuthority("ADMIN")
                // PLAYER endpoints
                .requestMatchers(
                        "/api/v1/booking/my",
                        "/api/v1/booking/update/{id}",
                        "/api/v1/booking/private-match/{privateMatchId}",
                        "/api/v1/booking/public-match/{publicMatchId}",
                        "/api/v1/booking/getBookingPublicMatch",
                        "/api/v1/emails/**",
                        "/api/v1/field/getBySportAndCity/{sportId}",
                        "/api/v1/field/getByDetailsSportAndCity/{sportId}",
                        "/api/v1/field/private-match/{privateMatchId}/assign-field/{fieldId}",
                        "/api/v1/payments/**",
                        "/api/v1/player/info",
                        "/api/v1/player/update",
                        "/api/v1/private-match/my-private-matches",
                        "/api/v1/private-match/update/{id}",
                        "/api/v1/private-match/create",
                        "/api/v1/public-match/my-public-matches",
                        "/api/v1/public-match/getTeams/{publicMatchId}",
                        "/api/v1/public-match/PlayWithPublicTeam/{publicId}/{teamId}",
                        "/api/v1/public-match/getMatchByTime/{sportId}/{fieldId}",
                        "/api/v1/public-match/checkout/{publicMatchId}/{teamId}",
                        "/api/v1/public-match/notifications/{bookingId}",
                        "/api/v1/slot/private-match/slots/{privateMatchId}",
                        "/api/v1/slot/private-match/{matchId}/assign-slots"
                ).hasAuthority("PLAYER")
                // ORGANIZER endpoints
                .requestMatchers(
                        "/api/v1/field/add/{sportId}",
                        "/api/v1/field/update/{fieldId}",
                        "/api/v1/field/delete/{fieldId}",
                        "/api/v1/field/organizer-fields",
                        "/api/v1/field/booked-slots/{fieldId}",
                        "/api/v1/field/available-slots/{fieldId}",
                        "/api/v1/organizer/info",
                        "/api/v1/organizer/update",
                        "/api/v1/organizer/delete",
                        "/api/v1/public-match/update/{id}",
                        "/api/v1/public-match/delete/{id}",
                        "/api/v1/public-match/field/{fieldId}/matches",
                        "/api/v1/public-match/matches/{fieldId}",
                        "/api/v1/team/update/{teamId}",
                        "/api/v1/team/delete/{teamId}",
                        "/api/v1/team/addTeamsForPublicMatch/{matchId}",
                        "/api/v1/slot/update/{id}",
                        "/api/v1/slot/delete/{id}",
                        "/api/v1/slot/field/{fieldId}/timeslots/create"
                ).hasAuthority("ORGANIZER")
                .and()
                .logout().logoutUrl("/api/v1/auth/logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .httpBasic();
        return httpSecurity.build();
    }
}
