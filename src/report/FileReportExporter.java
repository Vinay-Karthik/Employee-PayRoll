package report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Concrete file-system implementation of ReportExporter.
 */
public class FileReportExporter implements ReportExporter {
    @Override
    public void export(String reportContent, String destination) throws IOException {
        Path path = Paths.get(destination);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(reportContent);
        }
    }
}
