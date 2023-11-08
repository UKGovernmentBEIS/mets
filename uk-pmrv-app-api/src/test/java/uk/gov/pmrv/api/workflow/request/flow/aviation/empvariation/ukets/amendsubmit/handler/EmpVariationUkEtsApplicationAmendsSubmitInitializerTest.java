package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsApplicationAmendsSubmitInitializerTest {

	@InjectMocks
    private EmpVariationUkEtsApplicationAmendsSubmitInitializer initializer;

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
    	
    	UUID uuid = UUID.randomUUID();
    	Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.REJECTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build());
        reviewGroupDecisions.put(EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .details(ChangesRequiredDecisionDetails.builder().requiredChanges(List.of(
                        ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(uuid)).build())).build())
                .build());       
    	
        String reason = "reason";
        EmpVariationReviewDecision detailsReviewDecision = EmpVariationReviewDecision.builder()
        		.type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
        		.details(ChangesRequiredDecisionDetails.builder().requiredChanges(List.of(
                        ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(uuid)).build())).build())
        		.build();
        EmpVariationUkEtsRequestPayload empVariationUkEtsRequestPayload = EmpVariationUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .empVariationDetails(EmpVariationUkEtsDetails.builder().reason(reason).build())
                .empVariationDetailsReviewDecision(detailsReviewDecision)
                .reviewAttachments(Map.of(uuid, "test"))
                .reviewGroupDecisions(reviewGroupDecisions)
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

        assertThat(requestTaskPayload).isInstanceOf(EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload.class);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD);
        assertThat(((EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getEmpVariationDetailsReviewDecision()).isEqualTo(detailsReviewDecision);
        assertThat(((EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getEmpVariationDetails().getReason()).isEqualTo(reason);
        assertThat(((EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getReviewAttachments()).hasSize(1);
        assertThat(((EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getReviewGroupDecisions()).hasSize(1);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT);
    }
}
