package com.medislot.demo.filter;

import com.medislot.demo.util.CorrelationIdHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Request Logging Filter
 * Logs HTTP requests and responses with timing information
 * This filter runs after CorrelationIdFilter (Order 2)
 */
@Component
@Order(2)
public class RequestLoggingFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                        FilterChain filterChain) throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // Record start time
        long startTime = System.currentTimeMillis();
        
        // Get correlation ID from ThreadLocal
        String correlationId = CorrelationIdHolder.getCorrelationId();
        
        // Log incoming request
        logRequest(request, correlationId);
        
        try {
            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Calculate time taken
            long timeTaken = System.currentTimeMillis() - startTime;
            
            // Log response
            logResponse(request, response, timeTaken, correlationId);
        }
    }
    
    /**
     * Log incoming request details
     */
    private void logRequest(HttpServletRequest request, String correlationId) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString != null ? path + "?" + queryString : path;
        String clientIp = getClientIpAddress(request);
        
        logger.info("→ Incoming Request [{}] {} {} from {}", 
                correlationId, method, fullPath, clientIp);
        
        // Log headers in debug mode
        if (logger.isDebugEnabled()) {
            logHeaders(request, correlationId);
        }
    }
    
    /**
     * Log response details
     */
    private void logResponse(HttpServletRequest request, HttpServletResponse response, 
                            long timeTaken, String correlationId) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        int status = response.getStatus();
        
        String logLevel = determineLogLevel(status);
        
        String message = String.format("← Response [%s] %s %s - Status: %d - Time: %dms",
                correlationId, method, path, status, timeTaken);
        
        // Log based on status code
        switch (logLevel) {
            case "ERROR":
                logger.error(message);
                break;
            case "WARN":
                logger.warn(message);
                break;
            default:
                logger.info(message);
        }
    }
    
    /**
     * Log request headers (debug mode)
     */
    private void logHeaders(HttpServletRequest request, String correlationId) {
        StringBuilder headers = new StringBuilder();
        headers.append("[").append(correlationId).append("] Request Headers: ");
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // Mask sensitive headers
            if (isSensitiveHeader(headerName)) {
                headerValue = "***MASKED***";
            }
            
            headers.append(headerName).append("=").append(headerValue);
            if (headerNames.hasMoreElements()) {
                headers.append(", ");
            }
        }
        
        logger.debug(headers.toString());
    }
    
    /**
     * Determine log level based on HTTP status code
     */
    private String determineLogLevel(int status) {
        if (status >= 500) {
            return "ERROR";
        } else if (status >= 400) {
            return "WARN";
        } else {
            return "INFO";
        }
    }
    
    /**
     * Check if header contains sensitive information
     */
    private boolean isSensitiveHeader(String headerName) {
        String lowerCaseHeader = headerName.toLowerCase();
        return lowerCaseHeader.contains("authorization") ||
               lowerCaseHeader.contains("password") ||
               lowerCaseHeader.contains("secret") ||
               lowerCaseHeader.contains("token") ||
               lowerCaseHeader.contains("api-key");
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Handle multiple IPs (take the first one)
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("RequestLoggingFilter initialized");
    }
    
    @Override
    public void destroy() {
        logger.info("RequestLoggingFilter destroyed");
    }
}
