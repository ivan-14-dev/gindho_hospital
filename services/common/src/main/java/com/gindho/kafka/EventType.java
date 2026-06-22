package com.gindho.kafka;
public final class EventType {
    private EventType() {}
    public static final String PATIENT_CREATED = "PatientCreated";
    public static final String PATIENT_UPDATED = "PatientUpdated";
    public static final String APPOINTMENT_BOOKED = "AppointmentBooked";
    public static final String APPOINTMENT_CANCELLED = "AppointmentCancelled";
    public static final String PRESCRIPTION_CREATED = "PrescriptionCreated";
    public static final String LAB_RESULT_READY = "LabResultReady";
    public static final String INVOICE_PAID = "InvoicePaid";
    public static final String INVOICE_CREATED = "InvoiceCreated";
    public static final String ADMISSION_CREATED = "AdmissionCreated";
    public static final String DISCHARGE_COMPLETED = "DischargeCompleted";
    public static final String TRANSFER_INITIATED = "TransferInitiated";
    public static final String EMERGENCY_TRIAGED = "EmergencyTriaged";
    public static final String SURGERY_SCHEDULED = "SurgeryScheduled";
    public static final String SURGERY_COMPLETED = "SurgeryCompleted";
    public static final String MEDICATION_ADMINISTERED = "MedicationAdministered";
    public static final String INVENTORY_LOW = "InventoryLow";
    public static final String BED_ASSIGNED = "BedAssigned";
    public static final String BED_RELEASED = "BedReleased";
    public static final String ROUND_COMPLETED = "RoundCompleted";
    public static final String EVENT_CREATED = "EventCreated";
    public static final String NOTIFICATION_SENT = "NotificationSent";
    public static final String TELECONSULTATION_CREATED = "TeleconsultationCreated";
    public static final String TELECONSULTATION_STATUS_CHANGED = "TeleconsultationStatusChanged";
    public static final String QUALITY_INCIDENT_RESOLVED = "QualityIncidentResolved";
    public static final String INSURANCE_CREATED = "InsuranceCreated";
    public static final String INSURANCE_UPDATED = "InsuranceUpdated";
}