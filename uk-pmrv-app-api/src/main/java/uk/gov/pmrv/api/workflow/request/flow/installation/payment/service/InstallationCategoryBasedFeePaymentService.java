package uk.gov.pmrv.api.workflow.request.flow.installation.payment.service;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.transform.InstallationCategoryMapper;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.DigitizedPlan;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.payment.service.PaymentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import org.apache.commons.lang3.Range;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

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
            PermitType permitType = requestPayload.getPermitType();

            if(permitType.equals(PermitType.HSE)) {
                emitterType = EmitterType.HSE;
            } else if (permitType.equals(PermitType.GHGE)) {
                emitterType = EmitterType.GHGE;
                FeeType nrwFeeType = resolveNrwFeeType(request, requestPayload.getPermit());
                if (nrwFeeType != null) {
                    return nrwFeeType;
                }
            } else {
                emitterType = EmitterType.WASTE;
            }

            BigDecimal estimatedAnnualEmissions = requestPayload.getPermit().getEstimatedAnnualEmissions().getQuantity();
            installationCategory = InstallationCategoryMapper.getInstallationCategory(emitterType, estimatedAnnualEmissions);
        } else {
            InstallationAccountInfoDTO accountInfo = installationAccountQueryService.getInstallationAccountInfoDTOById(request.getAccountId());
            emitterType = accountInfo.getEmitterType();
            installationCategory = accountInfo.getInstallationCategory();
        }

        return resolveFeeType(emitterType, installationCategory);
    }

    private FeeType resolveNrwFeeType(Request request, Permit permit) {
        if (request.getCompetentAuthority() != CompetentAuthorityEnum.WALES) {
            return null;
        }
        InstallationAccountInfoDTO accountInfo = installationAccountQueryService
                .getInstallationAccountInfoDTOById(request.getAccountId());
        if (!Boolean.TRUE.equals(accountInfo.getFaStatus())) {
            return null;
        }
        int subInstallationCount = countSubInstallations(permit);
        if (Range.of(1, 2).contains(subInstallationCount)) {
            return FeeType.NRW_CAT_FA_1_TO_2;
        }
        if (Range.of(3, Integer.MAX_VALUE).contains(subInstallationCount)) {
            return FeeType.NRW_CAT_FA_3_PLUS;
        }
        return null;
    }

    private int countSubInstallations(Permit permit) {
        return Optional.ofNullable(permit.getMonitoringMethodologyPlans())
                .map(MonitoringMethodologyPlans::getDigitizedPlan)
                .map(DigitizedPlan::getSubInstallations)
                .map(Collection::size)
                .orElse(0);
    }

    private FeeType resolveFeeType(EmitterType emitterType, InstallationCategory installationCategory) {
        if(emitterType == null || installationCategory == null) {
            return null;
        }

        return switch (emitterType) {
            case EmitterType.HSE -> FeeType.HSE;
            case EmitterType.GHGE -> switch (installationCategory) {
                case A_LOW_EMITTER, A -> FeeType.CAT_A;
                case B -> FeeType.CAT_B;
                case C -> FeeType.CAT_C;
                default -> null;
            };
            default -> FeeType.WASTE;
        };
    }
}
