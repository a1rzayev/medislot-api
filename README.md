# MediSlot API

A comprehensive RESTful API for managing medical appointment bookings, built with Spring Boot. MediSlot enables patients to book appointments with doctors at hospitals, while providing doctors and administrators with tools to manage availability, schedules, and appointments.

## Overview

MediSlot API is a backend system that facilitates the entire appointment booking lifecycle - from doctor availability management to appointment scheduling, approval, and cancellation. The system enforces business rules to prevent double-booking, ensures proper authorization, and maintains data integrity across all operations.

## Key Features

### 🔐 Authentication & Authorization
- **JWT-based authentication** for secure API access
- **Role-based access control (RBAC)** with three roles:
  - **ADMIN**: Full system access, can manage hospitals and doctors
  - **DOCTOR**: Can manage their own availability slots and appointments
  - **PATIENT**: Can create and manage their own appointments
- User registration and login endpoints

### 👨‍⚕️ Doctor Management
- Create and manage doctor profiles with specialties
- Associate doctors with multiple hospitals
- Track doctor availability and active status
- View doctor's appointment history

### 🏥 Hospital Management
- Manage hospital information (name, address)
- Associate multiple doctors with hospitals
- Track hospital active status
- View appointments by hospital

### 📅 Availability & Slot Management
- Doctors can create time slots for their availability
- Slots are associated with specific hospitals
- Automatic slot status management (AVAILABLE, BOOKED)
- Prevent double-booking through business rule enforcement
- Query available slots by doctor, hospital, and date range

### 📋 Appointment Management
- **Patient-initiated booking**: Patients can request appointments for available slots
- **Appointment status tracking**: PENDING, CONFIRMED, CANCELLED, COMPLETED
- **Cancellation rules**:
  - Patients can cancel their appointments
  - Doctors can cancel appointments (with business rule restrictions)
- **Rescheduling support**: Update appointment details
- **Filtering and search**: Find appointments by patient, doctor, hospital, status, or date

### 🛡️ Business Rules & Validation
- Prevents double-booking of the same slot
- Prevents double-booking of doctors at the same time
- Validates appointment times are within slot boundaries
- Ensures slot belongs to specified doctor and hospital
- Email uniqueness validation
- Time range validation for slots

### 📚 API Documentation
- **OpenAPI/Swagger UI** integration for interactive API documentation
- Comprehensive endpoint documentation with request/response examples
- Available at `/api-documentation` when the application is running

### 🔧 Technical Features
- **RESTful API design** following best practices
- **PostgreSQL database** for data persistence
- **JPA/Hibernate** for ORM
- **Request/Response DTOs** for clean API contracts
- **Global exception handling** with standardized error responses
- **CORS configuration** for frontend integration
- **Request correlation IDs** for distributed tracing
- **Request logging** for debugging and monitoring
- **Input validation** using Bean Validation

## Architecture

### Core Entities
- **User**: Authentication and authorization (email, password, role)
- **Doctor**: Medical professional information (name, specialty)
- **Hospital**: Healthcare facility information (name, address)
- **DoctorHospital**: Many-to-many relationship between doctors and hospitals
- **Slot**: Time availability slots for doctors at hospitals
- **Appointment**: Booked appointments linking patients, doctors, hospitals, and slots
- **Patient**: Patient profile information

### API Structure
```
/api/auth/*          - Authentication endpoints
/api/doctors/*       - Doctor management
/api/hospitals/*     - Hospital management
/api/patients/*      - Patient management
/api/availability/*  - Slot and availability management
/api/appointments/*  - Appointment booking and management
```

### Security
- JWT tokens for stateless authentication
- Spring Security for endpoint protection
- Method-level security with `@PreAuthorize` annotations
- Password encryption (handled by Spring Security)

## Technology Stack

- **Java 25** - Programming language
- **Spring Boot 4.0.1** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Relational database
- **JWT (jjwt)** - Token-based authentication
- **SpringDoc OpenAPI** - API documentation
- **Maven** - Build and dependency management
- **Hibernate** - ORM framework

## Database Schema

The system uses a relational database with the following key relationships:
- Users can be Doctors or Patients (role-based)
- Doctors can be associated with multiple Hospitals (many-to-many)
- Doctors create Slots at specific Hospitals
- Patients book Appointments for specific Slots
- Appointments link Patients, Doctors, Hospitals, and Slots

## API Response Format

All API responses follow a standardized format:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

Error responses include detailed error information:
```json
{
  "success": false,
  "message": "Error description",
  "errors": [ ... ],
  "timestamp": "2024-01-01T00:00:00Z"
}
```
