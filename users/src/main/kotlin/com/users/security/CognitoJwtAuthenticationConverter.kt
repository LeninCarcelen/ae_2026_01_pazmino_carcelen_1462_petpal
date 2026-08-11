package com.users.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component

@Component
class CognitoJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {

    private val defaultConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val defaultAuthorities: Collection<GrantedAuthority> = defaultConverter.convert(jwt) ?: emptyList()

        val groupAuthorities: List<GrantedAuthority> = jwt.getClaimAsStringList("cognito:groups")
            ?.map { group -> SimpleGrantedAuthority(group) }
            ?: emptyList()

        val authorities = defaultAuthorities + groupAuthorities

        return JwtAuthenticationConverter().let {
            it.setJwtGrantedAuthoritiesConverter { authorities }
            it.convert(jwt)!!
        }
    }
}
