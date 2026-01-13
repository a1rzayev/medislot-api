package com.medislot.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS Configuration
 * Configures Cross-Origin Resource Sharing for frontend integration
 */
@Configuration
public class CorsConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);
    
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:4200,http://localhost:5173}")
    private String[] allowedOrigins;
    
    @Value("${cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
    private String[] allowedMethods;
    
    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;
    
    @Value("${cors.exposed-headers:X-Request-Id,X-Total-Count,X-Page-Number,X-Page-Size}")
    private String[] exposedHeaders;
    
    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;
    
    @Value("${cors.max-age:3600}")
    private long maxAge;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS...");
        
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        logger.info("CORS Allowed Origins: {}", Arrays.toString(allowedOrigins));
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        logger.info("CORS Allowed Methods: {}", Arrays.toString(allowedMethods));
        
        // Set allowed headers
        if (allowedHeaders.length == 1 && "*".equals(allowedHeaders[0])) {
            configuration.addAllowedHeader("*");
            logger.info("CORS Allowed Headers: * (all)");
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
            logger.info("CORS Allowed Headers: {}", Arrays.toString(allowedHeaders));
        }
        
        // Set exposed headers (headers that frontend can read from response)
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders));
        logger.info("CORS Exposed Headers: {}", Arrays.toString(exposedHeaders));
        
        // Allow credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(allowCredentials);
        logger.info("CORS Allow Credentials: {}", allowCredentials);
        
        // Max age for preflight requests (in seconds)
        configuration.setMaxAge(maxAge);
        logger.info("CORS Max Age: {} seconds", maxAge);
        
        // Apply configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        logger.info("CORS configuration completed successfully");
        return source;
    }
    
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}
