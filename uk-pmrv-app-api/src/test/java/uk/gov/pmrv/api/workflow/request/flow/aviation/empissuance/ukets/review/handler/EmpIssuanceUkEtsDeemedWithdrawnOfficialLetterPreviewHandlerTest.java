package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsDeemedWithdrawnOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private EmpIssuanceUkEtsDeemedWithdrawnOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void initializePayload() {

        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().build();
        final String operatorName = "name";
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                    .operatorDetails(EmpOperatorDetails.builder().operatorName(operatorName).build())
                    .build())
                .build())
            .build();
        final TemplateParams templateParams = TemplateParams.builder()
            .accountParams(AviationAccountTemplateParams.builder().build())
            .build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParamsWithoutAccountNameLocation(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.EMP_ISSUANCE_UKETS_DEEMED_WITHDRAWN,
            templateParams,
            "emp_application_withdrawn.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertEquals(templateParams.getAccountParams().getName(), operatorName);


        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.EMP_ISSUANCE_UKETS_DEEMED_WITHDRAWN,
            templateParams,
            "emp_application_withdrawn.pdf");
    }

    @Test
    void getOperatorNameFromRequestTaskPayload( ){

        final String expectedOperatorName = "operatorName";
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().operatorDetails(EmpOperatorDetails.builder().operatorName(expectedOperatorName).build()).build())
                .build();

        final String actualOperatorName = handler.getOperatorNameFromRequestTaskPayload(payload);

        assertEquals(expectedOperatorName,actualOperatorName);
    }

    @Test
    void getTypes() {
        assertIterableEquals(List.of(DocumentTemplateType.EMP_ISSUANCE_UKETS_DEEMED_WITHDRAWN), handler.getTypes());
    }

    @Test
    void getTaskTypes() {
        assertIterableEquals(List.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW,
                RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW), handler.getTaskTypes());
    }


}
