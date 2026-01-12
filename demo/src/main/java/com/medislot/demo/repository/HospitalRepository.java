package com.medislot.demo.repository;

import com.medislot.demo.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, UUID> {
    
    /**
     * Find all active hospitals
     */
    List<Hospital> findByActiveTrue();
}
