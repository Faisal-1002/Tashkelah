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
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests()
                
//                 .requestMatchers(
//                         "/api/v1/auth/register",
//                         "/api/v1/auth/login",
//                         "/api/v1/sports/all",
//                         "/api/v1/sports/{id}",
//                         "/api/v1/field/all",
//                         "/api/v1/field/images/**",
//                         "/api/v1/field/field/{id}",
//                         "/api/v1/organizer/register",
//                         "/api/v1/player/register"
//                 ).permitAll()

//                 // Admin-only endpoints
//                 .requestMatchers(
//                         "/api/v1/auth/users",
//                         "/api/v1/auth/register/admin",
//                         "/api/v1/auth/update/admin/{id}",
//                         "/api/v1/auth/delete/admin/{id}",
//                         "/api/v1/organizer/all",
//                         "/api/v1/organizer/approve/{organizerId}/{isApproved}",
//                         "/api/v1/organizer/reject/{organizerId}",
//                         "/api/v1/organizer/block/{organizerId}",
//                         "/api/v1/sports/add",
//                         "/api/v1/sports/update/{id}",
//                         "/api/v1/booking/all",
//                         "/api/v1/booking/{id}",
//                         "/api/v1/sports/delete/{id}"
//                 ).hasAuthority("ADMIN")

//                 // Organizer-only endpoints
//                 .requestMatchers(
//                         "/api/v1/organizer/info",
//                         "/api/v1/organizer/update",
//                         "/api/v1/organizer/delete",
//                         "/api/v1/field/add/{sport_id}",
//                         "/api/v1/field/update/{fieldId}",
//                         "/api/v1/field/delete/{fieldId}",
//                         "/api/v1/field/organizer-fields",
//                         "/api/v1/field/booked-slots/{fieldId}",
//                         "/api/v1/field/available-slots/{fieldId}",
//                         "/api/v1/public-match/addPublicMatch/{fieldId}/{timeSlotId}",
//                         "/api/v1/public-match/update/{id}",
//                         "/api/v1/public-match/delete/{id}",
//                         "/api/v1/public-match/field/{fieldId}/matches",
//                         "/api/v1/public-match/changeStatus/{publicMatchId}"
//                 ).hasAuthority("ORGANIZER")
//                 // Player-only endpoints
//                 .requestMatchers("/api/v1/player/info",
//                         "/api/v1/player/update",
//                         "/api/v1/player/delete",
//                         "/api/v1/field/getBySportAndCity/{sportId}",
//                         "/api/v1/field/getByDetailsSportAndCity/{sportId}",
//                         "/api/v1/field//choseField/{fieldId}/{sportId}",
//                         "/api/v1/field/private-match/assign-field/{fieldId}",
//                         "/api/v1/public-match/PlayWithPublicTeam/{sportId}/{fieldId}",
//                         "/api/v1/public-match/getMatchAndTeam/{sportId}/{fieldId}",
//                         "/api/v1/public-match/getTeams/{publicMatchId}",
//                         "/api/v1/public-match/selectTeam/{sportId}/{fieldId}/{matchId}/{teamId}",
//                         "/api/v1/public-match/chekout/{publicMatchId}/{teamId}",
//                         "/api/v1/public-match/not/{bookingId}",
//                 "/api/v1/booking/getBookingPublicMatch")
//                 .hasAuthority("PLAYER")

                // All
                .requestMatchers("**").permitAll()

//                // Public endpoints (no authentication required)
//                .requestMatchers(
//                        "/api/v1/auth/register",
//                        "/api/v1/auth/login",
//                        "/api/v1/sports/all",
//                        "/api/v1/sports/{id}",
//                        "/api/v1/field/all",
//                        "/api/v1/field/images/**",
//                        "/api/v1/field/field/{id}",
//                        "/api/v1/organizer/register",
//                        "/api/v1/player/register"
//                ).permitAll()
//


                 //                        "/api/v1/organizer/register",
                 //                        "/api/v1/organizer/approve/{organizerId}/{isApproved}",


                //Taha
//                .requestMatchers("/api/v1/field/add/{{sport_id}}","/api/v1/public-match/matches/{{fieldId}}/slots/{{slotIds}}").hasAuthority("ORGANIZER")



//                // Admin-only endpoints
//                .requestMatchers(
//                        "/api/v1/auth/users",
//                        "/api/v1/auth/register/admin",
//                        "/api/v1/auth/update/admin/{id}",
//                        "/api/v1/auth/delete/admin/{id}",
//                        "/api/v1/organizer/all",
//                        "/api/v1/organizer/approve/{organizerId}/{isApproved}",
//                        "/api/v1/organizer/reject/{organizerId}",
//                        "/api/v1/organizer/block/{organizerId}",
//                        "/api/v1/sports/add",
//                        "/api/v1/sports/update/{id}",
//                        "/api/v1/booking/all",
//                        "/api/v1/booking/{id}",
//                        "/api/v1/booking/delete/{id}",
//                        "/api/v1/sports/delete/{id}"
//                ).hasAuthority("ADMIN")
//
//                // Organizer-only endpoints
//                .requestMatchers(
//                        "/api/v1/organizer/info",
//                        "/api/v1/organizer/update",
//                        "/api/v1/organizer/delete",
//                        "/api/v1/field/add/{sport_id}",
//                        "/api/v1/field/update/{fieldId}",
//                        "/api/v1/field/delete/{fieldId}",
//                        "/api/v1/field/organizer-fields",
//                        "/api/v1/field/booked-slots/{fieldId}",
//                        "/api/v1/field/available-slots/{fieldId}",
//                        "/api/v1/public-match/addPublicMatch/{fieldId}/{timeSlotId}",
//                        "/api/v1/public-match/update/{id}",
//                        "/api/v1/public-match/delete/{id}",
//                        "/api/v1/public-match/field/{fieldId}/matches",
//                        "/api/v1/public-match/changeStatus/{publicMatchId}"
//                ).hasAuthority("ORGANIZER")
//
//                // Player-only endpoints
//                .requestMatchers(
//                        "/api/v1/player/info",
//                        "/api/v1/player/update",
//                        "/api/v1/player/delete",
//                        "/api/v1/field/getBySportAndCity/{sportId}",
//                        "/api/v1/field/choseField/{fieldId}/{sportId}",
//                        "/api/v1/field/private-match/assign-field/{fieldId}",
//                        "/api/v1/booking/my",
//                        "/api/v1/booking/update/{id}",
//                        "/api/v1/booking/book/private-match",
//                        "/api/v1/booking/book/publicMatch",
//                        "/api/v1/private-match/create/{fieldId}",
//                        "/api/v1/emails/add/{privateMatchId}",
//                        "/api/v1/emails/private-match/send-invites",
//                        "/api/v1/public-match/PlayWithPublicTeam/{sportId}/{fieldId}",
//                        "/api/v1/public-match/getMatchAndTeam/{sportId}/{fieldId}",
//                        "/api/v1/public-match/getTeams/{publicMatchId}",
//                        "/api/v1/public-match/selectTeam/{sportId}/{fieldId}/{matchId}/{teamId}",
//                        "/api/v1/public-match/chekout/{publicMatchId}/{teamId}",
//                        "/api/v1/public-match/not/{bookingId}",
//                        "/api/v1/payments/card",
//                        "/api/v1/payments/get-status/{id}",
//                        "/api/v1/payments/puy"
//                ).hasAuthority("PLAYER")
//
//                // Shared endpoints (both player and organizer)
//                .requestMatchers(
//                        "/api/v1/private-match/all",
//                        "/api/v1/private-match/{id}",
//                        "/api/v1/public-match/all",
//                        "/api/v1/public-match/getById/{id}",
//                        "/api/v1/emails/match/{privateMatchId}",
//                        "/api/v1/emails/delete/{emailId}"
//                ).hasAnyAuthority("PLAYER", "ORGANIZER")


                .anyRequest().authenticated()
                .and()
                .logout().logoutUrl("/api/v1/auth/logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .httpBasic();
        return httpSecurity.build();
    }
}
