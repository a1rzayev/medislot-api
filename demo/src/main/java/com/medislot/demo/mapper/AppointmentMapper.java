package com.medislot.demo.mapper;

import com.medislot.demo.dto.appointment.AppointmentCreateRequest;
import com.medislot.demo.dto.appointment.AppointmentResponse;
import com.medislot.demo.entity.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setDoctorId(appointment.getDoctorId());
        response.setHospitalId(appointment.getHospitalId());
        response.setSlotId(appointment.getSlotId());
        response.setPatientId(appointment.getPatientId());
        response.setStatus(appointment.getStatus());
        response.setCreatedAt(appointment.getCreatedAt());
        return response;
    }

    public Appointment toEntity(AppointmentCreateRequest request) {
        if (request == null) {
            return null;
        }

        Appointment appointment = new Appointment();
        appointment.setDoctorId(request.getDoctorId());
        appointment.setHospitalId(request.getHospitalId());
        appointment.setSlotId(request.getSlotId());
        appointment.setPatientId(request.getPatientId());
        appointment.setStatus(request.getStatus() != null ? request.getStatus() : com.medislot.demo.entity.AppointmentStatus.BOOKED);
        return appointment;
    }
}
