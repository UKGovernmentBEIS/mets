package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsApplicationPeerReviewInitializerTest {

    @InjectMocks
    private EmpIssuanceUkEtsApplicationPeerReviewInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void initializePayload() {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName("name")
                .crcoCode("code")
                .build())
            .build();
        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .build();
        Request request = Request.builder()
            .type(RequestType.EMP_ISSUANCE_UKETS)
            .payload(empIssuanceUkEtsRequestPayload)
            .build();
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .email("email")
            .roleCode("role")
            .name("name")
            .build();
        String operatorName = "operator name";
        String crcoCode = "crco code";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .crcoCode(crcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();

        when(requestAviationAccountQueryService.getAccountInfo(request.getAccountId())).thenReturn(aviationAccountInfo);

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW);
    }

    @Test
    void getRequestTaskPayloadType() {
        assertEquals(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW_PAYLOAD, initializer.getRequestTaskPayloadType());
    }
}