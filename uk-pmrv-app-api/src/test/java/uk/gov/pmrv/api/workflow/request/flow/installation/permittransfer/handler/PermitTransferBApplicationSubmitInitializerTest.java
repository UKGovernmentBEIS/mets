package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.AbbreviationDefinition;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialSection;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.domain.managementprocedures.DataFlowActivities;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.ProcedurePlan;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.SamplingPlan;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.SamplingPlanDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringReporting;
import uk.gov.pmrv.api.permit.domain.sitediagram.SiteDiagrams;
import uk.gov.pmrv.api.permit.domain.uncertaintyanalysis.UncertaintyAnalysis;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler.PermitTransferBApplicationSubmitInitializer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferBApplicationSubmitInitializerTest {

    @InjectMocks
    private PermitTransferBApplicationSubmitInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private PermitQueryService permitQueryService;

    @Test
    void initializePayload() {

        final Long receivingAccountId = 1L;
        final Long transferringAccountId = 2L;
        final String relatedRequestId = "relatedRequestId";
        final PermitTransferDetails permitTransferDetails = PermitTransferDetails.builder().reason("reason").build();
        final Request request = Request.builder()
            .payload(PermitTransferBRequestPayload.builder()
                .permitTransferDetails(permitTransferDetails)
                .relatedRequestId(relatedRequestId)
                .build())
            .accountId(receivingAccountId)
            .build();
        final List<AbbreviationDefinition> abbreviationDefinitions =
            List.of(AbbreviationDefinition.builder().abbreviation("abbreviation").definition("definition").build());
        final Permit permit = Permit.builder()
            .abbreviations(Abbreviations.builder()
                .exist(true)
                .abbreviationDefinitions(abbreviationDefinitions)
                .build())
            .siteDiagrams(SiteDiagrams.builder().siteDiagrams(Set.of(UUID.randomUUID(), UUID.randomUUID())).build())
            .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder().plans(Set.of(UUID.randomUUID(), UUID.randomUUID())).build())
            .additionalDocuments(AdditionalDocuments.builder().documents(Set.of(UUID.randomUUID(), UUID.randomUUID())).build())
            .uncertaintyAnalysis(UncertaintyAnalysis.builder().attachments(Set.of(UUID.randomUUID(), UUID.randomUUID())).build())
            .managementProcedures(ManagementProcedures.builder()
                .dataFlowActivities(DataFlowActivities.builder().diagramAttachmentId(UUID.randomUUID()).build())
                .monitoringReporting(MonitoringReporting.builder().organisationCharts(new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID()))).build())
                .build())
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(Map.of(
                    MonitoringApproachType.CALCULATION_CO2, 
                    CalculationOfCO2MonitoringApproach.builder()
                        .samplingPlan(SamplingPlan.builder().details(SamplingPlanDetails.builder()
                            .procedurePlan(ProcedurePlan.builder()
                                .procedurePlanIds(new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID())))
                                .build())
                            .build())
                        .build())
                    .build()))
                .build())
            .confidentialityStatement(ConfidentialityStatement.builder().confidentialSections(List.of(
                ConfidentialSection.builder().section("section").explanation("explanation").build()
            )).build())
            .build();
        final PermitEntityDto permitEntityDto = PermitEntityDto.builder()
            .permitContainer(PermitContainer.builder()
                .permitType(PermitType.GHGE)
                .permit(permit)
                .build())
            .build();
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("installationName").build();
        
        when(requestRepository.findById(relatedRequestId)).thenReturn(Optional.of(Request.builder().accountId(transferringAccountId).build()));
        when(permitQueryService.getPermitByAccountId(transferringAccountId)).thenReturn(permitEntityDto);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(receivingAccountId)).thenReturn(
            installationOperatorDetails);    
        
        final PermitTransferBApplicationRequestTaskPayload result = (PermitTransferBApplicationRequestTaskPayload) initializer.initializePayload(request);

        assertEquals(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD, result.getPayloadType());
        assertEquals(permitTransferDetails, result.getPermitTransferDetails());
        assertEquals(PermitType.GHGE, result.getPermitType());
        assertNull(permit.getSiteDiagrams());
        assertNull(permit.getMonitoringMethodologyPlans().getPlans());
        assertNull(permit.getAdditionalDocuments());
        assertNull(permit.getUncertaintyAnalysis());
        assertNull(permit.getConfidentialityStatement());
        assertNull(permit.getManagementProcedures().getDataFlowActivities().getDiagramAttachmentId());
        assertThat(permit.getManagementProcedures().getMonitoringReporting().getOrganisationCharts()).isEmpty();
        assertEquals(installationOperatorDetails, result.getInstallationOperatorDetails());
        assertThat(((CalculationOfCO2MonitoringApproach)permit.getMonitoringApproaches().getMonitoringApproaches()
            .get(MonitoringApproachType.CALCULATION_CO2)).getSamplingPlan().getDetails().getProcedurePlan().getProcedurePlanIds())
            .isEmpty();
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_SUBMIT);
    }
}
