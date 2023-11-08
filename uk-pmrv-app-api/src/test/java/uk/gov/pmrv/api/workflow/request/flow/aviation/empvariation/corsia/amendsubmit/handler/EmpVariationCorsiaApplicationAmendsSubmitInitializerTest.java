package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaApplicationAmendsSubmitInitializerTest {

    @InjectMocks
    private EmpVariationCorsiaApplicationAmendsSubmitInitializer initializer;

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

        UUID uuid = UUID.randomUUID();
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions =
            new EnumMap<>(EmpCorsiaReviewGroup.class);
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
            EmpVariationReviewDecision.builder()
                .type(EmpVariationReviewDecisionType.REJECTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build());
        reviewGroupDecisions.put(EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision.builder()
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
        EmpVariationCorsiaRequestPayload empVariationUkEtsRequestPayload = EmpVariationCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empVariationDetails(EmpVariationCorsiaDetails.builder().reason(reason).build())
            .empVariationDetailsReviewDecision(detailsReviewDecision)
            .reviewAttachments(Map.of(uuid, "test"))
            .reviewGroupDecisions(reviewGroupDecisions)
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

        assertThat(requestTaskPayload).isInstanceOf(EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload.class);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(
            RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD);
        assertThat(
            ((EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getEmpVariationDetailsReviewDecision()).isEqualTo(
            detailsReviewDecision);
        assertThat(
            ((EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getEmpVariationDetails()
                .getReason()).isEqualTo(reason);
        assertThat(
            ((EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getReviewAttachments()).hasSize(
            1);
        assertThat(
            ((EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTaskPayload).getReviewGroupDecisions()).hasSize(
            1);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(
            RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT);
    }
}
