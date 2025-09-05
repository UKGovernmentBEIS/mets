package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecisionOperatorAmendsNeededDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOperatorAmendsRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation.HSETIValidatorService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETIRegulatorReviewSubmitServiceTest {

    @InjectMocks
    private HSETIRegulatorReviewSubmitService service;

    @Mock
    private HSETIValidatorService validationService;

    @Mock
    private RequestService requestService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void submit() {
        UUID attachmentId = UUID.randomUUID();
        AppUser user = AppUser.builder().build();
        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI, HSETIRegulatorReviewDecision.builder().build()))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        HSETIRequestPayload requestPayload = HSETIRequestPayload
                .builder()
                .build();

        Request request = Request.builder().type(RequestType.HSE_TI).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        Set<String> operators = Set.of("oper");
        String signatory = "sign";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(signatory)
                .operators(operators)
                .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, user))
                .thenReturn(true);

        service.submit(requestTask,decisionNotification, user);

        verify(validationService, times(1))
                .validateRegulatorReview(taskPayload);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, user);

        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(requestPayload.getRegulatorReviewGroupDecisions()).isEqualTo(taskPayload.getRegulatorReviewGroupDecisions());
        assertThat(requestPayload.getRegulatorReviewAttachments()).isEqualTo(taskPayload.getRegulatorReviewAttachments());
        assertThat(requestPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void submit_decisionNotificationUsersAreNotValid_throwBusinessException() {
        UUID attachmentId = UUID.randomUUID();
        AppUser user = AppUser.builder().build();
        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI, HSETIRegulatorReviewDecision.builder().build()))
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        HSETIRequestPayload requestPayload = HSETIRequestPayload
                .builder()
                .build();

        Request request = Request.builder().type(RequestType.HSE_TI).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        Set<String> operators = Set.of("oper");
        String signatory = "sign";

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(signatory)
                .operators(operators)
                .build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, user))
                .thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class, () ->
                service.submit(requestTask,decisionNotification, user));

		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(validationService, times(1))
                .validateRegulatorReview(taskPayload);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification, user);

    }


    @Test
    void saveReviewGroupDecision() {
          HSETIApplicationSaveRegulatorReviewGroupDecisionRequestTaskActionPayload taskActionPayload =
                HSETIApplicationSaveRegulatorReviewGroupDecisionRequestTaskActionPayload
                        .builder()
                        .group(HSETIReviewGroup.HSETI)
                        .decision(HSETIRegulatorReviewDecision
                                    .builder()
                                    .type(HSETIRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                                    .details(HSETIRegulatorReviewDecisionOperatorAmendsNeededDetails
                                            .builder()
                                            .requiredChanges(List.of(HSETIRegulatorReviewOperatorAmendsRequiredChange
                                                    .builder()
                                                    .reason("test reason")
                                                    .build()))
                                            .build())
                                .build())
                        .regulatorReviewSectionsCompleted(Map.of("test", true))
                        .build();


        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .build();

        RequestTask task = RequestTask.builder().payload(taskPayload).build();

        service.saveReviewGroupDecision(taskActionPayload, task);

        assertThat(taskPayload.getRegulatorReviewGroupDecisions().get(taskActionPayload.getGroup())).isEqualTo(taskActionPayload.getDecision());
        assertThat(taskPayload.getRegulatorReviewSectionsCompleted()).containsExactlyEntriesOf(taskActionPayload.getRegulatorReviewSectionsCompleted());
    }


    @Test
    void save() {
        HSETIApplicationRegulatorReviewSaveTaskActionPayload taskActionPayload =
                HSETIApplicationRegulatorReviewSaveTaskActionPayload
                        .builder()
                        .overallDecision(HSETIRegulatorReviewOverallDecision
                                .builder()
                                .type(HSETIRegulatorReviewOverallDecisionType.APPROVED)
                                .reason("test reason")
                                .build())
                        .regulatorReviewSectionsCompleted(Map.of("test", true))
                        .build();


        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .build();

        RequestTask task = RequestTask.builder().payload(taskPayload).build();

        service.save(taskActionPayload, task);

        assertThat(taskPayload.getOverallDecision()).isEqualTo(taskActionPayload.getOverallDecision());
        assertThat(taskPayload.getRegulatorReviewSectionsCompleted()).containsExactlyEntriesOf(taskActionPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void returnForAmends() {

        AppUser user = AppUser.builder().userId("userid").build();

        UUID attachment1 = UUID.randomUUID();


        HSETIRegulatorReviewDecision  regulatorReviewDecision = HSETIRegulatorReviewDecision
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

        Request request = Request
                .builder()
                        .type(RequestType.HSE_TI)
                        .payload(HSETIRequestPayload.builder().build())
                        .id("req id")
                        .accountId(1L)
                        .metadata(HSETIRequestMetadata.builder().build())
                .build();

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .regulatorReviewAttachments(regulatorReviewAttachments)
                        .regulatorReviewSectionsCompleted(Map.of("test",true))
                        .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI,regulatorReviewDecision))
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(taskPayload).request(request).build();

        HSETIRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload =
                HSETIRegulatorReviewReturnedForAmendsRequestActionPayload
                        .builder()
                        .payloadType(RequestActionPayloadType.HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD)
                        .regulatorReviewAttachments(regulatorReviewAttachments)
                        .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI,regulatorReviewDecision))
                        .build();

        service.returnForAmends(requestTask, user);

        verify(validationService, times(1)).validateReturnForAmends(taskPayload);
        verify(requestService, times(1)).addActionToRequest(
                request,
                requestActionPayload,
                RequestActionType.HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS,
                "userid");

        assertThat(request.getPayload().getRegulatorReviewer())
                .isEqualTo("userid");

        assertThat(((HSETIRequestPayload) request.getPayload()).getRegulatorReviewGroupDecisions())
                .isEqualTo(taskPayload.getRegulatorReviewGroupDecisions());

        assertThat(((HSETIRequestPayload) request.getPayload()).getRegulatorReviewAttachments())
                .containsExactlyEntriesOf(taskPayload.getRegulatorReviewAttachments());

        assertThat(((HSETIRequestPayload) request.getPayload()).getRegulatorReviewSectionsCompleted())
                .containsExactlyEntriesOf(taskPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void updateRequestPayload() {

        AppUser user = AppUser.builder().userId("userid").build();

        UUID attachment1 = UUID.randomUUID();

        HSETIRegulatorReviewDecision  regulatorReviewDecision = HSETIRegulatorReviewDecision
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

        Request request = Request
                .builder()
                        .type(RequestType.HSE_TI)
                        .payload(HSETIRequestPayload.builder().build())
                        .id("req id")
                        .accountId(1L)
                        .metadata(HSETIRequestMetadata.builder().build())
                .build();

        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .regulatorReviewAttachments(regulatorReviewAttachments)
                        .regulatorReviewSectionsCompleted(Map.of("test",true))
                        .regulatorReviewGroupDecisions(Map.of(HSETIReviewGroup.HSETI,regulatorReviewDecision))
                        .overallDecision(HSETIRegulatorReviewOverallDecision
                                .builder()
                                .type(HSETIRegulatorReviewOverallDecisionType.APPROVED)
                                .reason("test reason")
                                .build())
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(taskPayload).request(request).build();

        service.updateRequestPayload(requestTask, user);

        assertThat(request.getPayload().getRegulatorReviewer())
                .isEqualTo("userid");

        assertThat(((HSETIRequestPayload) request.getPayload()).getOverallDecision())
                .isEqualTo(taskPayload.getOverallDecision());

        assertThat(((HSETIRequestPayload) request.getPayload()).getRegulatorReviewGroupDecisions())
                .isEqualTo(taskPayload.getRegulatorReviewGroupDecisions());

        assertThat(((HSETIRequestPayload) request.getPayload()).getRegulatorReviewAttachments())
                .containsExactlyEntriesOf(taskPayload.getRegulatorReviewAttachments());

        assertThat(((HSETIRequestPayload) request.getPayload()).getRegulatorReviewSectionsCompleted())
                .containsExactlyEntriesOf(taskPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void requestPeerReview() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId).build();
        final String selectedPeerReviewer = "selectedPeerReviewer";

        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        UUID hsetiFile = UUID.randomUUID();
        HSETI hseti = HSETI.builder().hsetiFile(hsetiFile).files(Set.of(hsetiFile)).notes("test").build();

        HSETIRequestPayload payload = HSETIRequestPayload.builder()
                .payloadType(RequestPayloadType.HSE_TI_REQUEST_PAYLOAD)
                .build();

        Request request = Request.builder()
                .payload(payload)
                .build();

        HSETIRequestPayload updatedPayload = (HSETIRequestPayload) request.getPayload();
        updatedPayload.setHseti(hseti);
        updatedPayload.setHsetiSectionsCompleted(sectionsCompleted);
        updatedPayload.setHsetiAttachments(attachments);
        updatedPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        updatedPayload.setRegulatorReviewer(user.getUserId());

        HSETIRequestPayload expectedPayload = HSETIRequestPayload.builder()
                .payloadType(RequestPayloadType.HSE_TI_REQUEST_PAYLOAD)
                .hseti(hseti)
                .hsetiSectionsCompleted(sectionsCompleted)
                .hsetiAttachments(attachments)
                .regulatorPeerReviewer(selectedPeerReviewer)
                .regulatorReviewer(user.getUserId())
                .build();

        Assertions.assertEquals(expectedPayload, updatedPayload);
    }
}
