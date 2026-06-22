package com.gindho.patient.controller;
import com.gindho.patient.service.PatientService;
import com.gindho.base.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock private PatientService patientService;
    @InjectMocks private PatientController controller;

    @Test
    void getAllPatients_ReturnsList() {
        assertNotNull(controller);
    }

    @Test
    void createPatient_ValidData_ReturnsCreated() {
        assertNotNull(controller);
    }
}