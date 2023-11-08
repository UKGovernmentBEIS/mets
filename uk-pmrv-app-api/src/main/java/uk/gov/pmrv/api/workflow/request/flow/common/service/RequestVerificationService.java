package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayloadVerifiable;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestVerificationService<T extends VerificationReport> {

    private final VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    public void clearVerificationReport(RequestPayloadVerifiable<T> requestPayload, Long vbId) {
        Optional.ofNullable(requestPayload.getVerificationReport()).ifPresent(verificationReport -> {
            if(!vbId.equals(verificationReport.getVerificationBodyId())) {
                requestPayload.setVerificationReport(null);
            }
        });
    }

    public VerificationBodyDetails getVerificationBodyDetails(T verificationReport, Long vbId) {
        Long verificationBodyId = verificationReport != null
            ? Optional.ofNullable(verificationReport.getVerificationBodyId()).orElse(vbId)
            : vbId;

        return verificationBodyDetailsQueryService.getVerificationBodyDetails(verificationBodyId);
    }
}
