package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountReport;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmpBatchReissueGenerateReportService {

	private static final DateTimeFormatter CSV_DATE_ISSUE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	
	private final RequestService requestService;
	private final FileDocumentService fileDocumentService;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void generateReport(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final BatchReissueRequestPayload payload = (BatchReissueRequestPayload) request.getPayload();
		final EmpBatchReissueRequestMetadata metadata = (EmpBatchReissueRequestMetadata) request.getMetadata();
		final Map<Long, EmpReissueAccountReport> accountsReports = metadata.getAccountsReports();
		
		try (StringWriter sw = new StringWriter();
				CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
						.setHeader("Plan ID", 
								"Operator name", 
								"Date issued", 
								"Status")
						.build());) {
			for (EmpReissueAccountReport accountReport : accountsReports.values()) {
				csvPrinter.printRecord(
						accountReport.getEmpId(), 
						accountReport.getAccountName(),
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
