package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.FollowUpActionType;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class InstallationInspectionMapperTest {


    private final InstallationInspectionMapper mapper = Mappers.getMapper(InstallationInspectionMapper.class);

    @Test
    void toSubmittedActionPayload() {
        UUID attachment1 = UUID.randomUUID();
        FollowUpAction followUpAction = FollowUpAction
                .builder()
                .followUpActionType(FollowUpActionType.NON_COMPLIANCE)
                .explanation("Dummy explanation")
                .followUpActionAttachments(Set.of(attachment1))
                .build();

        final Map<String, Boolean> installationInspectionSectionsCompleted = new HashMap<>();
        installationInspectionSectionsCompleted.put("followUpActions",false);


        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder()
                .installationInspection(InstallationInspection.builder()
                        .responseDeadline(LocalDate.now().plusDays(1))
                        .followUpActions(List.of(followUpAction))
                        .build())
                .installationInspectionSectionsCompleted(installationInspectionSectionsCompleted)
                .inspectionAttachments(Map.of(attachment1, "att1.pdf"))
                .decisionNotification(DecisionNotification.builder()
                        .signatory("signatory")
                        .build())
                .build();

        InstallationInspectionApplicationSubmittedRequestActionPayload actionPayload = mapper.toSubmittedActionPayload(requestPayload);

        assertThat(actionPayload.getPayloadType()).isEqualTo(RequestActionPayloadType.INSTALLATION_INSPECTION_APPLICATION_SUBMITTED_PAYLOAD);
        assertThat(actionPayload.getAttachments()).containsAllEntriesOf(requestPayload.getInspectionAttachments());
        assertThat(actionPayload.getDecisionNotification()).isEqualTo(requestPayload.getDecisionNotification());
        assertThat(actionPayload.getInstallationInspection()).isEqualTo(requestPayload.getInstallationInspection());
    }

    @Test
    void toInstallationInspectionApplicationSubmitRequestTaskPayload() {
        UUID attachment1 = UUID.randomUUID();
        FollowUpAction followUpAction = FollowUpAction
                .builder()
                .followUpActionType(FollowUpActionType.NON_COMPLIANCE)
                .explanation("Dummy explanation")
                .followUpActionAttachments(Set.of(attachment1))
                .build();

        InstallationInspection installationInspection = InstallationInspection.builder()
                .responseDeadline(LocalDate.now().plusDays(1))
                .followUpActions(List.of(followUpAction))
                .build();


        Map<UUID, String> attachments = new HashMap<>();
        attachments.put(attachment1, "att1");

        final Map<String, Boolean> installationInspectionSectionsCompleted = new HashMap<>();
        installationInspectionSectionsCompleted.put("followUpActions",false);

        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder()
                .installationInspection(installationInspection)
                .inspectionAttachments(attachments)
                .installationInspectionSectionsCompleted(installationInspectionSectionsCompleted)
                .payloadType(RequestPayloadType.INSTALLATION_AUDIT_REQUEST_PAYLOAD)
                .build();

        InstallationInspectionApplicationSubmitRequestTaskPayload result = mapper.toInstallationInspectionApplicationSubmitRequestTaskPayload(requestPayload, RequestTaskPayloadType.INSTALLATION_AUDIT_APPLICATION_SUBMIT_PAYLOAD);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.INSTALLATION_AUDIT_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(result.getInspectionAttachments()).containsExactlyEntriesOf(attachments);
        assertThat(result.getInstallationInspection()).isEqualTo(installationInspection);
        assertThat(result.getInstallationInspectionSectionsCompleted()).isEqualTo(installationInspectionSectionsCompleted);
    }

    @Test
    void toInstallationInspectionOperatorRespondedRequestActionPayload(){
        final UUID attachment1 = UUID.randomUUID();
        final UUID attachment2 = UUID.randomUUID();

        final FollowUpAction followUpAction = FollowUpAction
                .builder()
                .followUpActionType(FollowUpActionType.NON_COMPLIANCE)
                .explanation("Dummy explanation")
                .followUpActionAttachments(Set.of(attachment1))
                .build();

        final Set<UUID> followUpActionResponseAttachments = new HashSet<>();
        followUpActionResponseAttachments.add(attachment2);

        final FollowUpActionResponse followUpActionResponse = FollowUpActionResponse
                .builder()
                .followUpActionResponseAttachments(followUpActionResponseAttachments)
                .completed(true)
                .explanation("Test")
                .completionDate(LocalDate.now().minusDays(2))
                .build();

        final InstallationInspection installationInspection = InstallationInspection.builder()
                .responseDeadline(LocalDate.now().plusDays(1))
                .followUpActions(List.of(followUpAction))
                .build();


        final Map<UUID, String> inspectionAttachments = new HashMap<>();
        inspectionAttachments.put(attachment1, "att1");

        final Map<String, Boolean> installationInspectionOperatorRespondSectionsCompleted = new HashMap<>();

        InstallationInspectionOperatorRespondRequestTaskPayload requestTaskPayload = InstallationInspectionOperatorRespondRequestTaskPayload.builder()
                .installationInspection(installationInspection)
                .inspectionAttachments(inspectionAttachments)
                .installationInspectionOperatorRespondSectionsCompleted(installationInspectionOperatorRespondSectionsCompleted)
                .followupActionsResponses(Map.of(0,followUpActionResponse))
                .payloadType(RequestTaskPayloadType.INSTALLATION_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS_PAYLOAD)
                .build();

        InstallationInspectionOperatorRespondedRequestActionPayload result = mapper
                 .toInstallationInspectionOperatorRespondedRequestActionPayload(requestTaskPayload);

        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.INSTALLATION_INSPECTION_OPERATOR_RESPONDED_PAYLOAD);
        assertThat(result.getInspectionAttachments()).containsExactlyEntriesOf(inspectionAttachments);
        assertThat(result.getInstallationInspection()).isEqualTo(installationInspection);
        assertThat(result.getFollowupActionsResponses()).containsExactlyEntriesOf(Map.of(0,followUpActionResponse));
    }
}
