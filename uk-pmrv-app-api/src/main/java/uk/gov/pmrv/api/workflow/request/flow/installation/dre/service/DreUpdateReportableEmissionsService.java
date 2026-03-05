package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsSaveParams;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@Service
@RequiredArgsConstructor
public class DreUpdateReportableEmissionsService {
	
	private final RequestService requestService;
	private final ReportableEmissionsService reportableEmissionsService;

	@Transactional
	public void updateReportableEmissions(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final DreRequestPayload requestPayload = (DreRequestPayload)request.getPayload();
		final DreRequestMetadata metadata = (DreRequestMetadata)request.getMetadata();
		
		reportableEmissionsService.saveReportableEmissions(ReportableEmissionsSaveParams.builder()
                .accountId(request.getAccountId())
                .year(metadata.getYear())
                .reportableEmissions(requestPayload.getDre().getTotalReportableEmissions())
                .isFromDre(true)
                .build());
	}
}
