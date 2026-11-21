package service;

import model.PaySlip;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable result of a monthly payroll batch processing operation.
 *
 * <p>Explicitly communicates the outcome of the batch, including total employees,
 * successful payslips, failed employees, processing status, and the workflow
 * stage at which any failure occurred. This enables the presentation layer to
 * clearly display partial-success scenarios and distinguish between different
 * failure types.
 */
public final class PayrollBatchResult {

    public enum Status {
        SUCCESS,
        PARTIAL_SUCCESS,
        FAILURE
    }

    /**
     * Workflow stage at which a failure occurred (if any).
     * Used to distinguish between different types of failures while preserving
     * successfully generated payslips.
     */
    public enum FailedStage {
        EMPLOYEE_RETRIEVAL,
        PAYSLIP_GENERATION,
        REPORT_GENERATION,
        REPORT_EXPORT,
        NONE
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
    private final FailedStage failedStage;
    private final boolean reportExported;

    private PayrollBatchResult(Builder builder) {
        this.status = Objects.requireNonNull(builder.status, "Status must not be null");
        this.totalEmployees = builder.totalEmployees;
        this.successfulCount = builder.successfulCount;
        this.failedCount = builder.failedCount;
        // Defensive copies to ensure immutability
        this.successfulPaySlips = List.copyOf(builder.successfulPaySlips);
        this.failedEmployeeIds = List.copyOf(builder.failedEmployeeIds);
        this.summaryReport = builder.summaryReport;
        this.reportDestination = builder.reportDestination;
        this.errorMessage = builder.errorMessage;
        this.failedStage = Objects.requireNonNullElse(builder.failedStage, FailedStage.NONE);
        this.reportExported = builder.reportExported;
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

    public FailedStage getFailedStage() {
        return failedStage;
    }

    public boolean isReportExported() {
        return reportExported;
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
                "PayrollBatchResult{status=%s, stage=%s, total=%d, successful=%d, failed=%d, exported=%s}",
                status, failedStage, totalEmployees, successfulCount, failedCount, reportExported
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
        private FailedStage failedStage;
        private boolean reportExported = false;

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder totalEmployees(int totalEmployees) {
            if (totalEmployees < 0) {
                throw new IllegalArgumentException("Total employees cannot be negative");
            }
            this.totalEmployees = totalEmployees;
            return this;
        }

        public Builder successfulCount(int successfulCount) {
            if (successfulCount < 0) {
                throw new IllegalArgumentException("Successful count cannot be negative");
            }
            this.successfulCount = successfulCount;
            return this;
        }

        public Builder failedCount(int failedCount) {
            if (failedCount < 0) {
                throw new IllegalArgumentException("Failed count cannot be negative");
            }
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

        public Builder failedStage(FailedStage failedStage) {
            this.failedStage = failedStage;
            return this;
        }

        public Builder reportExported(boolean reportExported) {
            this.reportExported = reportExported;
            return this;
        }

        public PayrollBatchResult build() {
            // Validate negative counts
            if (totalEmployees < 0 || successfulCount < 0 || failedCount < 0) {
                throw new IllegalStateException("Counts cannot be negative");
            }

            // Validate consistency: total = successful + failed
            if (totalEmployees != successfulCount + failedCount) {
                throw new IllegalStateException(
                        "Inconsistent counts: total=" + totalEmployees +
                        ", successful=" + successfulCount +
                        ", failed=" + failedCount
                );
            }

            // Validate successful count matches payslip list size
            if (successfulCount != successfulPaySlips.size()) {
                throw new IllegalStateException(
                        "Inconsistent successful count: count=" + successfulCount +
                        ", list size=" + successfulPaySlips.size()
                );
            }

            // Validate failed count matches failed ID list size
            if (failedCount != failedEmployeeIds.size()) {
                throw new IllegalStateException(
                        "Inconsistent failed count: count=" + failedCount +
                        ", list size=" + failedEmployeeIds.size()
                );
            }

            // Validate status consistency with counts if explicitly set
            if (status != null) {
                if (status == Status.SUCCESS && (failedCount > 0 || successfulCount == 0)) {
                    throw new IllegalStateException(
                            "Status SUCCESS requires failedCount=0 and successfulCount>0"
                    );
                }
                if (status == Status.PARTIAL_SUCCESS && (successfulCount == 0 || failedCount == 0)) {
                    throw new IllegalStateException(
                            "Status PARTIAL_SUCCESS requires both successfulCount>0 and failedCount>0"
                    );
                }
                // FAILURE can have successful payslips (e.g., report generation/export failed)
                // No validation restriction on FAILURE status
            } else {
                // Determine status from counts
                if (failedCount == 0 && successfulCount > 0) {
                    status = Status.SUCCESS;
                } else if (successfulCount > 0) {
                    status = Status.PARTIAL_SUCCESS;
                } else {
                    status = Status.FAILURE;
                }
            }

            // Default failedStage to NONE if not set and status is not FAILURE
            if (failedStage == null) {
                failedStage = (status == Status.FAILURE) ? FailedStage.PAYSLIP_GENERATION : FailedStage.NONE;
            }

            return new PayrollBatchResult(this);
        }
    }
}