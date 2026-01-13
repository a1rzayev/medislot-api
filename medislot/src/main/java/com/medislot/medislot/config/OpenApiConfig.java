package com.medislot.medislot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration
 * Configures API documentation accessible at /api-documentation
 */
@Configuration
public class OpenApiConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiConfig.class);

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI medislotOpenAPI() {
        try {
            logger.info("Initializing OpenAPI configuration...");
            
            Server localServer = new Server();
            localServer.setUrl("http://localhost:" + serverPort);
            localServer.setDescription("Local Development Server");

            Contact contact = new Contact();
            contact.setName("MediSlot API Team");
            contact.setEmail("support@medislot.com");

            License license = new License();
            license.setName("MIT License");
            license.setUrl("https://opensource.org/licenses/MIT");

            Info info = new Info();
            info.setTitle("MediSlot API");
            info.setVersion("1.0.0");
            info.setDescription("REST API for MediSlot - Medical Appointment Booking System");
            info.setContact(contact);
            info.setLicense(license);
            info.setTermsOfService("https://medislot.com/terms");

            OpenAPI openAPI = new OpenAPI();
            openAPI.setInfo(info);
            openAPI.setServers(List.of(localServer));
            
            logger.info("OpenAPI configuration initialized successfully");
            return openAPI;
        } catch (Exception e) {
            logger.error("Error initializing OpenAPI configuration", e);
            // Return minimal configuration as fallback
            Info info = new Info();
            info.setTitle("MediSlot API");
            info.setVersion("1.0.0");
            return new OpenAPI().info(info);
        }
    }
}
