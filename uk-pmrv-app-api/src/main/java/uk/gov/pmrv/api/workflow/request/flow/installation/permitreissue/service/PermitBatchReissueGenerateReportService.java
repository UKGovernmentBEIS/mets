package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountReport;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class PermitBatchReissueGenerateReportService {

	private static final DateTimeFormatter CSV_DATE_ISSUE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	
	private final RequestService requestService;
	private final FileDocumentService fileDocumentService;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void generateReport(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final BatchReissueRequestPayload payload = (BatchReissueRequestPayload) request.getPayload();
		final PermitBatchReissueRequestMetadata metadata = (PermitBatchReissueRequestMetadata) request.getMetadata();
		final Map<Long, PermitReissueAccountReport> accountsReports = metadata.getAccountsReports();
		
		try (StringWriter sw = new StringWriter();
				CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
						.setHeader("Permit ID", 
								"Emitter name", 
								"Installation name", 
								"Date issued", 
								"Status")
						.build());) {
			for (PermitReissueAccountReport accountReport : accountsReports.values()) {
				csvPrinter.printRecord(
						accountReport.getPermitId(), 
						accountReport.getOperatorName(),
						accountReport.getInstallationName(),
						accountReport.getIssueDate() != null
								? CSV_DATE_ISSUE_FORMATTER.format(accountReport.getIssueDate())
								: "N/A",
						accountReport.isSucceeded() ? "Pass" : "Fail");
			}
			
			final byte[] generatedFile = sw.toString().getBytes(StandardCharsets.UTF_8);
			final FileInfoDTO reportFile = fileDocumentService.createFileDocument(generatedFile, request.getId() + ".csv");
			
			//update payload
			payload.setReport(reportFile);
		} catch (Exception e) {
			log.error(String.format("Cannot generate csv report for request %s", requestId), e);
		}
	}
	
}
