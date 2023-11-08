package uk.gov.pmrv.api.web.config.security;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.web.config.property.CorsProperties;
import uk.gov.pmrv.api.web.security.PmrvAuthenticationConverter;
import uk.gov.pmrv.api.web.security.RoleTypeClaimConverter;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final AuthorityService authorityService;
    private final UserRoleTypeService userRoleTypeService;

    public SecurityConfig(
            CorsProperties corsProperties,
            AuthorityService authorityService,
            UserRoleTypeService userRoleTypeService) {
        this.corsProperties = corsProperties;
        this.authorityService = authorityService;
        this.userRoleTypeService = userRoleTypeService;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .cors(Customizer.withDefaults())
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.httpStrictTransportSecurity().includeSubDomains(true))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(
                                antMatcher("/v1.0/operator-users/registration/**"),
                                antMatcher("/v1.0/regulator-users/registration/**"),
                                antMatcher("/v1.0/verifier-users/registration/**"),
                                antMatcher("/v1.0/users/security-setup/2fa/delete*"),
                                antMatcher("/v1.0/users/forgot-password/**"),
                                antMatcher("/v1.0/file-attachments/**"),
                                antMatcher("/v1.0/file-document-templates/**"),
                                antMatcher("/v1.0/file-documents/**"),
                                antMatcher("/v1.0/file-notes/**"),
                                antMatcher("/v1.0/user-signatures/**"),
                                antMatcher("/v1.0/data*"),
                                antMatcher("/v3/api-docs/**"),
                                antMatcher("/error"),
                                antMatcher("/swagger-ui/**"),
                                antMatcher("/swagger-resources/**"),
                                antMatcher("/configuration/**"),
                                antMatcher("/ui-configuration/**"),
                                antMatcher("/webjars/**"),
                                antMatcher("/actuator/**"),
                                antMatcher("/camunda-api/**"))
                        .permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(antMatcher("/**"))
                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt()
                        .jwtAuthenticationConverter(new PmrvAuthenticationConverter(authorityService)));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(
                properties.getJwt().getJwkSetUri()).build();

        jwtDecoder.setClaimSetConverter(new RoleTypeClaimConverter(userRoleTypeService));
        return jwtDecoder;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
