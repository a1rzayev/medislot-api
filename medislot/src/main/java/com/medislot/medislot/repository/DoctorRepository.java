package com.medislot.medislot.repository;

import com.medislot.medislot.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    
    /**
     * Find all active doctors
     */
    List<Doctor> findByActiveTrue();
    
    /**
     * Find doctors by specialty
     */
    List<Doctor> findBySpecialty(String specialty);
    
    /**
     * Find active doctors by specialty
     */
    List<Doctor> findByActiveTrueAndSpecialty(String specialty);
    
    /**
     * Find doctors by specialty (case-insensitive)
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
    
    /**
     * Find active doctors by specialty (case-insensitive)
     */
    List<Doctor> findByActiveTrueAndSpecialtyIgnoreCase(String specialty);
    
    /**
     * Check if doctor has any slots
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Slot s WHERE s.doctorId = :doctorId")
    boolean hasSlots(@Param("doctorId") UUID doctorId);
    
    /**
     * Check if doctor has any appointments
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE a.doctorId = :doctorId")
    boolean hasAppointments(@Param("doctorId") UUID doctorId);
}
