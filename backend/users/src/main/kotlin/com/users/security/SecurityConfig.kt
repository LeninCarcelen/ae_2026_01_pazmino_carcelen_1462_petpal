package com.users.security

import com.users.logging.RequestLoggingFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val cognitoJwtAuthenticationConverter: CognitoJwtAuthenticationConverter,
    private val requestLoggingFilter: RequestLoggingFilter
) {

    companion object {
        const val ADMIN = "Administrator"
        const val VET = "Veterinarian"
        const val HAIRDRESSER = "Hairdresser"
        const val CLIENT = "Clients"
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority(ADMIN)
                    .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority(ADMIN)
                    .requestMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority(ADMIN, VET, HAIRDRESSER, CLIENT)
                    .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyAuthority(ADMIN, VET, HAIRDRESSER, CLIENT)
                    .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority(ADMIN, VET, HAIRDRESSER, CLIENT)
                    .anyRequest().authenticated()
            }
            .addFilterAfter(requestLoggingFilter, BearerTokenAuthenticationFilter::class.java)
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(cognitoJwtAuthenticationConverter)
                }
            }

        return http.build()
    }
}
