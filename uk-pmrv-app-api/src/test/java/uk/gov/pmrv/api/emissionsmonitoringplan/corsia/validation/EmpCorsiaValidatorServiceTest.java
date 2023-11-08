package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.EmpBlockHourMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation.EmpBlockHourMethodProceduresSectionValidator;

@ExtendWith(MockitoExtension.class)
class EmpCorsiaValidatorServiceTest {

    @InjectMocks
    private EmpCorsiaValidatorService empCorsiaValidatorService;

    @Spy
    private ArrayList<EmpCorsiaContextValidator> empCorsiaValidatorServices;

    @Mock
    private EmpCorsiaOperatorDetailsSectionValidator operatorDetailsSectionValidator;

    @Mock
    private EmpCorsiaEmissionSourcesSectionValidator emissionSourcesSectionValidator;

    @Mock
    private EmpCorsiaCorsiaMethodAProceduresSectionValidator methodAProceduresSectionValidator;

    @Mock
    private EmpCorsiaCorsiaMethodBProceduresSectionValidator methodBProceduresSectionValidator;

    @Mock
    private EmpCorsiaBlockOnBlockOffMethodProceduresSectionValidator blockOnBlockOffMethodProceduresSectionValidator;

    @Mock
    private EmpCorsiaFuelUpliftMethodProceduresSectionValidator fuelUpliftMethodProceduresSectionValidator;

    @Mock
    private EmpCorsiaBlockHourMethodProceduresSectionValidator blockHourMethodProceduresSectionValidator;

    @BeforeEach
    void setUp() {
        empCorsiaValidatorServices.add(operatorDetailsSectionValidator);
        empCorsiaValidatorServices.add(emissionSourcesSectionValidator);
        empCorsiaValidatorServices.add(methodAProceduresSectionValidator);
        empCorsiaValidatorServices.add(methodBProceduresSectionValidator);
        empCorsiaValidatorServices.add(blockOnBlockOffMethodProceduresSectionValidator);
        empCorsiaValidatorServices.add(fuelUpliftMethodProceduresSectionValidator);
        empCorsiaValidatorServices.add(blockHourMethodProceduresSectionValidator);
    }

    @Test
    void validateEmissionsMonitoringPlan_valid() {
        EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder().build();

        when(operatorDetailsSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(emissionSourcesSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(methodAProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(methodBProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(blockOnBlockOffMethodProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(fuelUpliftMethodProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(blockHourMethodProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());

        empCorsiaValidatorService.validateEmissionsMonitoringPlan(empContainer);

        verify(operatorDetailsSectionValidator, times(1)).validate(empContainer);
        verify(emissionSourcesSectionValidator, times(1)).validate(empContainer);
        verify(methodAProceduresSectionValidator, times(1)).validate(empContainer);
        verify(methodBProceduresSectionValidator, times(1)).validate(empContainer);
        verify(blockOnBlockOffMethodProceduresSectionValidator, times(1)).validate(empContainer);
        verify(fuelUpliftMethodProceduresSectionValidator, times(1)).validate(empContainer);
        verify(blockHourMethodProceduresSectionValidator, times(1)).validate(empContainer);
    }

    @Test
    void validateEmissionsMonitoringPlan_invalid() {
        EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder().build();

        when(operatorDetailsSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(EmpCorsiaOperatorDetails.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ISSUING_AUTHORITY))));
        when(emissionSourcesSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(EmpEmissionSourcesCorsia.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_CONSUMPTION_MEASURING_METHOD))));
        when(methodAProceduresSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(EmpMethodAProcedures.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_METHOD_A_PROCEDURES))));
        when(methodBProceduresSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(EmpMethodBProcedures.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_METHOD_B_PROCEDURES))));
        when(blockOnBlockOffMethodProceduresSectionValidator.validate(empContainer))
            .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                new EmissionsMonitoringPlanViolation(EmpBlockOnBlockOffMethodProcedures.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_BLOCK_ON_BLOCK_OFF_METHOD_PROCEDURES))));
        when(fuelUpliftMethodProceduresSectionValidator.validate(empContainer))
            .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                new EmissionsMonitoringPlanViolation(EmpFuelUpliftMethodProcedures.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_UPLIFT_METHOD_PROCEDURES))));
        when(blockHourMethodProceduresSectionValidator.validate(empContainer))
            .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                new EmissionsMonitoringPlanViolation(EmpBlockHourMethodProcedures.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_BLOCK_HOUR_METHOD_PROCEDURES))));
        BusinessException be = assertThrows(BusinessException.class,
            () -> empCorsiaValidatorService.validateEmissionsMonitoringPlan(empContainer));

        verify(operatorDetailsSectionValidator, times(1)).validate(empContainer);
        verify(emissionSourcesSectionValidator, times(1)).validate(empContainer);
        verify(methodAProceduresSectionValidator, times(1)).validate(empContainer);
        verify(methodBProceduresSectionValidator, times(1)).validate(empContainer);
        verify(blockOnBlockOffMethodProceduresSectionValidator, times(1)).validate(empContainer);
        verify(fuelUpliftMethodProceduresSectionValidator, times(1)).validate(empContainer);
        verify(blockHourMethodProceduresSectionValidator, times(1)).validate(empContainer);
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMP);
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.CORSIA, empCorsiaValidatorService.getEmissionTradingScheme());
    }

}