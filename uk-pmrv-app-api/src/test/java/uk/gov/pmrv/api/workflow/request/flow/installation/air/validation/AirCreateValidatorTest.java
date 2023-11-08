package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationPFC;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirCreateImprovementDataService;

@ExtendWith(MockitoExtension.class)
class AirCreateValidatorTest {
    
    @InjectMocks
    private AirCreateValidator validator;
    
    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private AirCreateImprovementDataService airCreateImprovementDataService;

    @Test
    void validate_whenHSE_thenInvalid() {

        final long accountId = 1L;

        when(requestCreateValidatorService.validate(
            accountId,
            validator.getApplicableAccountStatuses(),
            validator.getMutuallyExclusiveRequests())
        ).thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(permitQueryService.getPermitContainerByAccountId(accountId))
            .thenReturn(PermitContainer.builder().permitType(PermitType.HSE).build());

        final RequestCreateValidationResult result = validator.validateAction(accountId);

        assertFalse(result.isValid());
        assertFalse(result.isAvailable());
    }

    @Test
    void validate_whenNoImprovements_thenInvalid() {

        final long accountId = 1L;

        final Permit permit = Permit.builder().build();
        final PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(permit)
            .build();
        
        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        
        when(airCreateImprovementDataService.createImprovementData(permit)).thenReturn(List.of());

        when(requestCreateValidatorService.validate(
            accountId,
            validator.getApplicableAccountStatuses(),
            validator.getMutuallyExclusiveRequests())
        ).thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        final AirRequestCreateValidationResult result = validator.validateAction(accountId);

        assertFalse(result.isValid());
        assertFalse(result.isImprovementsExist());
    }
    
    @Test
    void validate_whenOK_thenValid() {

        final long accountId = 1L;

        final Permit permit = Permit.builder().build();
        final PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(permit)
            .build();

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);

        when(airCreateImprovementDataService.createImprovementData(permit))
            .thenReturn(List.of(AirImprovementCalculationPFC.builder().build()));

        when(requestCreateValidatorService.validate(
            accountId,
            validator.getApplicableAccountStatuses(),
            validator.getMutuallyExclusiveRequests())
        ).thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        final AirRequestCreateValidationResult result = validator.validateAction(accountId);

        assertTrue(result.isValid());
        assertTrue(result.isImprovementsExist());
    }
}
