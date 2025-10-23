package io.felipepoliveira.fpmtoolkit.api.config

import io.felipepoliveira.fpmtoolkit.api.security.auth.AuthenticationFilter
import io.felipepoliveira.fpmtoolkit.beans.AppContext
import io.felipepoliveira.fpmtoolkit.beans.AppContextProvider
import io.felipepoliveira.fpmtoolkit.beans.CoreBeans
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cglib.core.Converter
import org.springframework.context.annotation.*
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


class ApiAppContextProvider : AppContextProvider {
    override fun getAppContext(): AppContext = AppContext.DEVELOPMENT
}

@ComponentScans(value = [
    ComponentScan("io.felipepoliveira.fpmtoolkit.api.config"),
    ComponentScan("io.felipepoliveira.fpmtoolkit.api.controllers"),
    ComponentScan("io.felipepoliveira.fpmtoolkit.api.security"),
])
@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@Import(value = [CoreBeans::class, ApiAppContextProvider::class])
class ApiConfiguration {

    @Autowired
    private lateinit var authenticationFilter: AuthenticationFilter


    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer()
        val endpointsMatcher = authorizationServerConfigurer.endpointsMatcher

        http
            .securityMatcher(endpointsMatcher)
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .csrf { it.ignoringRequestMatchers(endpointsMatcher) }
            .with(authorizationServerConfigurer, {

            })
            .oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }

        return http.build()
    }


    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.requestMatchers("/api/*/public/**", "/login/**").permitAll()
                    .anyRequest().authenticated()
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .accessDeniedHandler(customAccessDeniedHandler())
            }

        return http.build()
    }



    fun customAccessDeniedHandler(): AccessDeniedHandler {
        return AccessDeniedHandler { _: HttpServletRequest, response: HttpServletResponse, _: AccessDeniedException ->
            response.status = HttpStatus.FORBIDDEN.value()
            response.addHeader("X-Session-Auth-Role-Message", "Access Denied: You do not have sufficient permissions to access this resource.")
        }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*") // ✅ Allows all origins with credentials
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true // ✅ Works with allowedOriginPatterns
        configuration.exposedHeaders = listOf("X-Error", "X-Reason")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}