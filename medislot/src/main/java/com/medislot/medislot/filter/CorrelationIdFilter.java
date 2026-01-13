package com.medislot.medislot.filter;

import com.medislot.medislot.util.CorrelationIdHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Correlation ID Filter
 * Generates or accepts X-Request-Id header for request tracing
 * This filter runs first (Order 1) to ensure correlation ID is available for all subsequent filters
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Request-Id";
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
                        FilterChain filterChain) throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        try {
            // Get correlation ID from request header, or generate a new one
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            
            if (correlationId == null || correlationId.trim().isEmpty()) {
                correlationId = generateCorrelationId();
                logger.debug("Generated new correlation ID: {}", correlationId);
            } else {
                logger.debug("Using existing correlation ID: {}", correlationId);
            }
            
            // Store in ThreadLocal for access throughout the request
            CorrelationIdHolder.setCorrelationId(correlationId);
            
            // Add correlation ID to response header
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
            
        } finally {
            // Always clear ThreadLocal to prevent memory leaks
            CorrelationIdHolder.clear();
        }
    }
    
    /**
     * Generate a unique correlation ID
     */
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("CorrelationIdFilter initialized");
    }
    
    @Override
    public void destroy() {
        logger.info("CorrelationIdFilter destroyed");
    }
}
