package integration;

import java.math.BigDecimal;

/**
 * External DTO (Data Transfer Object) returned by the External HR API.
 */
public record HRRecord(
        String externalId,
        String firstName,
        String lastName,
        String workEmail,
        String departmentCode,
        String employmentType,
        BigDecimal baseSalary,
        BigDecimal bonusAmount,
        BigDecimal hourlyRate,
        BigDecimal hoursLogged,
        BigDecimal stipendAmount,
        String bankAccountNumber,
        boolean active
) {
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
