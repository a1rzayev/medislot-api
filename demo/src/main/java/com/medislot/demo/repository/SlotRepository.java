package com.medislot.demo.repository;

import com.medislot.demo.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SlotRepository extends JpaRepository<Slot, UUID> {
}
