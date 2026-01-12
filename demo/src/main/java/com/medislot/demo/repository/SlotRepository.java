package com.medislot.demo.repository;

import com.medislot.demo.entity.Slot;
import com.medislot.demo.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SlotRepository extends JpaRepository<Slot, UUID> {
    
    /**
     * Find slots by doctor ID
     */
    List<Slot> findByDoctorId(UUID doctorId);
    
    /**
     * Find slots by hospital ID
     */
    List<Slot> findByHospitalId(UUID hospitalId);
    
    /**
     * Find slots by doctor and hospital
     */
    List<Slot> findByDoctorIdAndHospitalId(UUID doctorId, UUID hospitalId);
    
    /**
     * Find available slots by doctor
     */
    List<Slot> findByDoctorIdAndStatus(UUID doctorId, SlotStatus status);
    
    /**
     * Find available slots by hospital
     */
    List<Slot> findByHospitalIdAndStatus(UUID hospitalId, SlotStatus status);
    
    /**
     * Find available slots by doctor and hospital
     */
    List<Slot> findByDoctorIdAndHospitalIdAndStatus(UUID doctorId, UUID hospitalId, SlotStatus status);
    
    /**
     * Find slots by doctor in a time range
     */
    @Query("SELECT s FROM Slot s WHERE s.doctorId = :doctorId " +
           "AND s.startTime >= :startTime AND s.endTime <= :endTime")
    List<Slot> findByDoctorIdAndTimeRange(@Param("doctorId") UUID doctorId,
                                          @Param("startTime") OffsetDateTime startTime,
                                          @Param("endTime") OffsetDateTime endTime);
    
    /**
     * Check if slot overlaps with existing slots for a doctor
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Slot s " +
           "WHERE s.doctorId = :doctorId " +
           "AND s.id != :excludeId " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime")
    boolean hasOverlappingSlot(@Param("doctorId") UUID doctorId,
                               @Param("excludeId") UUID excludeId,
                               @Param("startTime") OffsetDateTime startTime,
                               @Param("endTime") OffsetDateTime endTime);
    
    /**
     * Find slot by doctor, start time and end time
     */
    Optional<Slot> findByDoctorIdAndStartTimeAndEndTime(UUID doctorId, 
                                                         OffsetDateTime startTime, 
                                                         OffsetDateTime endTime);
}
