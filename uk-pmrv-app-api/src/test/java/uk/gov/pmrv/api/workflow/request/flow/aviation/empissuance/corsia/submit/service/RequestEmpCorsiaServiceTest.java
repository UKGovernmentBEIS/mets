package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpFlightAndAircraftProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation.EmpCorsiaValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class RequestEmpCorsiaServiceTest {

    @InjectMocks
    private RequestEmpCorsiaService requestEmpCorsiaService;

    @Mock
    private RequestService requestService;

    @Mock
    private EmpCorsiaValidatorService empCorsiaValidatorService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void applySaveAction() {
        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                        .empSectionsCompleted(Map.of("Section A", List.of(true)))
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                            .abbreviations(EmpAbbreviations.builder().exist(false).build())
                            .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                            .build())
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        EmissionsMonitoringPlanCorsia updatedEmissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .abbreviations(EmpAbbreviations.builder()
                .exist(true)
                .abbreviationDefinitions(List.of(
                    EmpAbbreviationDefinition.builder().abbreviation("abbr").definition("def").build()))
                .build())
            .additionalDocuments(EmpAdditionalDocuments.builder()
                .exist(true)
                .documents(Set.of(UUID.randomUUID()))
                .build())
            .build();

        EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD)
                        .emissionsMonitoringPlan(updatedEmissionsMonitoringPlan)
                        .empSectionsCompleted(Map.of("Section B", List.of(true)))
                        .build();

        //invoke
        requestEmpCorsiaService.applySaveAction(requestTaskActionPayload, requestTask);

        //verify
        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload.class);

        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload payloadSaved =
                (EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        assertEquals(updatedEmissionsMonitoringPlan, payloadSaved.getEmissionsMonitoringPlan());
        assertThat(payloadSaved.getEmpSectionsCompleted()).containsExactly(Map.entry("Section B", List.of(true)));
    }

    @Test
    void applySubmitAction() {
        AppUser user = AppUser.builder().userId("userId").build();
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName("name")
                .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                    .operatorType(OperatorType.COMMERCIAL)
                    .flightTypes(Set.of(FlightType.SCHEDULED))
                    .activityDescription("activity description")
                    .build())
                .build())
            .build();
        Map<String, List<Boolean>> empSectionsCompleted = Map.of("Section A", List.of(true));
        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .empSectionsCompleted(empSectionsCompleted)
                .build();

        EmpIssuanceCorsiaRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceCorsiaRequestPayload.builder()
            .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .additionalDocuments(EmpAdditionalDocuments.builder().build())
                .flightAndAircraftProcedures(EmpFlightAndAircraftProceduresCorsia.builder().build())
                .build())
            .empSectionsCompleted(Map.of("Section B", List.of(true)))
            .build();
        Request request = Request.builder().payload(empIssuanceUkEtsRequestPayload).build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(requestTaskPayload)
            .build();

        EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .scheme(EmissionTradingScheme.CORSIA)
            .emissionsMonitoringPlan(emissionsMonitoringPlan)
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

        EmissionsMonitoringPlanCorsia expectedEmissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder()
            .operatorDetails(EmpCorsiaOperatorDetails.builder()
                .operatorName(operatorName)
                .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                    .operatorType(OperatorType.COMMERCIAL)
                    .flightTypes(Set.of(FlightType.SCHEDULED))
                    .activityDescription("activity description")
                    .build())
                .build())
            .build();
        EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload empApplicationSubmittedPayload =
            EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED_PAYLOAD)
                .emissionsMonitoringPlan(expectedEmissionsMonitoringPlan)
                .serviceContactDetails(serviceContactDetails)
                .empSectionsCompleted(empSectionsCompleted)
                .build();

        when(requestAviationAccountQueryService.getAccountInfo(request.getAccountId())).thenReturn(aviationAccountInfo);

        requestEmpCorsiaService.applySubmitAction(requestTask, user);

        verify(empCorsiaValidatorService, times(1)).validateEmissionsMonitoringPlan(empContainer);
        verify(requestService, times(1)).addActionToRequest(
            request,
            empApplicationSubmittedPayload,
            RequestActionType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED,
            user.getUserId()
        );

        EmpIssuanceCorsiaRequestPayload updatedRequestPayload = (EmpIssuanceCorsiaRequestPayload) request.getPayload();

        assertNotNull(updatedRequestPayload);
        assertEquals(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
    }
}