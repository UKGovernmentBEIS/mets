package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsWaitForAmendsInitializerTest {

	
	@InjectMocks
    private EmpVariationUkEtsWaitForAmendsInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void initializePayload() {
    	EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(EmpOperatorDetails.builder()
                    .operatorName("name")
                    .crcoCode("code")
                    .build())
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .build();
    	
        EmpVariationUkEtsRequestPayload empVariationUkEtsRequestPayload = EmpVariationUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.EMP_VARIATION_UKETS_REQUEST_PAYLOAD)
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .build();
    	Request request = Request.builder()
                .type(RequestType.EMP_VARIATION_UKETS)
                .payload(empVariationUkEtsRequestPayload)
                .build();
    	String operatorName = "name";
        String crcoCode = "code";
    	RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
                .operatorName(operatorName)
                .crcoCode(crcoCode)
                .build();
        
        when(requestAviationAccountQueryService.getAccountInfo(request.getAccountId())).thenReturn(aviationAccountInfo);

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload).isInstanceOf(EmpVariationUkEtsApplicationReviewRequestTaskPayload.class);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_VARIATION_UKETS_WAIT_FOR_AMENDS_PAYLOAD);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_VARIATION_UKETS_WAIT_FOR_AMENDS);
    }
}
