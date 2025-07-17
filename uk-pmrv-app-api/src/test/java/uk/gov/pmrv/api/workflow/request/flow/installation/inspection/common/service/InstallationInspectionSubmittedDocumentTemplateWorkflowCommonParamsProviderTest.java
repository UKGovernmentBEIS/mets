package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.FollowUpAction;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.FollowUpActionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProviderTest {

    @InjectMocks
    private InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider provider;


    @Test
    void constructParams() {
        final InstallationInspection installationInspection = InstallationInspection
                .builder()
                .details(InstallationInspectionDetails
                        .builder()
                        .additionalInformation("Test additional information")
                        .build())
                .followUpActionsRequired(true)
                .followUpActionsOmissionJustification("Test justification")
                .followUpActions(List.of(FollowUpAction.builder().followUpActionType(FollowUpActionType.MISSTATEMENT).explanation("Test").build(),
                        FollowUpAction.builder().followUpActionType(FollowUpActionType.NON_COMPLIANCE).explanation("Test 1").build()))
                .responseDeadline(LocalDate.of(2025, 9, 10))
                .build();

        Map<String, Object> expectedParams = Map.of("additionalInformation", "Test additional information",
                "followupActions", List.of(FollowUpAction.builder().followUpActionType(FollowUpActionType.MISSTATEMENT).explanation("Test").build(),
                        FollowUpAction.builder().followUpActionType(FollowUpActionType.NON_COMPLIANCE).explanation("Test 1").build()),
                "responseDeadline","10 September 2025",
               "followUpActionsRequired", true,
                "followUpActionsOmissionJustification", "Test justification");

        Map<String, Object> actualParams = provider.constructParams(installationInspection);

        assertThat(actualParams)
                .containsAllEntriesOf(expectedParams);
    }

    @Test
    void constructParams_responseDeadlineEmpty_blankResponseDeadline() {
        final InstallationInspection installationInspection = InstallationInspection
                .builder()
                .details(InstallationInspectionDetails
                        .builder()
                        .additionalInformation("Test additional information")
                        .build())
                .followUpActions(List.of(FollowUpAction.builder().followUpActionType(FollowUpActionType.MISSTATEMENT).explanation("Test").build(),
                        FollowUpAction.builder().followUpActionType(FollowUpActionType.NON_COMPLIANCE).explanation("Test 1").build()))
                .build();

        Map<String, Object> expectedParams = Map.of("additionalInformation", "Test additional information",
                "followupActions", List.of(FollowUpAction.builder().followUpActionType(FollowUpActionType.MISSTATEMENT).explanation("Test").build(),
                        FollowUpAction.builder().followUpActionType(FollowUpActionType.NON_COMPLIANCE).explanation("Test 1").build()),
                "responseDeadline","");


        Map<String, Object> actualParams = provider.constructParams(installationInspection);

        assertThat(actualParams)
                .containsAllEntriesOf(expectedParams);
    }
}
