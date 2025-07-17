package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;

@Service
@RequiredArgsConstructor
public class RequestVerificationService {

    private final VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;
    
    @Transactional
    public void refreshVerificationReportVBDetails(VerificationReport verificationReport, Long requestVBId) {
    	if(verificationReport == null) {
    		return;
    	}
    	
    	final Long verificationReportVBId = verificationReport.getVerificationBodyId();
		verificationBodyDetailsQueryService
				.getVerificationBodyDetails(verificationReportVBId != null ? verificationReportVBId : requestVBId)
				.ifPresent(latestVBDetails -> verificationReport.setVerificationBodyDetails(latestVBDetails));
	}
    
}
