package report;

import java.io.IOException;

/**
 * Interface Segregation & Dependency Inversion:
 * Abstracts report output destinations (file, console, email, cloud storage).
 */
public interface ReportExporter {
    void export(String reportContent, String destination) throws IOException;
}
