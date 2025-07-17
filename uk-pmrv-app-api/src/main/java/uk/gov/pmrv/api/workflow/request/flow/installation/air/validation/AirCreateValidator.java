package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirCreateImprovementDataService;

import java.util.Set;

@Service
public class AirCreateValidator extends RequestCreateAccountRelatedValidator {

    private final PermitQueryService permitQueryService;
    private final AirCreateImprovementDataService airCreateImprovementDataService;
    
    public AirCreateValidator(final RequestCreateValidatorService requestCreateValidatorService,
                              final AirCreateImprovementDataService airCreateImprovementDataService,
                              final PermitQueryService permitQueryService) {
        
        super(requestCreateValidatorService);
        this.permitQueryService = permitQueryService;
        this.airCreateImprovementDataService = airCreateImprovementDataService;
    }

    @Override
    public AirRequestCreateValidationResult validateAction(final Long accountId) {
        
        final RequestCreateValidationResult result = super.validateAction(accountId);
        if (!result.isValid() && !result.getApplicableAccountStatuses().isEmpty()) {
            return AirRequestCreateValidationResult.builder()
                .valid(false)
                .isAvailable(true) // this might not really be true (e.g. for SURRENDER account of HSE type), but the account status check takes priority
                .reportedAccountStatus(result.getReportedAccountStatus())
                .applicableAccountStatuses(result.getApplicableAccountStatuses())
                .build();
        }
        
        final PermitContainer permitContainer = permitQueryService.getPermitContainerByAccountId(accountId);
        if (permitContainer.getPermitType() == PermitType.HSE) {
            return AirRequestCreateValidationResult.builder().isAvailable(false).build();
        }
        
        final Permit permit = permitContainer.getPermit();
        final boolean improvementsExist = !airCreateImprovementDataService.createImprovementData(permit).isEmpty();
        
        return AirRequestCreateValidationResult.builder()
            .valid(result.isValid() && improvementsExist)
            .reportedAccountStatus(result.getReportedAccountStatus())
            .applicableAccountStatuses(result.getApplicableAccountStatuses())
            .reportedRequestTypes(result.getReportedRequestTypes())
            .improvementsExist(improvementsExist)
            .build();
    }
    
    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
            InstallationAccountStatus.LIVE
        );
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of(RequestType.AIR);
    }
    
    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AIR;
    }
}
