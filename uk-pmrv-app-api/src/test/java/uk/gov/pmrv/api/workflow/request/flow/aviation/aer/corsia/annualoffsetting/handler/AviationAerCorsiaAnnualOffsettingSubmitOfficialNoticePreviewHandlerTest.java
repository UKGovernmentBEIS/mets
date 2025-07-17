package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsiaAnnualOffsettingSubmitOfficialNoticePreviewHandlerTest {


    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingSubmitOfficialNoticePreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationAerCorsiaAnnualOffsettingOfficialNoticeService previewOfficialNoticeService;


    @Test
    void generateDocument() {

        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();

        final Map<String, Boolean> aviationAerCorsiaAnnualOffsettingSectionsCompleted = new HashMap<>();
        aviationAerCorsiaAnnualOffsettingSectionsCompleted.put("aviationAerCorsiaAnnualOffsetting",false);
        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = AviationAerCorsiaAnnualOffsetting
                .builder()
                .calculatedAnnualOffsetting(6)
                .schemeYear(Year.of(2023))
                .sectorGrowth(2.9)
                .totalChapter(1)
                .build();

        AviationAerCorsiaAnnualOffsettingRequestPayload aviationAerCorsiaAnnualOffsettingRequestPayload = AviationAerCorsiaAnnualOffsettingRequestPayload
                .builder()
                .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_REQUEST_PAYLOAD)
                .aviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting)
                .aviationAerCorsiaAnnualOffsettingSectionsCompleted(aviationAerCorsiaAnnualOffsettingSectionsCompleted)
                .decisionNotification(decisionNotification)
                .build();

        AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload taskPayload = AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .aviationAerCorsiaAnnualOffsettingSectionsCompleted(aviationAerCorsiaAnnualOffsettingSectionsCompleted)
                .aviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting)
                .decisionNotification(decisionNotification)
                .build();

        final Request request = Request.builder()
                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                .id("1")
                .payload(aviationAerCorsiaAnnualOffsettingRequestPayload)
                .build();


        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        final TemplateParams templateParams = TemplateParams.builder()
                .accountParams(InstallationAccountTemplateParams.builder().build())
                .build();

        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();
        final Map<String, Object> params = Map.of();


        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.doGenerateOfficialNoticeWithoutSave(request, decisionNotification)).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(params);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).doGenerateOfficialNoticeWithoutSave(
                request,
                decisionNotification);
    }


    @Test
    void getTypes(){
          assertThat(handler.getTypes()).containsExactly(DocumentTemplateType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED);
    }

    @Test
    void getTaskTypes(){
        assertThat(handler.getTaskTypes()).containsExactlyInAnyOrder(
                RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT,
                RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW
        );
    }
}
