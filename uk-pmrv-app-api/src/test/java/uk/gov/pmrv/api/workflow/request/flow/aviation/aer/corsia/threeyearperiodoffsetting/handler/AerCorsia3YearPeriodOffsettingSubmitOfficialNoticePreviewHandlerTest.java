package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AerCorsia3YearPeriodOffsettingSubmitOfficialNoticePreviewHandlerTest {

    @InjectMocks
    private AerCorsia3YearPeriodOffsettingSubmitOfficialNoticePreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationAerCorsia3YearPeriodOffsettingOfficialNoticeService previewOfficialNoticeService;

    @Test
    void getTypes(){
        assertThat(handler.getTypes()).containsExactlyInAnyOrder(DocumentTemplateType.AVIATION_AER_CORSIA_3_YEAR_PERIOD_OFFSETTING_SUBMITTED);
    }

    @Test
    void getTaskTypes(){
         assertThat(handler.getTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT,RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW);
    }

    @Test
    void generateDocument(){
        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting = AviationAerCorsia3YearPeriodOffsetting
                .builder()
                .schemeYears(List.of(Year.of(2021),Year.of(2022),Year.of(2023)))
                .build();

        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload taskPayload = AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload
                .builder()
                .decisionNotification(decisionNotification)
                .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                .build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload = AviationAerCorsia3YearPeriodOffsettingRequestPayload
                .builder()
                .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                .decisionNotification(decisionNotification)
                .build();


        final Request request = Request.builder()
                .type(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING)
                .payload(requestPayload)
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
        verify(previewOfficialNoticeService, times(1)).doGenerateOfficialNoticeWithoutSave(request, decisionNotification);
    }
}
