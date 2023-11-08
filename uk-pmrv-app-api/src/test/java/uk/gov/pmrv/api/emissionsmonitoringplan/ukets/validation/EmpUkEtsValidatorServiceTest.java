package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.EmpBlockHourMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;


import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpUkEtsValidatorServiceTest {

    @InjectMocks
    private EmpUkEtsValidatorService empUkEtsValidatorService;

    @Spy
    private ArrayList<EmpUkEtsContextValidator> empUkEtsContextValidators;

    @Mock
    private EmpOperatorDetailsSectionValidator operatorDetailsSectionValidator;

    @Mock
    private EmpEmissionSourcesSectionValidator emissionSourcesSectionValidator;

    @Mock
    private EmpManagementProceduresSectionValidator managementProceduresSectionValidator;

    @Mock
    private EmpMethodAProceduresSectionValidator methodAProceduresSectionValidator;

    @Mock
    private EmpMethodBProceduresSectionValidator methodBProceduresSectionValidator;

    @Mock
    private EmpBlockOnBlockOffMethodProceduresSectionValidator blockOnBlockOffMethodProceduresSectionValidator;

    @Mock
    private EmpFuelUpliftMethodProceduresSectionValidator fuelUpliftMethodProceduresSectionValidator;

    @Mock
    private EmpDataGapsSectionValidator dataGapsSectionValidator;

    @Mock
    private EmpBlockHourMethodProceduresSectionValidator blockHourMethodProceduresSectionValidator;

    @BeforeEach
    void setUp() {
        empUkEtsContextValidators.add(operatorDetailsSectionValidator);
        empUkEtsContextValidators.add(emissionSourcesSectionValidator);
        empUkEtsContextValidators.add(managementProceduresSectionValidator);
        empUkEtsContextValidators.add(methodAProceduresSectionValidator);
        empUkEtsContextValidators.add(methodBProceduresSectionValidator);
        empUkEtsContextValidators.add(blockOnBlockOffMethodProceduresSectionValidator);
        empUkEtsContextValidators.add(fuelUpliftMethodProceduresSectionValidator);
        empUkEtsContextValidators.add(dataGapsSectionValidator);
        empUkEtsContextValidators.add(blockHourMethodProceduresSectionValidator);

    }

    @Test
    void validateEmissionsMonitoringPlan_valid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();

        when(operatorDetailsSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(emissionSourcesSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(managementProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(methodAProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(methodBProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(blockOnBlockOffMethodProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(fuelUpliftMethodProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(dataGapsSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());
        when(blockHourMethodProceduresSectionValidator.validate(empContainer)).thenReturn(EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan());


        empUkEtsValidatorService.validateEmissionsMonitoringPlan(empContainer);

        verify(operatorDetailsSectionValidator, times(1)).validate(empContainer);
        verify(emissionSourcesSectionValidator, times(1)).validate(empContainer);
        verify(managementProceduresSectionValidator, times(1)).validate(empContainer);
        verify(methodAProceduresSectionValidator, times(1)).validate(empContainer);
        verify(methodBProceduresSectionValidator, times(1)).validate(empContainer);
        verify(blockOnBlockOffMethodProceduresSectionValidator, times(1)).validate(empContainer);
        verify(fuelUpliftMethodProceduresSectionValidator, times(1)).validate(empContainer);
        verify(dataGapsSectionValidator, times(1)).validate(empContainer);
        verify(blockHourMethodProceduresSectionValidator, times(1)).validate(empContainer);

    }

    @Test
    void validateEmissionsMonitoringPlan_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();

        when(operatorDetailsSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(EmpOperatorDetails.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ISSUING_AUTHORITY))));
        when(emissionSourcesSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(EmpOperatorDetails.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ADDITIONAL_AIRCRAFT_MONITORING_APPROACH))));
        when(managementProceduresSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(ManagementProcedures.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES))));
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
        when(dataGapsSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(EmpDataGaps.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_DATA_GAPS))));
        when(blockHourMethodProceduresSectionValidator.validate(empContainer))
                .thenReturn(EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(List.of(
                        new EmissionsMonitoringPlanViolation(EmpBlockHourMethodProcedures.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_BLOCK_HOUR_METHOD_PROCEDURES))));
        BusinessException be = assertThrows(BusinessException.class,
            () -> empUkEtsValidatorService.validateEmissionsMonitoringPlan(empContainer));

        verify(operatorDetailsSectionValidator, times(1)).validate(empContainer);
        verify(emissionSourcesSectionValidator, times(1)).validate(empContainer);
        verify(managementProceduresSectionValidator, times(1)).validate(empContainer);
        verify(methodAProceduresSectionValidator, times(1)).validate(empContainer);
        verify(methodBProceduresSectionValidator, times(1)).validate(empContainer);
        verify(blockOnBlockOffMethodProceduresSectionValidator, times(1)).validate(empContainer);
        verify(fuelUpliftMethodProceduresSectionValidator, times(1)).validate(empContainer);
        verify(dataGapsSectionValidator, times(1)).validate(empContainer);
        verify(blockHourMethodProceduresSectionValidator, times(1)).validate(empContainer);
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMP);
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.UK_ETS_AVIATION, empUkEtsValidatorService.getEmissionTradingScheme());
    }

}