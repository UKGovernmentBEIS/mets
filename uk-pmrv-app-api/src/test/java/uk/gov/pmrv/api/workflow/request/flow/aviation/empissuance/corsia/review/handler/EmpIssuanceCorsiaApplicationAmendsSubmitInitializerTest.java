package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaApplicationAmendsSubmitInitializerTest {

	@InjectMocks
    private EmpIssuanceCorsiaApplicationAmendsSubmitInitializer initializer;

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
    	
    	UUID uuid = UUID.randomUUID();
    	Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpCorsiaReviewGroup.class);
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpIssuanceReviewDecision.builder()
                .type(EmpReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision.builder()
                .type(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(ChangesRequiredDecisionDetails.builder().requiredChanges(List.of(
                        ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(uuid)).build())).build())
                .build());       
    	
        EmpIssuanceCorsiaRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .reviewAttachments(Map.of(uuid, "test"))
                .reviewGroupDecisions(reviewGroupDecisions)
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

        assertThat(requestTaskPayload).isInstanceOf(EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload.class);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD);
        assertThat(((EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getReviewAttachments()).hasSize(1);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT);
    }
}
