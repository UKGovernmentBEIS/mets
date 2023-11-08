package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaWaitForAmendsInitializerTest {

	@InjectMocks
    private EmpVariationCorsiaWaitForAmendsInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void initializePayload() {
    	EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .operatorName("name")
                    .build())
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .build();
    	
        EmpVariationCorsiaRequestPayload empVariationUkEtsRequestPayload = EmpVariationCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.EMP_VARIATION_CORSIA_REQUEST_PAYLOAD)
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .build();
    	Request request = Request.builder()
                .type(RequestType.EMP_VARIATION_CORSIA)
                .payload(empVariationUkEtsRequestPayload)
                .build();
    	String operatorName = "name";
    	RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
                .operatorName(operatorName)
                .build();
        
        when(requestAviationAccountQueryService.getAccountInfo(request.getAccountId())).thenReturn(aviationAccountInfo);

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload).isInstanceOf(EmpVariationCorsiaApplicationReviewRequestTaskPayload.class);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS_PAYLOAD);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS);
    }
}
