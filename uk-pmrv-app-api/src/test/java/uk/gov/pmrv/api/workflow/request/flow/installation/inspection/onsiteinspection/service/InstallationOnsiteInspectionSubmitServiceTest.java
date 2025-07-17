package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.FollowUpAction;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.FollowUpActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.validation.InstallationInspectionValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.validation.InstallationOnsiteInspectionValidatorService;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionSubmitServiceTest {

    @InjectMocks
    private InstallationOnsiteInspectionSubmitService service;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOnsiteInspectionValidatorService installationOnsiteInspectionValidatorService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;


    @Test
    void getRequestType() {
        assertThat(service.getRequestType()).isEqualTo(RequestType.INSTALLATION_ONSITE_INSPECTION);
    }

    @Test
    void applySaveAction() {
        final List<FollowUpAction> expectedFollowupActions = new ArrayList<>();
        expectedFollowupActions.add(FollowUpAction.builder().explanation("Explanation").followUpActionType(FollowUpActionType.MISSTATEMENT).build());

        final InstallationInspection expectedInstallationInspection = InstallationInspection.builder().followUpActions(expectedFollowupActions).build();

        final Map<String, Boolean> expectedInstallationInspectionSectionsCompleted = new HashMap<>();
        expectedInstallationInspectionSectionsCompleted.put("followUpActions",false);

        final InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload expectedTaskPayload =
                InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload
                        .builder()
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final InstallationOnsiteInspectionApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                InstallationOnsiteInspectionApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SAVE_PAYLOAD)
                        .installationInspection(expectedInstallationInspection)
                        .installationInspectionSectionsCompleted(expectedInstallationInspectionSectionsCompleted)
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getInstallationInspection(), expectedTaskActionPayload.getInstallationInspection());
        assertEquals(expectedTaskPayload.getInstallationInspectionSectionsCompleted(), expectedTaskActionPayload.getInstallationInspectionSectionsCompleted());
    }

    @Test
    void cancel() {
        final String requestId = "1";
        final String regulatorAssignee = "regulator";

        final Request request = Request.builder()
                .id(requestId)
                .payload(InstallationOnsiteInspectionRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.cancel(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, null, RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_CANCELLED, regulatorAssignee);
    }

    @Test
    void requestPeerReview() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId).build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final InstallationInspection installationInspection = InstallationInspection.builder().build();

        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        Request request = Request.builder()
                .payload(InstallationOnsiteInspectionRequestPayload.builder()
                        .payloadType(RequestPayloadType.INSTALLATION_ONSITE_INSPECTION_REQUEST_PAYLOAD)
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_PAYLOAD)
                        .installationInspection(installationInspection)
                        .installationInspectionSectionsCompleted(sectionsCompleted)
                        .inspectionAttachments(attachments)
                        .build())
                .build();

        final InstallationOnsiteInspectionRequestPayload expectedPayload = InstallationOnsiteInspectionRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ONSITE_INSPECTION_REQUEST_PAYLOAD)
                .installationInspection(installationInspection)
                .installationInspectionSectionsCompleted(sectionsCompleted)
                .inspectionAttachments(attachments)
                .regulatorPeerReviewer(selectedPeerReviewer)
                .regulatorReviewer(user.getUserId())
                .build();

        // Invoke
        service.requestPeerReview(requestTask, selectedPeerReviewer, user);

        // Verify
        InstallationOnsiteInspectionRequestPayload updatedPayload = (InstallationOnsiteInspectionRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
    }


    @Test
    void applySubmitNotify(){
        UUID att1 = UUID.randomUUID();
        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        InstallationInspection installationInspection =
                InstallationInspection
                .builder()
                .details(
                     InstallationInspectionDetails
                        .builder()
                        .date(LocalDate.now())
                        .officerNames(List.of("officer2"))
                        .build())
                .build();

        InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload taskPayload = InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_PAYLOAD)
                .installationInspection(installationInspection)
                .inspectionAttachments(Map.of(att1, "atta1.pdf"))

                .installationInspectionSectionsCompleted(sectionsCompleted)
                .build();
        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(requestPayload)
                        .build())
                .payload(taskPayload).build();

        Set<String> operators = Set.of("oper");
        String signatory = "sign";
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(signatory)
                .operators(operators)
                .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser))
                .thenReturn(true);

        // Invoke
        service.applySubmitNotify(requestTask, decisionNotification, appUser);

        verify(installationOnsiteInspectionValidatorService, times(1)).validateInstallationInspection(installationInspection);
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, appUser);
        assertThat(requestPayload.getInstallationInspection()).isEqualTo(installationInspection);
        assertThat(requestPayload.getInstallationInspectionSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(requestPayload.getInspectionAttachments()).containsExactlyEntriesOf(Map.of(att1, "atta1.pdf"));
        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
    }
}
