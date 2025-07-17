package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsiaAnnualOffsettingRequestPeerReviewValidatorTest {

    @InjectMocks
    private  AviationAerCorsiaAnnualOffsettingRequestPeerReviewValidator validator;

    @Mock
    private AviationAerCorsiaAnnualOffsettingValidatorService installationInspectionValidatorService;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;


    @Test
    void validate_installationInspectionIsValid_shouldPassValidation() {

//        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = AviationAerCorsiaAnnualOffsetting.builder()
//                .schemeYear(LocalDate.now().plusDays(1).getYear())
//                .calculatedAnnualOffsetting(3.2)
//                .totalChapter(1.2)
//                .sectorGrowth(2.3)
//                .build();
//
//        RequestTask requestTask = RequestTask.builder()
//                .type(RequestTaskType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW)
//                .payload(InstallationInspectionApplicationSubmitRequestTaskPayload.builder()
//                        .installationInspection(installationInspection)
//                        .build())
//                .build();
//
//        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
//                .peerReviewer("reviewer")
//                .build();
//
//        AppUser appUser = AppUser.builder().userId("user").build();
//
//        validator.validate(requestTask, taskActionPayload, appUser);
//
//        verify(peerReviewerTaskAssignmentValidator, times(1)).validate(RequestTaskType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW, "reviewer", appUser);
//        verify(installationInspectionValidatorService, times(1)).validateInstallationInspection(installationInspection);
    }

}
