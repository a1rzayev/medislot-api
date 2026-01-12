package com.medislot.demo.repository;

import com.medislot.demo.entity.Appointment;
import com.medislot.demo.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    /**
     * Find appointment by slot ID
     */
    Optional<Appointment> findBySlotId(UUID slotId);
    
    /**
     * Find all appointments for a patient
     */
    List<Appointment> findByPatientId(UUID patientId);
    
    /**
     * Find all appointments for a doctor
     */
    List<Appointment> findByDoctorId(UUID doctorId);
    
    /**
     * Find all appointments at a hospital
     */
    List<Appointment> findByHospitalId(UUID hospitalId);
    
    /**
     * Find appointments by doctor and status
     */
    List<Appointment> findByDoctorIdAndStatus(UUID doctorId, AppointmentStatus status);
    
    /**
     * Check if a time slot is already booked for a doctor
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Appointment a JOIN Slot s ON a.slotId = s.id " +
           "WHERE a.doctorId = :doctorId " +
           "AND a.status = 'BOOKED' " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime")
    boolean isTimeSlotBooked(@Param("doctorId") UUID doctorId,
                            @Param("startTime") OffsetDateTime startTime,
                            @Param("endTime") OffsetDateTime endTime);
    
    /**
     * Find appointments for a doctor on a specific date
     */
    @Query("SELECT a FROM Appointment a JOIN Slot s ON a.slotId = s.id " +
           "WHERE a.doctorId = :doctorId " +
           "AND DATE(s.startTime) = DATE(:date)")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") UUID doctorId,
                                            @Param("date") OffsetDateTime date);
    
    /**
     * Find appointments by multiple criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE (:doctorId IS NULL OR a.doctorId = :doctorId) " +
           "AND (:patientId IS NULL OR a.patientId = :patientId) " +
           "AND (:hospitalId IS NULL OR a.hospitalId = :hospitalId) " +
           "AND (:status IS NULL OR a.status = :status)")
    List<Appointment> findByCriteria(@Param("doctorId") UUID doctorId,
                                     @Param("patientId") UUID patientId,
                                     @Param("hospitalId") UUID hospitalId,
                                     @Param("status") AppointmentStatus status);
}
