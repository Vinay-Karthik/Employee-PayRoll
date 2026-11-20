package integration;

import java.math.BigDecimal;
import java.util.*;

/**
 * Simulates third-party External HR API (Adaptee in Adapter Pattern).
 */
public class ExternalHRService {
    private final Map<String, HRRecord> externalDatabase = new LinkedHashMap<>();
    private boolean simulateFailure = false;

    public ExternalHRService() {
        seedExternalEmployees();
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }

    private void seedExternalEmployees() {
        // Full-time employee
        externalDatabase.put("HR-101", new HRRecord(
                "HR-101", "Alice", "Johnson", "alice.johnson@external-hr.com",
                "Engineering", "FULL_TIME",
                bd("6500.00"), bd("800.00"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                "US-112233", true
        ));

        // Part-time employee
        externalDatabase.put("HR-102", new HRRecord(
                "HR-102", "Bob", "Smith", "bob.smith@external-hr.com",
                "Operations", "PART_TIME",
                BigDecimal.ZERO, BigDecimal.ZERO, bd("35.00"), bd("48.00"), BigDecimal.ZERO,
                "US-445566", true
        ));

        // Contractor
        externalDatabase.put("HR-103", new HRRecord(
                "HR-103", "Charlie", "Davis", "charlie.davis@external-hr.com",
                "IT Consulting", "CONTRACTOR",
                BigDecimal.ZERO, BigDecimal.ZERO, bd("75.00"), bd("60.00"), BigDecimal.ZERO,
                "US-778899", true
        ));

        // Intern
        externalDatabase.put("HR-104", new HRRecord(
                "HR-104", "Diana", "Prince", "diana.prince@external-hr.com",
                "Marketing", "INTERN",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, bd("2500.00"),
                "US-990011", true
        ));

        // Another full-time executive
        externalDatabase.put("HR-105", new HRRecord(
                "HR-105", "Bruce", "Wayne", "bruce.wayne@external-hr.com",
                "Executive", "FULL_TIME",
                bd("12000.00"), bd("3000.00"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                "US-123456", true
        ));

        // Inactive / Terminated employee in external HR
        externalDatabase.put("HR-999", new HRRecord(
                "HR-999", "Inactive", "User", "inactive@external-hr.com",
                "Operations", "FULL_TIME",
                bd("4000.00"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                "US-000000", false
        ));
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    public HRRecord getEmployeeById(String externalId) throws ExternalEmployeeNotFoundException, ExternalServiceException {
        if (simulateFailure) {
            throw new ExternalServiceException("External HR API connection timed out while querying ID: " + externalId);
        }
        if (externalId == null || !externalDatabase.containsKey(externalId)) {
            throw new ExternalEmployeeNotFoundException("No employee found in external HR with ID: " + externalId);
        }
        HRRecord record = externalDatabase.get(externalId);
        if (!record.active()) {
            throw new ExternalEmployeeNotFoundException("Employee " + externalId + " is inactive in external HR");
        }
        return record;
    }

    public List<HRRecord> getAllEmployees() throws ExternalServiceException {
        if (simulateFailure) {
            throw new ExternalServiceException("External HR API connection timed out while fetching employee list");
        }
        return new ArrayList<>(externalDatabase.values());
    }

    public void saveExternalRecord(HRRecord record) {
        Objects.requireNonNull(record, "HR record cannot be null");
        Objects.requireNonNull(record.externalId(), "External ID cannot be null");
        if (!record.externalId().matches("^HR-[1-9]\\d*$")) {
            throw new IllegalArgumentException("Invalid external ID format for HR record: " + record.externalId());
        }
        externalDatabase.put(record.externalId(), record);
    }

    public boolean deleteExternalRecord(String externalId) {
        Objects.requireNonNull(externalId, "External ID cannot be null");
        return externalDatabase.remove(externalId) != null;
    }
}
