package com.security.securitydemo.config;

import com.security.securitydemo.util.FidoAuthenticationConverter;
import com.security.securitydemo.util.FidoAuthenticationManager;
import com.security.securitydemo.util.FidoLoginSuccessHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;

/**
 * This class uses the Lambda style DSL see the following blog posts for more info
 * <p>
 * https://spring.io/blog/2019/11/21/spring-security-lambda-dsl
 * https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /*
     * see https://github.com/jzheaux/cve-2023-34035-mitigations/tree/main#mitigations
     */
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http, FidoAuthenticationManager fidoAuthenticationManager, MvcRequestMatcher.Builder mvc) throws Exception {


        final String[] permittedURLs = new String[]{"/api/v1/webauthn/register/**", "/api/v1/webauthn/login/**"};

        // Configure a generic authentication filter that knows how to log in a user using a fido
        // authentication token
        // the key thing about this code is the convertor which can take a http request and extract out
        // the fido
        // credential and the authentication manager that validates the fido credential.
        // the success handler defined below is for debug purposes so that we can see the full flow of
        // interaction
        // between the browser and the fido server that we are implementing in this sample normally you
        // would configure
        // success handler to go to a url after successfully logging in.

        // http.securityContext().requireExplicitSave(false);
        var authenticationFilter =
                new AuthenticationFilter(fidoAuthenticationManager, new FidoAuthenticationConverter());
        authenticationFilter.setRequestMatcher(new AntPathRequestMatcher("/fido/login"));
        authenticationFilter.setSuccessHandler(new FidoLoginSuccessHandler());
        authenticationFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());


        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .authorizeHttpRequests(authorization ->
                        authorization
                                .requestMatchers(permittedURLs)
                                .permitAll()
                                .requestMatchers(PathRequest
                                        .toStaticResources()
                                        .atCommonLocations())
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://localhost:4200")); // or Collections.singletonList("*")
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS configuration to all paths
        return source;
    }

}
