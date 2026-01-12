package com.medislot.demo.mapper;

import com.medislot.demo.dto.hospital.HospitalCreateRequest;
import com.medislot.demo.dto.hospital.HospitalResponse;
import com.medislot.demo.entity.Hospital;
import org.springframework.stereotype.Component;

@Component
public class HospitalMapper {

    public HospitalResponse toResponse(Hospital hospital) {
        if (hospital == null) {
            return null;
        }

        HospitalResponse response = new HospitalResponse();
        response.setId(hospital.getId());
        response.setName(hospital.getName());
        response.setAddress(hospital.getAddress());
        response.setActive(hospital.getActive());
        response.setCreatedAt(hospital.getCreatedAt());
        return response;
    }

    public Hospital toEntity(HospitalCreateRequest request) {
        if (request == null) {
            return null;
        }

        Hospital hospital = new Hospital();
        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setActive(request.getActive() != null ? request.getActive() : true);
        return hospital;
    }
}
