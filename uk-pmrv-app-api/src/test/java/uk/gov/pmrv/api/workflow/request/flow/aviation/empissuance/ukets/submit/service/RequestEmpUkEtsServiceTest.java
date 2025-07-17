package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.flightaircraftprocedures.EmpFlightAndAircraftProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestEmpUkEtsServiceTest {

    @InjectMocks
    private RequestEmpUkEtsService requestEmpUkEtsService;

    @Mock
    private RequestService requestService;

    @Mock
    private EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;

    @Mock
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Test
    void applySaveAction() {
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
                EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                                .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder()
                                                .dateOfStart(LocalDate.now())
                                                .submittedOnTime(true).build())
                                .flightAndAircraftProcedures(EmpFlightAndAircraftProcedures.builder().build())
                                .build())
                        .empSectionsCompleted(Map.of("Section A", List.of(true)))
                        .build();

        RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).build();

        EmissionsMonitoringPlanUkEts updatedEmissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
                .abbreviations(EmpAbbreviations.builder()
                        .exist(true)
                        .abbreviationDefinitions(List.of(EmpAbbreviationDefinition.builder().abbreviation("abbr").definition("def").build()))
                        .build())
                .additionalDocuments(EmpAdditionalDocuments.builder()
                        .exist(true)
                        .documents(Set.of(UUID.randomUUID()))
                        .build())
                .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder()
                        .dateOfStart(LocalDate.of(2023, Month.APRIL, 26))
                        .submittedOnTime(false)
                        .reasonForLateSubmission("my reason")
                        .build())
                .flightAndAircraftProcedures(EmpFlightAndAircraftProcedures.builder()
                        .aircraftUsedDetails(createProcedureForm())
                        .ukEtsFlightsCoveredDetails(createProcedureForm())
                        .flightListCompletenessDetails(createProcedureForm())
                        .build())
                .build();

        EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload requestTaskActionPayload =
                EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_PAYLOAD)
                        .emissionsMonitoringPlan(updatedEmissionsMonitoringPlan)
                        .empSectionsCompleted(Map.of("Section B", List.of(true)))
                        .build();

        //invoke
        requestEmpUkEtsService.applySaveAction(requestTaskActionPayload, requestTask);

        //verify

        assertThat(requestTask.getPayload()).isInstanceOf(EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.class);

        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload payloadSaved =
                (EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        assertEquals(updatedEmissionsMonitoringPlan, payloadSaved.getEmissionsMonitoringPlan());
        assertThat(payloadSaved.getEmpSectionsCompleted()).containsExactly(Map.entry("Section B", List.of(true)));
    }

    @Test
    void applySubmitAction() {
        AppUser user = AppUser.builder().userId("userId").build();
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder()
                .dateOfStart(LocalDate.of(2023, Month.APRIL, 26))
                .submittedOnTime(false)
                .reasonForLateSubmission("my reason")
                .build())
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
        Map<String, List<Boolean>> empSectionsCompleted = Map.of("Section A", List.of(true));
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
                EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                        .emissionsMonitoringPlan(emissionsMonitoringPlan)
                        .empSectionsCompleted(empSectionsCompleted)
                        .build();

        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload = EmpIssuanceUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .abbreviations(EmpAbbreviations.builder().exist(false).build())
                        .additionalDocuments(EmpAdditionalDocuments.builder().build())
                        .flightAndAircraftProcedures(EmpFlightAndAircraftProcedures.builder().build())
                        .build())
                .empSectionsCompleted(Map.of("Section B", List.of(true)))
                .build();
        Request request = Request.builder().payload(empIssuanceUkEtsRequestPayload).build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(requestTaskPayload)
                .build();

        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
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

        EmissionsMonitoringPlanUkEts expectedEmissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder()
            .applicationTimeframeInfo(EmpApplicationTimeframeInfo.builder()
                .dateOfStart(LocalDate.of(2023, Month.APRIL, 26))
                .submittedOnTime(false)
                .reasonForLateSubmission("my reason")
                .build())
            .operatorDetails(EmpOperatorDetails.builder()
                .operatorName(operatorName)
                .crcoCode(crcoCode)
                .activitiesDescription(ActivitiesDescription.builder()
                    .operatorType(OperatorType.COMMERCIAL)
                    .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                    .flightTypes(Set.of(FlightType.SCHEDULED))
                    .activityDescription("activity description")
                    .build())
                .build())
            .build();
        EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload empApplicationSubmittedPayload =
            EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED_PAYLOAD)
                .emissionsMonitoringPlan(expectedEmissionsMonitoringPlan)
                .serviceContactDetails(serviceContactDetails)
                .empSectionsCompleted(empSectionsCompleted)
                .build();

        when(requestAviationAccountQueryService.getAccountInfo(request.getAccountId())).thenReturn(aviationAccountInfo);

        requestEmpUkEtsService.applySubmitAction(requestTask, user);

        verify(empUkEtsValidatorService, times(1)).validateEmissionsMonitoringPlan(empContainer);
        verify(requestService, times(1)).addActionToRequest(
                request,
                empApplicationSubmittedPayload,
                RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED,
                user.getUserId()
        );

        EmpIssuanceUkEtsRequestPayload updatedRequestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        assertNotNull(updatedRequestPayload);
        assertEquals(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD, updatedRequestPayload.getPayloadType());
        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
        assertThat(updatedRequestPayload.getEmpSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(empSectionsCompleted);
        assertEquals(emissionsMonitoringPlan, updatedRequestPayload.getEmissionsMonitoringPlan());
    }

    private EmpProcedureForm createProcedureForm() {
        return EmpProcedureForm.builder()
                .procedureDescription("procedureDescription")
                .procedureDocumentName("procedureDocumentName")
                .procedureReference("procedureReference")
                .responsibleDepartmentOrRole("responsibleDepartment")
                .locationOfRecords("locationOfRecords")
                .itSystemUsed("itSystem")
                .build();
    }
}