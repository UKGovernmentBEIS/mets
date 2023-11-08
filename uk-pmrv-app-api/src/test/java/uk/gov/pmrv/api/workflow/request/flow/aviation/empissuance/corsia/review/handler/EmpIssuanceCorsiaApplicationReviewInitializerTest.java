package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
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
class EmpIssuanceCorsiaApplicationReviewInitializerTest {

    @InjectMocks
    private EmpIssuanceCorsiaApplicationReviewInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void initializePayload() {
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .emissionsMonitoringApproach(FuelMonitoringApproach.builder().build())
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName("name")
                .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                    .operatorType(OperatorType.COMMERCIAL)
                    .flightTypes(Set.of(FlightType.SCHEDULED))
                    .activityDescription("activity description")
                    .build())
                .build())
            .build();
        Map<UUID, String> empAttachments = Map.of(
            UUID.randomUUID(), "attachment"
        );
        Map<String, List<Boolean>> empSectionsCompleted = Map.of(
            "abbreviations", List.of(true),
            "operatorDetails", List.of(true),
            "monitoringApproach", List.of(true),
            "managementProcedures", List.of(true)
        );
        EmpIssuanceCorsiaRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empSectionsCompleted(empSectionsCompleted)
            .empAttachments(empAttachments)
            .build();

        Request request = Request.builder()
            .type(RequestType.EMP_ISSUANCE_CORSIA)
            .payload(empIssuanceUkEtsRequestPayload)
            .accountId(20L)
            .build();
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .email("email")
            .roleCode("role")
            .name("name")
            .build();
        String operatorName = "operator name";
        RequestAviationAccountInfo aviationAccountInfo = RequestAviationAccountInfo.builder()
            .operatorName(operatorName)
            .serviceContactDetails(serviceContactDetails)
            .build();

        when(requestAviationAccountQueryService.getAccountInfo(request.getAccountId())).thenReturn(aviationAccountInfo);

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.class);

        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload empIssuanceCorsiaApplicationReviewRequestTaskPayload =
            (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTaskPayload;

        EmissionsMonitoringPlanCorsia reviewRequestTaskPayloadEmpObject = empIssuanceCorsiaApplicationReviewRequestTaskPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), reviewRequestTaskPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails(), reviewRequestTaskPayloadEmpObject.getOperatorDetails());
        assertEquals(emissionsMonitoringPlan.getEmissionsMonitoringApproach(), reviewRequestTaskPayloadEmpObject.getEmissionsMonitoringApproach());
        assertEquals(emissionsMonitoringPlan.getManagementProcedures(), reviewRequestTaskPayloadEmpObject.getManagementProcedures());

        EmpCorsiaOperatorDetails reviewRequestTaskPayloadEmpObjectOperatorDetails = reviewRequestTaskPayloadEmpObject.getOperatorDetails();

        assertNotNull(reviewRequestTaskPayloadEmpObjectOperatorDetails);
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), reviewRequestTaskPayloadEmpObjectOperatorDetails.getOperatorName());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), reviewRequestTaskPayloadEmpObjectOperatorDetails.getActivitiesDescription());

        assertThat(empIssuanceCorsiaApplicationReviewRequestTaskPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(empIssuanceCorsiaApplicationReviewRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertEquals(aviationAccountInfo.getServiceContactDetails(), empIssuanceCorsiaApplicationReviewRequestTaskPayload.getServiceContactDetails());
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW));
    }

    @Test
    void getRequestTaskPayloadType() {
        assertEquals(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD, initializer.getRequestTaskPayloadType());
    }
}