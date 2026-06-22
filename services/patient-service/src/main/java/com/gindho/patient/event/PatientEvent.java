package com.gindho.patient.event;

import com.gindho.kafka.BaseEvent;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class PatientEvent extends BaseEvent {
    public static final String PATIENT_CREATED = "PatientCreated";
    public static final String PATIENT_UPDATED = "PatientUpdated";
    public static final String PATIENT_DELETED = "PatientDeleted";
}
