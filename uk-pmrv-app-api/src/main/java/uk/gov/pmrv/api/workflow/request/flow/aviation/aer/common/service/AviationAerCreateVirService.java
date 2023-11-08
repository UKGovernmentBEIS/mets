package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataReportable;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirCreationService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@Service
@RequiredArgsConstructor
public class AviationAerCreateVirService {

    private final RequestService requestService;
    private final AviationVirCreationService virCreationService;
    private final RequestCreateValidatorService requestCreateValidatorService;
    private final AviationAerService aerService;

    @Transactional
    public void createRequestVir(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final AviationAerRequestPayload requestPayload = (AviationAerRequestPayload) request.getPayload();
        final RequestMetadataReportable aerRequestMetadata = (RequestMetadataReportable) request.getMetadata();

        // Triggered ONLY at 1st Operator submission of verified AER to Regulator
        if (!requestPayload.isVirTriggered() &&
            requestPayload.isVerificationPerformed() &&
            this.isValidForVir(request.getAccountId(), requestPayload) &&
            !aerService.existsAerByAccountIdAndYear(request.getAccountId(), aerRequestMetadata.getYear())) {

            virCreationService.createRequestVir(requestId);
            requestPayload.setVirTriggered(true);
        }
    }

    private boolean isValidForVir(final long accountId, final AviationAerRequestPayload aerRequestPayload) {

        // VIR is triggered only for new and live accounts
        final RequestCreateAccountStatusValidationResult validationAccountStatusResult = 
            requestCreateValidatorService.validateAccountStatuses(
                accountId, 
                Set.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE)
            );
        if (!validationAccountStatusResult.isValid()) {
            return false;
        }

        // Check if data applicable for VIR
        return aerRequestPayload.getVerificationData().isValidForVir();
    }
}
