package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.handler;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler.EmpIssuanceCorsiaDeemedWithdrawnOfficialLetterPreviewHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler.EmpIssuanceCorsiaGrantedOfficialLetterPreviewHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler.EmpIssuanceUkEtsDeemedWithdrawnOfficialLetterPreviewHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler.EmpIssuanceUkEtsGrantedOfficialLetterPreviewHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmpIssuanceOfficialLetterPreviewHandlerTest {


    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;


    private static Stream<Arguments> provideConstructTemplateParamsTestArguments() {

        List<Arguments> arguments = List.of(
                Arguments.of(EmpIssuanceUkEtsDeemedWithdrawnOfficialLetterPreviewHandler.class,EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                        .build()),
                Arguments.of(EmpIssuanceUkEtsGrantedOfficialLetterPreviewHandler.class,EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                        .build()),
                Arguments.of(EmpIssuanceCorsiaDeemedWithdrawnOfficialLetterPreviewHandler.class, EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
                        .build()),
                Arguments.of(EmpIssuanceCorsiaGrantedOfficialLetterPreviewHandler.class, EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder()
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
                        .build())
        );

        return arguments.stream();
    }


    @ParameterizedTest
    @MethodSource("provideConstructTemplateParamsTestArguments")
    void constructTemplateParams(Class<EmpIssuanceOfficialLetterPreviewHandler> handlerClass, EmpIssuanceApplicationRequestTaskPayload payload){
        final EmpIssuanceOfficialLetterPreviewHandler handler = mock(handlerClass,Mockito.withSettings().useConstructor(requestTaskService,previewOfficialNoticeService,documentFileGeneratorService));
        final long taskId = 2L;
        final String operatorName = "operatorName";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(payload)
                .build();

        final TemplateParams expectedTemplateParams = TemplateParams.builder()
                .accountParams(AviationAccountTemplateParams.builder().name(operatorName).build())
                .build();

        when(handler.getOperatorNameFromRequestTaskPayload(payload)).thenReturn(operatorName);
        when(handler.constructTemplateParams(taskId,decisionNotification)).thenCallRealMethod();
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParamsWithoutAccountNameLocation(request, decisionNotification)).thenReturn(TemplateParams.builder()
                .accountParams(AviationAccountTemplateParams.builder().build())
                .build());

        final TemplateParams actualTemplateParams = handler.constructTemplateParams(taskId,decisionNotification);

        assertEquals(expectedTemplateParams,actualTemplateParams);
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParamsWithoutAccountNameLocation(request, decisionNotification);
    }

}
