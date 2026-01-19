package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecisionType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class WasteQDRValidationServiceTest {

    private final WasteQDRValidationService service = new WasteQDRValidationService();

    @Test
    void validateReturnForAmends_validDecision_doesNotThrow() {
        // Arrange
        WasteQDRReviewDecision decision = new WasteQDRReviewDecision();
        decision.setType(WasteQDRReviewDecisionType.OPERATOR_AMENDS_NEEDED);

        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                new WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload();
        payload.setReviewDecision(decision);

        // Act & Assert
        assertDoesNotThrow(() -> service.validateReturnForAmends(payload));
    }

    @Test
    void validateReturnForAmends_invalidDecision_throwsBusinessException() {
        // Arrange
        WasteQDRReviewDecision decision = new WasteQDRReviewDecision();
        decision.setType(WasteQDRReviewDecisionType.ACCEPTED); // ❌ invalid

        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                new WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload();
        payload.setReviewDecision(decision);

        // Act & Assert
        assertThatThrownBy(() -> service.validateReturnForAmends(payload))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid Waste QDR review");
    }
}
