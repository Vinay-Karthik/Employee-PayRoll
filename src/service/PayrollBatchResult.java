package service;

import model.PaySlip;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable result of a monthly payroll batch processing operation.
 *
 * <p>Explicitly communicates the outcome of the batch, including total employees,
 * successful payslips, failed employees, and processing status. This enables
 * the presentation layer to clearly display partial-success scenarios.
 */
public final class PayrollBatchResult {

    public enum Status {
        SUCCESS,
        PARTIAL_SUCCESS,
        FAILURE
    }

    private final Status status;
    private final int totalEmployees;
    private final int successfulCount;
    private final int failedCount;
    private final List<PaySlip> successfulPaySlips;
    private final List<Integer> failedEmployeeIds;
    private final String summaryReport;
    private final String reportDestination;
    private final String errorMessage;

    private PayrollBatchResult(Builder builder) {
        this.status = Objects.requireNonNull(builder.status, "Status must not be null");
        this.totalEmployees = builder.totalEmployees;
        this.successfulCount = builder.successfulCount;
        this.failedCount = builder.failedCount;
        this.successfulPaySlips = Collections.unmodifiableList(builder.successfulPaySlips);
        this.failedEmployeeIds = Collections.unmodifiableList(builder.failedEmployeeIds);
        this.summaryReport = builder.summaryReport;
        this.reportDestination = builder.reportDestination;
        this.errorMessage = builder.errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getSuccessfulCount() {
        return successfulCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public List<PaySlip> getSuccessfulPaySlips() {
        return successfulPaySlips;
    }

    public List<Integer> getFailedEmployeeIds() {
        return failedEmployeeIds;
    }

    public String getSummaryReport() {
        return summaryReport;
    }

    public String getReportDestination() {
        return reportDestination;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isPartialSuccess() {
        return status == Status.PARTIAL_SUCCESS;
    }

    public boolean isFailure() {
        return status == Status.FAILURE;
    }

    @Override
    public String toString() {
        return String.format(
                "PayrollBatchResult{status=%s, total=%d, successful=%d, failed=%d, reportExported=%s}",
                status, totalEmployees, successfulCount, failedCount,
                reportDestination != null && !reportDestination.isBlank()
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Status status;
        private int totalEmployees;
        private int successfulCount;
        private int failedCount;
        private List<PaySlip> successfulPaySlips = Collections.emptyList();
        private List<Integer> failedEmployeeIds = Collections.emptyList();
        private String summaryReport;
        private String reportDestination;
        private String errorMessage;

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder totalEmployees(int totalEmployees) {
            this.totalEmployees = totalEmployees;
            return this;
        }

        public Builder successfulCount(int successfulCount) {
            this.successfulCount = successfulCount;
            return this;
        }

        public Builder failedCount(int failedCount) {
            this.failedCount = failedCount;
            return this;
        }

        public Builder successfulPaySlips(List<PaySlip> successfulPaySlips) {
            this.successfulPaySlips = Objects.requireNonNullElse(successfulPaySlips, Collections.emptyList());
            return this;
        }

        public Builder failedEmployeeIds(List<Integer> failedEmployeeIds) {
            this.failedEmployeeIds = Objects.requireNonNullElse(failedEmployeeIds, Collections.emptyList());
            return this;
        }

        public Builder summaryReport(String summaryReport) {
            this.summaryReport = summaryReport;
            return this;
        }

        public Builder reportDestination(String reportDestination) {
            this.reportDestination = reportDestination;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public PayrollBatchResult build() {
            // Validate consistency
            if (totalEmployees != successfulCount + failedCount) {
                throw new IllegalStateException(
                        "Inconsistent counts: total=" + totalEmployees +
                        ", successful=" + successfulCount +
                        ", failed=" + failedCount
                );
            }

            // Determine status if not explicitly set
            if (status == null) {
                if (failedCount == 0 && successfulCount > 0) {
                    status = Status.SUCCESS;
                } else if (successfulCount > 0) {
                    status = Status.PARTIAL_SUCCESS;
                } else {
                    status = Status.FAILURE;
                }
            }

            return new PayrollBatchResult(this);
        }
    }
}