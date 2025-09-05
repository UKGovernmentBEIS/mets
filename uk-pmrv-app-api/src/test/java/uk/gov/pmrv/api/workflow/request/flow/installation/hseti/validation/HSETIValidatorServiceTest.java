package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HSETIValidatorServiceTest {

    @InjectMocks
    private HSETIValidatorService hsetiValidatorService;

    @Test
    void validateHSETI() {
        UUID hsetiFile = UUID.randomUUID();
        HSETI hseti = HSETI.builder().hsetiFile(hsetiFile).files(Set.of(hsetiFile)).notes("test").build();

        hsetiValidatorService.validateHSETI(hseti);
    }

    @Test
    void validateReturnForAmends_regulatorReviewGroupDecisionIsEmpty_throwBusinessException() {

        UUID attachment1 = UUID.randomUUID();

        Map<UUID, String> regulatorReviewAttachments = Map.of(attachment1, "test");

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .regulatorReviewAttachments(regulatorReviewAttachments)
                        .regulatorReviewSectionsCompleted(Map.of("test",true))
                        .regulatorReviewGroupDecisions(new HashMap<>())
                        .build();

        BusinessException be = assertThrows(BusinessException.class, () ->
                hsetiValidatorService.validateReturnForAmends(taskPayload));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_HSE_TI_REVIEW);
    }

    @Test
    void validateReturnForAmends_regulatorReviewDecisionTypeIsNotOperatorAmendsNeeded_throwBusinessException() {

        UUID attachment1 = UUID.randomUUID();

        HSETIRegulatorReviewDecision regulatorReviewDecision = HSETIRegulatorReviewDecision
                                        .builder()
                                        .type(HSETIRegulatorReviewDecisionType.ACCEPTED)
                                        .details(HSETIRegulatorReviewDecisionAcceptedDetails
                                                .builder()
                                                .build())
                                        .build();

        Map<UUID, String> regulatorReviewAttachments = Map.of(attachment1, "test");

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .regulatorReviewAttachments(regulatorReviewAttachments)
                        .regulatorReviewSectionsCompleted(Map.of("test",true))
                        .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI,regulatorReviewDecision))
                        .build();

        BusinessException be = assertThrows(BusinessException.class, () ->
                hsetiValidatorService.validateReturnForAmends(taskPayload));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_HSE_TI_REVIEW);
    }

    @Test
    void validateReturnForAmends_regulatorReviewDecisionTypeIsOperatorAmendsNeeded_doNotThrowException() {

        UUID attachment1 = UUID.randomUUID();

        HSETIRegulatorReviewDecision regulatorReviewDecision = HSETIRegulatorReviewDecision
                                        .builder()
                                        .type(HSETIRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                                        .details(HSETIRegulatorReviewDecisionOperatorAmendsNeededDetails
                                                .builder()
                                                .requiredChanges(List.of(HSETIRegulatorReviewOperatorAmendsRequiredChange
                                                        .builder()
                                                        .reason("test reason")
                                                        .build()))
                                                .build())
                                        .build();

        Map<UUID, String> regulatorReviewAttachments = Map.of(attachment1, "test");

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .regulatorReviewAttachments(regulatorReviewAttachments)
                        .regulatorReviewSectionsCompleted(Map.of("test",true))
                        .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI,regulatorReviewDecision))
                        .build();

        assertDoesNotThrow(() ->  hsetiValidatorService.validateReturnForAmends(taskPayload));
    }

    @Test
    void validateRegulatorReview_aRejectedGroupDecisionExistsAndOverallDecisionIsRejected_doNotThrowException() {

        UUID attachment1 = UUID.randomUUID();
        HSETIRegulatorReviewDecision regulatorReviewDecision = HSETIRegulatorReviewDecision
                                        .builder()
                                        .type(HSETIRegulatorReviewDecisionType.REJECTED)
                                        .details(HSETIRegulatorReviewDecisionRejectedDetails
                                                .builder()
                                                .build())
                                        .build();

        Map<UUID, String> regulatorReviewAttachments = Map.of(attachment1, "test");

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .overallDecision(HSETIRegulatorReviewOverallDecision
                                .builder()
                                .reason("test reason")
                                .type(HSETIRegulatorReviewOverallDecisionType.REJECTED)
                                .build())
                        .regulatorReviewAttachments(regulatorReviewAttachments)
                        .regulatorReviewSectionsCompleted(Map.of("test",true))
                        .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI,regulatorReviewDecision))
                        .build();

        assertDoesNotThrow(() ->  hsetiValidatorService.validateRegulatorReview(taskPayload));
    }

    @Test
    void validateRegulatorReview_aRejectedGroupDecisionDoesNotExistAndOverallDecisionIsRejected_throwBusinessException() {

        UUID attachment1 = UUID.randomUUID();
        HSETIRegulatorReviewDecision regulatorReviewDecision = HSETIRegulatorReviewDecision
                                        .builder()
                                        .type(HSETIRegulatorReviewDecisionType.ACCEPTED)
                                        .details(HSETIRegulatorReviewDecisionRejectedDetails
                                                .builder()
                                                .build())
                                        .build();

        Map<UUID, String> regulatorReviewAttachments = Map.of(attachment1, "test");

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .overallDecision(HSETIRegulatorReviewOverallDecision
                                .builder()
                                .reason("test reason")
                                .type(HSETIRegulatorReviewOverallDecisionType.REJECTED)
                                .build())
                        .regulatorReviewAttachments(regulatorReviewAttachments)
                        .regulatorReviewSectionsCompleted(Map.of("test",true))
                        .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI,regulatorReviewDecision))
                        .build();

        BusinessException be = assertThrows(BusinessException.class, () ->
                hsetiValidatorService.validateRegulatorReview(taskPayload));
		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_HSE_TI_REVIEW);
    }

}
