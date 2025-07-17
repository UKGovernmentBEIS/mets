package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalProceededToAuthorityOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private DoalProceededToAuthorityOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void generateDocument() {

        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().build();
        final ActivityLevel activityLevel1 = ActivityLevel.builder()
                .subInstallationName(SubInstallationName.COATED_CARBON_BOARD)
                .year(Year.of(2023))
                .changeType(ChangeType.INCREASE)
                .comments("comments")
                .build();
        final ActivityLevel activityLevel2 = ActivityLevel.builder()
                .subInstallationName(SubInstallationName.ALUMINIUM)
                .year(Year.of(2023))
                .changeType(ChangeType.DECREASE)
                .comments("comments")
                .build();
        List<ActivityLevel> activityLevels = new ArrayList<>();
        activityLevels.add(activityLevel1);
        activityLevels.add(activityLevel2);
        final PreliminaryAllocation preliminaryAllocation1 = PreliminaryAllocation.builder()
                .subInstallationName(SubInstallationName.AROMATICS)
                .year(Year.of(2023))
                .allowances(100)
                .build();
        final PreliminaryAllocation preliminaryAllocation2 = PreliminaryAllocation.builder()
                .subInstallationName(SubInstallationName.COATED_FINE_PAPER)
                .year(Year.of(2023))
                .allowances(200)
                .build();
        SortedSet<PreliminaryAllocation> preliminaryAllocations = new TreeSet<>();
        preliminaryAllocations.add(preliminaryAllocation1);
        preliminaryAllocations.add(preliminaryAllocation2);
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder()
                                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .activityLevels(activityLevels)
                                        .preliminaryAllocations(preliminaryAllocations)
                                        .build())
                                .build())
                        .build())
                .build();
        final TemplateParams templateParams = TemplateParams.builder()
                .accountParams(InstallationAccountTemplateParams.builder().build())
                .build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();
        final Map<String, Object> params = Map.of(
                "activityLevels", activityLevels,
                "allocations",preliminaryAllocations,
                "allocationsPerYear", Map.of(Year.of(2023), 300)
        );

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.DOAL_SUBMITTED,
                templateParams,
                "Activity_level_determination_preliminary_allocation_letter.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(params);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.DOAL_SUBMITTED,
                templateParams,
                "Activity_level_determination_preliminary_allocation_letter.pdf");
    }
}
