package structural.adapter;

import model.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter (GoF Adapter Pattern).
 *
 * Implements the {@link EmployeeDirectory} Target interface by wrapping
 * the third-party {@link ExternalHRService} Adaptee.
 *
 * Responsibilities:
 * 1. Translates internal numeric employee IDs to external format (e.g. 101 -> "HR-101").
 * 2. Catches external API exceptions (e.g. {@link ExternalEmployeeNotFoundException})
 *    and converts them to domain-friendly {@link Optional#empty()}.
 * 3. Converts external {@link HRRecord} DTOs into domain {@link Employee} models
 *    (FullTimeEmployee, PartTimeEmployee, ContractorEmployee, InternEmployee).
 * 4. Isolates third-party HR details completely away from the Payroll Service.
 */
public class ExternalHRAdapter implements EmployeeDirectory {
    private final ExternalHRService hrService;

    public ExternalHRAdapter(ExternalHRService hrService) {
        this.hrService = Objects.requireNonNull(hrService, "ExternalHRService must not be null");
    }

    @Override
    public Optional<Employee> findEmployeeById(int id) {
        if (id <= 0) {
            return Optional.empty();
        }

        // Translate internal numeric ID to external system's format
        String externalId = "HR-" + id;

        try {
            HRRecord record = hrService.getEmployeeById(externalId);
            if (record == null || !record.active()) {
                return Optional.empty();
            }
            return Optional.ofNullable(mapToEmployee(record));
        } catch (ExternalEmployeeNotFoundException e) {
            // Gracefully handle employee-not-found without leaking external exceptions
            return Optional.empty();
        } catch (Exception e) {
            // Guard against unhandled external system anomalies
            return Optional.empty();
        }
    }

    @Override
    public List<Employee> findAllEmployees() {
        return hrService.getAllEmployees().stream()
                .filter(HRRecord::active)
                .map(this::mapToEmployee)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Converts external HRRecord DTO into internal domain Employee model.
     */
    private Employee mapToEmployee(HRRecord record) {
        if (record == null) {
            return null;
        }

        int internalId = parseInternalId(record.externalId());
        String name = record.getFullName();
        String email = record.workEmail();
        String dept = record.departmentCode();
        String bankAccount = record.bankAccountNumber() != null ? record.bankAccountNumber() : "N/A";

        String type = record.employmentType() != null ? record.employmentType().toUpperCase().trim() : "";

        return switch (type) {
            case "FULL_TIME", "SALARIED", "PERMANENT" -> new FullTimeEmployee(
                    internalId, name, email, dept, bankAccount,
                    record.baseSalary(), record.bonusAmount()
            );
            case "PART_TIME", "HOURLY" -> new PartTimeEmployee(
                    internalId, name, email, dept, bankAccount,
                    record.hourlyRate(), record.hoursLogged()
            );
            case "CONTRACTOR", "CONTRACT" -> new ContractorEmployee(
                    internalId, name, email, dept, bankAccount,
                    record.hourlyRate(), record.hoursLogged()
            );
            case "INTERN" -> new InternEmployee(
                    internalId, name, email, dept, bankAccount,
                    record.stipendAmount()
            );
            default -> throw new IllegalArgumentException("Unknown external employment type: " + record.employmentType());
        };
    }

    private int parseInternalId(String externalId) {
        if (externalId == null) {
            return 0;
        }
        // Extract numeric digits from "HR-101" -> 101
        String digits = externalId.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
