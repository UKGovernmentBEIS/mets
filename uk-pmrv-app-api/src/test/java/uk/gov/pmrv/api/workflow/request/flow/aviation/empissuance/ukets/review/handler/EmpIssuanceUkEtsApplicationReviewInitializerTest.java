package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsApplicationReviewInitializerTest {

    @InjectMocks
    private EmpIssuanceUkEtsApplicationReviewInitializer initializer;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void initializePayload() {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .abbreviations(EmpAbbreviations.builder().exist(false).build())
            .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder().dateOfStart(LocalDate.now()).submittedOnTime(Boolean.TRUE).build())
            .emissionsMonitoringApproach(FuelMonitoringApproach.builder().build())
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName("name")
                .crcoCode("code")
                .activitiesDescription(ActivitiesDescription.builder()
                    .operatorType(OperatorType.COMMERCIAL)
                    .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
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
            "applicationTimeframeInfo", List.of(true),
            "emissionsMonitoringApproach", List.of(true)
        );
        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
            .empSectionsCompleted(empSectionsCompleted)
            .empAttachments(empAttachments)
            .build();

        Request request = Request.builder()
            .type(RequestType.EMP_ISSUANCE_UKETS)
            .payload(empIssuanceUkEtsRequestPayload)
            .accountId(20L)
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

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.class);

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload empIssuanceUkEtsApplicationReviewRequestTaskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTaskPayload;

        EmissionsMonitoringPlanUkEts reviewRequestTaskPayloadEmpObject = empIssuanceUkEtsApplicationReviewRequestTaskPayload.getEmissionsMonitoringPlan();

        assertEquals(emissionsMonitoringPlan.getAbbreviations(), reviewRequestTaskPayloadEmpObject.getAbbreviations());
        assertEquals(emissionsMonitoringPlan.getApplicationTimeframeInfo(), reviewRequestTaskPayloadEmpObject.getApplicationTimeframeInfo());
        assertEquals(emissionsMonitoringPlan.getEmissionsMonitoringApproach(), reviewRequestTaskPayloadEmpObject.getEmissionsMonitoringApproach());

        EmpOperatorDetails reviewRequestTaskPayloadEmpObjectOperatorDetails = reviewRequestTaskPayloadEmpObject.getOperatorDetails();

        assertNotNull(reviewRequestTaskPayloadEmpObjectOperatorDetails);
        assertEquals(aviationAccountInfo.getCrcoCode(), reviewRequestTaskPayloadEmpObjectOperatorDetails.getCrcoCode());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getOperatorName(), reviewRequestTaskPayloadEmpObjectOperatorDetails.getOperatorName());
        assertEquals(emissionsMonitoringPlan.getOperatorDetails().getActivitiesDescription(), reviewRequestTaskPayloadEmpObjectOperatorDetails.getActivitiesDescription());

        assertThat(empIssuanceUkEtsApplicationReviewRequestTaskPayload.getEmpAttachments()).containsExactlyInAnyOrderEntriesOf(empAttachments);
        assertThat(empIssuanceUkEtsApplicationReviewRequestTaskPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertEquals(aviationAccountInfo.getServiceContactDetails(), empIssuanceUkEtsApplicationReviewRequestTaskPayload.getServiceContactDetails());
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW));
    }

    @Test
    void getRequestTaskPayloadType() {
        assertEquals(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW_PAYLOAD, initializer.getRequestTaskPayloadType());
    }
}