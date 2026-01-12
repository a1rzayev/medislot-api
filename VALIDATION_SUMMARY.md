# PHASE 2 — Validation Summary

## DTO-Level Validation Annotations

### DoctorCreateRequest
- `@NotBlank` + `@Size(2-100)` on `fullName`
- `@NotBlank` + `@Size(2-50)` on `specialty`

### DoctorUpdateRequest
- No validation (intentional - supports partial updates)

### PatientCreateRequest
- `@NotBlank` + `@Size(2-100)` on `fullName`
- `@Pattern(E.164 format)` on `phone` (optional field)
- `@Email` + `@Size(max=100)` on `email` (optional field)

### HospitalCreateRequest
- `@NotBlank` + `@Size(2-100)` on `name`
- `@Size(max=255)` on `address` (optional field)

### AppointmentCreateRequest
- `@NotNull` on all UUID fields (`doctorId`, `hospitalId`, `slotId`, `patientId`)
- `@ValidAppointmentTime` (custom validator at class level)

### SlotCreateRequest
- `@NotNull` on all UUID fields (`doctorId`, `hospitalId`)
- `@NotNull` + `@Future` on `startTime`
- `@NotNull` on `endTime`
- `@ValidTimeRange` (custom validator at class level)

## Custom Validators

### 1. @ValidTimeRange
**Purpose:** Ensures `startTime` is before `endTime`  
**Applies to:** `SlotCreateRequest`  
**Implementation:**
- Class-level annotation
- Uses reflection to access fields
- Validates temporal ordering

### 2. @ValidAppointmentTime
**Purpose:** Validates appointment booking against slot availability  
**Applies to:** `AppointmentCreateRequest`  
**Validates:**
- Slot exists
- Slot status is `AVAILABLE`
- Slot belongs to specified doctor and hospital
**Implementation:**
- Uses `SlotRepository` for database validation
- Returns `false` if slot is invalid or already booked

### 3. @UniqueEmail
**Purpose:** Ensures email uniqueness in database  
**Applies to:** Can be added to `PatientCreateRequest.email`  
**Implementation:**
- Uses `PatientRepository.existsByEmail()`
- Optional - can be enabled when needed

## Validation Error Formatting

### GlobalExceptionHandler
Handles `MethodArgumentNotValidException` and formats errors as:

```json
{
  "message": "Validation failed",
  "errors": [
    "fullName: Full name must be between 2 and 100 characters",
    "email: Email must be valid"
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Features:**
- Returns 400 BAD_REQUEST status
- Field-level error messages
- List format for multiple errors
- Timestamp included

## Repository Layer

Created repositories to support custom validators:
- `PatientRepository` - with `existsByEmail(String email)` method
- `SlotRepository` - standard JPA repository

## Usage Examples

### Valid Request
```json
POST /api/patients
{
  "fullName": "John Doe",
  "phone": "+14155552671",
  "email": "john@example.com"
}
```

### Invalid Request (Multiple Errors)
```json
POST /api/patients
{
  "fullName": "J",
  "phone": "invalid",
  "email": "not-an-email"
}
```

**Response (400):**
```json
{
  "message": "Validation failed",
  "errors": [
    "fullName: Full name must be between 2 and 100 characters",
    "phone: Phone number must be valid",
    "email: Email must be valid"
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Invalid Slot Time
```json
POST /api/slots
{
  "doctorId": "...",
  "hospitalId": "...",
  "startTime": "2024-01-01T10:00:00Z",
  "endTime": "2024-01-01T09:00:00Z"
}
```

**Response (400):**
```json
{
  "message": "Validation failed",
  "errors": [
    "End time must be after start time"
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## Validation Rules Summary

| Field Type | Validation Rules |
|------------|------------------|
| Names | 2-100 chars, not blank |
| Specialty | 2-50 chars, not blank |
| Email | Valid format, max 100 chars |
| Phone | E.164 format (optional) |
| Address | Max 255 chars (optional) |
| UUID fields | Not null |
| Start time | Not null, future date |
| End time | Not null, after start time |
| Appointment | Valid slot, doctor, hospital match |

## Notes

- All validation is declarative (annotation-based)
- Custom validators integrate with Spring's validation framework
- Error messages are clear and actionable
- Validation happens before controller method execution
- Database validators (like @UniqueEmail) are optional to avoid performance overhead
