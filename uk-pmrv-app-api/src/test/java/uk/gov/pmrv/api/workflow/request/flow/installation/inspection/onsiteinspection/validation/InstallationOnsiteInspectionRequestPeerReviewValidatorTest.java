package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.FollowUpAction;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.FollowUpActionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class InstallationOnsiteInspectionRequestPeerReviewValidatorTest {

    @InjectMocks
    private InstallationOnsiteInspectionRequestPeerReviewValidator validator;

    @Mock
    private InstallationOnsiteInspectionValidatorService installationOnsiteInspectionValidatorService;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;


    @Test
    void validate(){
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

        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT)
                .payload(InstallationInspectionApplicationSubmitRequestTaskPayload.builder()
                        .installationInspection(installationInspection)
                        .build())
                .build();

        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer("reviewer")
                .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        validator.validate(requestTask, taskActionPayload, appUser);

        verify(peerReviewerTaskAssignmentValidator, times(1)).validate(RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW, "reviewer", appUser);
        verify(installationOnsiteInspectionValidatorService, times(1)).validateInstallationInspection(installationInspection);
    }

    @Test
    void getRequestType(){
        assertThat(validator.getRequestType()).isEqualTo(RequestType.INSTALLATION_ONSITE_INSPECTION);
    }
}
