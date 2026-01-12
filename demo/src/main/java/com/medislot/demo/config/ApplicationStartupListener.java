package com.medislot.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Application Startup Listener
 * Displays important URLs when the application starts
 */
@Component
public class ApplicationStartupListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${springdoc.swagger-ui.path:/api-documentation}")
    private String swaggerPath;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String baseUrl = "http://localhost:" + serverPort;
        
        logger.info("\n" +
                "========================================================================\n" +
                "   __  __          _ _ ____  _       _      _    ____ ___ \n" +
                "  |  \\/  | ___  __| (_) ___||| | ___ | |_   / \\  |  _ \\_ _|\n" +
                "  | |\\/| |/ _ \\/ _` | \\___ \\| |/ _ \\| __| / _ \\ | |_) | | \n" +
                "  | |  | |  __/ (_| | |___) | | (_) | |_ / ___ \\|  __/| | \n" +
                "  |_|  |_|\\___|\\__,_|_|____/|_|\\___/ \\__/_/   \\_\\_|  |___|\n" +
                "\n" +
                "========================================================================\n" +
                "  Application: MediSlot API\n" +
                "  Status: RUNNING ✓\n" +
                "  Port: " + serverPort + "\n" +
                "========================================================================\n" +
                "\n" +
                "  📚 API Documentation (Swagger UI):\n" +
                "     " + baseUrl + swaggerPath + "\n" +
                "\n" +
                "  📋 OpenAPI JSON Spec:\n" +
                "     " + baseUrl + "/api-docs\n" +
                "\n" +
                "  🔗 API Endpoints:\n" +
                "     Doctors:       " + baseUrl + "/api/doctors\n" +
                "     Patients:      " + baseUrl + "/api/patients\n" +
                "     Hospitals:     " + baseUrl + "/api/hospitals\n" +
                "     Availability:  " + baseUrl + "/api/availability\n" +
                "     Appointments:  " + baseUrl + "/api/appointments\n" +
                "\n" +
                "========================================================================\n"
        );
    }
}
