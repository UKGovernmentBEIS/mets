package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

import java.time.Year;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsiaAnnualOffsettingAddSubmittedRequestActionServiceTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingAddSubmittedRequestActionService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;


    @Test
    void add() {

        String requestId = "1";
        Set<String> operators = Set.of("oper");
        String signatory = "sign";
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(signatory)
                .operators(operators)
                .build();

        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting =
                AviationAerCorsiaAnnualOffsetting
                        .builder()
                        .calculatedAnnualOffsetting(6)
                        .schemeYear(Year.of(2023))
                        .sectorGrowth(2.0)
                        .totalChapter(12)
                        .build();


        final Request request = Request
                .builder()
                .id(requestId)
                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload.builder()
                        .aviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting)
                        .decisionNotification(decisionNotification)
                        .regulatorAssignee("regulator").build())
                .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of("oper", RequestActionUserInfo.builder().name("operator1").build());


        AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload actionPayload =
                AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload
                        .builder()
                        .aviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting)
                        .decisionNotification(decisionNotification)
                        .usersInfo(usersInfo)
                        .payloadType(RequestActionPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED_PAYLOAD)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(operators, signatory, request))
                .thenReturn(usersInfo);


        service.add("1");

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1)).getUsersInfo(operators, signatory, request);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED, "regulator");
    }


}
