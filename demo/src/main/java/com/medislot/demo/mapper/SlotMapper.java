package com.medislot.demo.mapper;

import com.medislot.demo.dto.slot.SlotCreateRequest;
import com.medislot.demo.dto.slot.SlotResponse;
import com.medislot.demo.entity.Slot;
import org.springframework.stereotype.Component;

@Component
public class SlotMapper {

    public SlotResponse toResponse(Slot slot) {
        if (slot == null) {
            return null;
        }

        SlotResponse response = new SlotResponse();
        response.setId(slot.getId());
        response.setDoctorId(slot.getDoctorId());
        response.setHospitalId(slot.getHospitalId());
        response.setStartTime(slot.getStartTime());
        response.setEndTime(slot.getEndTime());
        response.setStatus(slot.getStatus());
        response.setCreatedAt(slot.getCreatedAt());
        return response;
    }

    public Slot toEntity(SlotCreateRequest request) {
        if (request == null) {
            return null;
        }

        Slot slot = new Slot();
        slot.setDoctorId(request.getDoctorId());
        slot.setHospitalId(request.getHospitalId());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(request.getStatus() != null ? request.getStatus() : com.medislot.demo.entity.SlotStatus.AVAILABLE);
        return slot;
    }
}
