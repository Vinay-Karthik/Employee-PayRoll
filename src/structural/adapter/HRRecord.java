package structural.adapter;

import java.math.BigDecimal;

/**
 * External DTO (Data Transfer Object) returned by the External HR API.
 * Represents third-party employee structure, which has incompatible fields
 * and naming conventions compared to the internal Employee domain model.
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
