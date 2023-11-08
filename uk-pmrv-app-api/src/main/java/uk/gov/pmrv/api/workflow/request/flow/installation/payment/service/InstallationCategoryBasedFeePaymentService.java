package uk.gov.pmrv.api.workflow.request.flow.installation.payment.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.transform.InstallationCategoryMapper;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.payment.service.PaymentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.math.BigDecimal;

@Service
public class InstallationCategoryBasedFeePaymentService extends PaymentService {

    private final InstallationAccountQueryService installationAccountQueryService;

    public InstallationCategoryBasedFeePaymentService(PaymentFeeMethodRepository paymentFeeMethodRepository,
                                                      InstallationAccountQueryService installationAccountQueryService) {
        super(paymentFeeMethodRepository);
        this.installationAccountQueryService = installationAccountQueryService;
    }

    @Override
    public FeeMethodType getFeeMethodType() {
        return FeeMethodType.INSTALLATION_CATEGORY_BASED;
    }

    @Override
    public FeeType resolveFeeType(Request request) {
        EmitterType emitterType;
        InstallationCategory installationCategory;
        if(request.getType() == RequestType.PERMIT_ISSUANCE) {
            PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
            emitterType = requestPayload.getPermitType() == PermitType.HSE ? EmitterType.HSE : EmitterType.GHGE;
            BigDecimal estimatedAnnualEmissions = requestPayload.getPermit().getEstimatedAnnualEmissions().getQuantity();
            installationCategory = InstallationCategoryMapper.getInstallationCategory(emitterType, estimatedAnnualEmissions);
        } else {
            InstallationAccountInfoDTO accountInfo = installationAccountQueryService.getInstallationAccountInfoDTOById(request.getAccountId());
            emitterType = accountInfo.getEmitterType();
            installationCategory = accountInfo.getInstallationCategory();
        }

        return resolveFeeType(emitterType, installationCategory);
    }

    private FeeType resolveFeeType(EmitterType emitterType, InstallationCategory installationCategory) {
        if(emitterType == null || installationCategory == null) {
            return null;
        }

        if(emitterType == EmitterType.HSE) {
            return FeeType.HSE;
        } else {
            switch(installationCategory) {
                case A_LOW_EMITTER:
                case A:
                    return  FeeType.CAT_A;
                case B:
                    return FeeType.CAT_B;
                case C:
                    return FeeType.CAT_C;
                default:
                    return null;
            }
        }
    }
}
