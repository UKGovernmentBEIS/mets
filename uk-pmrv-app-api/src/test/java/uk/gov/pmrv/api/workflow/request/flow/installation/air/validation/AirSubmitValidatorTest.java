package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementFallback;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;

@ExtendWith(MockitoExtension.class)
class AirSubmitValidatorTest {

    @InjectMocks
    private AirSubmitValidator validator;

    @Test
    void validate_whenMissingKeys_thenThrowException() {

        final Map<Integer, AirImprovement> airImprovements = Map.of(
            1, AirImprovementCalculationCO2.builder().build(),
            2, AirImprovementFallback.builder().build()
        );

        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses =
            Map.of(1, OperatorAirImprovementYesResponse.builder().proposal("proposal").build());


        BusinessException be = assertThrows(BusinessException.class,
            () -> validator.validate(operatorImprovementResponses, airImprovements));

        assertEquals(ErrorCode.INVALID_AIR, be.getErrorCode());
    }


    @Test
    void validate_whenKeysExist_thenOK() {

        final Map<Integer, AirImprovement> airImprovements = Map.of(
            1, AirImprovementCalculationCO2.builder().build(),
            2, AirImprovementFallback.builder().build()
        );

        final Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses =
            Map.of(
                1, OperatorAirImprovementYesResponse.builder().proposal("proposal").build(),
                2, OperatorAirImprovementYesResponse.builder().proposal("proposal").build()
            );


        assertDoesNotThrow(() -> validator.validate(operatorImprovementResponses, airImprovements));
    }
}
