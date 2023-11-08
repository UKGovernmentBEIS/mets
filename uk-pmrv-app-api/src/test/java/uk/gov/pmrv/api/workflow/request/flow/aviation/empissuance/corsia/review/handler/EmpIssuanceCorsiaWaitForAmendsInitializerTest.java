package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaWaitForAmendsInitializerTest {

	@InjectMocks
    private EmpIssuanceCorsiaWaitForAmendsInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void initializePayload() {
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName("name")
                .build())
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .build();
        EmpIssuanceCorsiaRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .build();
        Request request = Request.builder()
            .type(RequestType.EMP_ISSUANCE_CORSIA)
            .payload(empIssuanceUkEtsRequestPayload)
            .build();
        String operatorName = "name";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .build();

        when(requestAviationAccountQueryService.getAccountInfo(request.getAccountId())).thenReturn(aviationAccountInfo);

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS);
    }

    @Test
    void getRequestTaskPayloadType() {
        assertEquals(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS_PAYLOAD, initializer.getRequestTaskPayloadType());
    }
}
