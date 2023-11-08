package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.service.AerService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirCreationService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AerCreateVirService {

    private final RequestService requestService;
    private final VirCreationService virCreationService;
    private final RequestCreateValidatorService requestCreateValidatorService;
    private final AerService aerService;

    @Transactional
    public void createRequestVir(String requestId) {
        Request request = requestService.findRequestById(requestId);
        AerRequestPayload requestPayload = (AerRequestPayload) request.getPayload();
        AerRequestMetadata aerRequestMetadata = (AerRequestMetadata) request.getMetadata();

        // Triggered ONLY at 1st Operator submission of verified AER to Regulator for GHGE
        if (!requestPayload.isVirTriggered() && requestPayload.isVerificationPerformed()
            && isValidForVir(request.getAccountId(), requestPayload)
            && !aerService.existsAerByAccountIdAndYear(request.getAccountId(), aerRequestMetadata.getYear())) {
            virCreationService.createRequestVir(requestId);
            requestPayload.setVirTriggered(true);
        }
    }

    private boolean isValidForVir(long accountId, AerRequestPayload aerRequestPayload) {
        // VIR is triggered only for Live accounts
        RequestCreateAccountStatusValidationResult validationAccountStatusResult = requestCreateValidatorService
            .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
        if (!validationAccountStatusResult.isValid()) {
            return false;
        }

        // Check if data applicable for VIR
        AerVerificationData aerVerificationData = aerRequestPayload.getVerificationReport().getVerificationData();
        InstallationCategory installationCategory = aerRequestPayload.getPermitOriginatedData().getInstallationCategory();
        PermitType permitType = aerRequestPayload.getPermitOriginatedData().getPermitType();

        List<InstallationCategory> applicableCategoriesForRecommendedImprovements = List.of(
            InstallationCategory.A_LOW_EMITTER,
            InstallationCategory.A,
            InstallationCategory.B, InstallationCategory.C
        );

        return PermitType.GHGE.equals(permitType)
            && applicableCategoriesForRecommendedImprovements.contains(installationCategory)
            && isValidForVir(installationCategory, aerVerificationData);
    }

    private boolean isValidForVir(InstallationCategory category, AerVerificationData aerVerificationData) {
        if (category == InstallationCategory.A_LOW_EMITTER) {
            return Boolean.TRUE.equals(aerVerificationData.getUncorrectedNonConformities().getAreThereUncorrectedNonConformities())
                || Boolean.TRUE.equals(aerVerificationData.getUncorrectedNonConformities().getAreTherePriorYearIssues());
        }
        return Boolean.TRUE.equals(aerVerificationData.getUncorrectedNonConformities().getAreThereUncorrectedNonConformities())
            || Boolean.TRUE.equals(aerVerificationData.getUncorrectedNonConformities().getAreTherePriorYearIssues())
            || Boolean.TRUE.equals(aerVerificationData.getRecommendedImprovements().getAreThereRecommendedImprovements());
    }
}
