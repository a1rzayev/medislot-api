package com.medislot.demo.service;

import java.util.List;
import java.util.Optional;

/**
 * Base service interface providing common CRUD operations
 * @param <T> Entity type
 * @param <ID> ID type
 * @param <CreateDTO> DTO for creation
 * @param <UpdateDTO> DTO for updates
 * @param <ResponseDTO> Response DTO
 */
public interface BaseService<T, ID, CreateDTO, UpdateDTO, ResponseDTO> {
    
    /**
     * Create a new entity
     * @param createRequest the creation DTO
     * @return the created entity as response DTO
     */
    ResponseDTO create(CreateDTO createRequest);
    
    /**
     * Get entity by ID
     * @param id the entity ID
     * @return Optional containing the response DTO if found
     */
    Optional<ResponseDTO> findById(ID id);
    
    /**
     * Get all entities
     * @return list of response DTOs
     */
    List<ResponseDTO> findAll();
    
    /**
     * Update an entity
     * @param id the entity ID
     * @param updateRequest the update DTO
     * @return the updated entity as response DTO
     */
    ResponseDTO update(ID id, UpdateDTO updateRequest);
    
    /**
     * Delete an entity
     * @param id the entity ID
     */
    void delete(ID id);
    
    /**
     * Check if entity exists by ID
     * @param id the entity ID
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);
}
