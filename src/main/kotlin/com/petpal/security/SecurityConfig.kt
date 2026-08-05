package com.petpal.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

/**
 * Roles del dominio (vienen del claim "cognito:groups" del JWT de Cognito).
 * Usamos los nombres de grupo TAL CUAL existen en el User Pool (no llevan
 * prefijo "ROLE_" porque el pool ya tenía estos grupos de una versión anterior):
 * - "Administrator": acceso total, incluye borrar cualquier recurso.
 * - "Veterinarian":  puede crear/editar citas y vacunas, y ver todo (no borrar).
 * - "Hairdresser":   mismos permisos que "Veterinarian" (crear/editar, no borrar).
 * - "Clients":       dueños de mascota, solo lectura (GET) sobre sus recursos.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val cognitoJwtAuthenticationConverter: CognitoJwtAuthenticationConverter
) {

    companion object {
        const val ADMIN = "Administrator"
        const val VET = "Veterinarian"
        const val HAIRDRESSER = "Hairdresser"
        const val OWNER = "Clients"
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/h2-console/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    ).permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(ADMIN)
                    .requestMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority(ADMIN, VET, HAIRDRESSER)
                    .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority(ADMIN, VET, HAIRDRESSER)
                    .requestMatchers(HttpMethod.PATCH, "/api/**").hasAnyAuthority(ADMIN, VET, HAIRDRESSER)
                    .requestMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority(ADMIN, VET, HAIRDRESSER, OWNER)
                    .anyRequest().authenticated()
            }
            .headers { it.frameOptions { frame -> frame.disable() } } // necesario para H2 console
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(cognitoJwtAuthenticationConverter)
                }
            }

        return http.build()
    }
}
