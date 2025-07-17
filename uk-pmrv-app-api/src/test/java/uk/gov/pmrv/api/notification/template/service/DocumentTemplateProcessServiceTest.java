package uk.gov.pmrv.api.notification.template.service;

import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.common.config.CustomFreeMarkerConfiguration;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviationDefinition;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.EmpBlockHourMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.FuelBurnCalculationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelUpliftSupplierRecordType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.datagaps.EmpDataGapsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.CertEmissionsType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.enumeration.FuelTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpFlightAndAircraftProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsiaDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpDataManagement;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpManagementProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpMonitoringReportingRoleCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpMonitoringReportingRolesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.SubsidiaryCompanyCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsreductionclaim.EmpEmissionsReductionClaim;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.flightaircraftprocedures.EmpFlightAndAircraftProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpDataFlowActivities;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpEnvironmentalManagementSystem;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpMonitoringReportingRole;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpMonitoringReportingRoles;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.pmrv.api.notification.template.TemplatesConfiguration;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.WorkflowTemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.abbreviations.AbbreviationDefinition;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummaries;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;
import uk.gov.pmrv.api.permit.domain.envmanagementsystem.EnvironmentalManagementSystem;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvPermitOrLicence;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.domain.installationdesc.InstallationDescription;
import uk.gov.pmrv.api.permit.domain.managementprocedures.AssessAndControlRisk;
import uk.gov.pmrv.api.permit.domain.managementprocedures.DataFlowActivities;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProceduresDefinition;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFraction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContent;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContentTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValue;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.ProcedurePlan;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.SamplingPlan;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.SamplingPlanDetails;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CellAndAnodeType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCTier2EmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetails;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetailsType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Laboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeteringUncertainty;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2Direction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2Direction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallationDetails;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.MeasurementDevice;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TemperaturePressure;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransferredCO2AndN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransportCO2AndN2OPipelineSystems;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringReporting;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringRole;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableAndBiomassEmission;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.TemplateSubsidiaryCompany;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.CalculationOfCO2ReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.CalculationOfPFCReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.DreMonitoringApproachReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.FallbackReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.MeasurementOfCO2ReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.MeasurementOfN2OReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesReasonType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateProcessServiceTest {
	
    private static FreemarkerTemplateEngine freemarkerTemplateEngine;

    @Mock
    private DocumentGeneratorRemoteClientService documentGeneratorClientService;
    
    @BeforeAll
    public static void init() {
        CustomFreeMarkerConfiguration customFreeMarkerConfiguration = new CustomFreeMarkerConfiguration();
        freemarker.template.Configuration freemarkerConfig = customFreeMarkerConfiguration.freemarkerConfig();

        TemplatesConfiguration templatesConfiguration = new TemplatesConfiguration();
        freemarkerTemplateEngine = templatesConfiguration.freemarkerTemplateEngine(freemarkerConfig);
    }

    @Test
    void generateFileDocumentFromTemplate_rfi_template() throws Exception {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        String fileNameToGenerate = "fileNameToGenerate";
        String signatoryUser = "Signatory user full name";
        Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "installation", "L025_P3_Request_for_further_information_notice_20130402.docx");
        FileDTO templateFile = createFile(templateFilePath);

        Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signatureFile = createFile(signatureFilePath);

        Map<String, Object> params = new HashMap<>();
        Date deadlineDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        params.put("deadline", deadlineDate);
        params.put("questions", List.of("question1", "question2"));

        TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, params);
        
        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
        
        ArgumentCaptor<byte[]> postProcessedDocumentCaptor = ArgumentCaptor.forClass(byte[].class);
        
        verify(documentGeneratorClientService, times(1)).generateDocument(postProcessedDocumentCaptor.capture(), eq(fileNameToGenerate));
        
        byte[] postProcessedDocument = postProcessedDocumentCaptor.getValue();
        
        try (InputStream bais = new ByteArrayInputStream(postProcessedDocument);
                XWPFDocument document = new XWPFDocument(bais);
        		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(document)) {
            final String docText = xwpfWordExtractor.getText();
			assertThat(docText).contains(templateParams.getPermitId());
			assertThat(docText).contains(templateParams.getCompetentAuthorityParams().getName());
			assertThat(docText).contains(templateParams.getSignatoryParams().getFullName());
			assertThat(docText).contains(
					((InstallationAccountTemplateParams) templateParams.getAccountParams()).getLegalEntityName());
			assertThat(docText).contains(((InstallationAccountTemplateParams) templateParams.getAccountParams())
					.getLegalEntityLocation().split("\\n")[0]);
			assertThat(docText).contains("question1");
			assertThat(docText).contains("question2");
			assertThat(docText).contains(new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(deadlineDate));
        }
    }

    @Test
    void generateFileDocumentFromTemplate_rfi_template_when_processing_fails_should_throw_DocumentTemplateProcessException() throws IOException {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        String signatoryUser = "Signatory user full name";
        Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "installation", "L025_P3_Request_for_further_information_notice_20130402.docx");
        FileDTO templateFile = createFile(templateFilePath);

        Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signatureFile = createFile(signatureFilePath);

        Map<String, Object> params = new HashMap<>();

        TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, params);

        try {
            new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                    .generateFileDocumentFromTemplate(templateFile, templateParams, "fileNameToGenerate");
        } catch (DocumentTemplateProcessException e) {
            return;
        }

        fail("Should not reach here");
    }

    @Test
    void generateFileDocumentFromTemplate_rde_opred_template() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.OPRED;
        String signatoryUser = "Signatory user full name";
        Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "opred", "installation", "L026 P3 Request for time extension notice.docx");
        FileDTO templateFile = createFile(templateFilePath);

        Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signatureFile = createFile(signatureFilePath);

        Map<String, Object> params = new HashMap<>();
        Date extensionDate = Date.from(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date deadlineDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        params.put("extensionDate", extensionDate);
        params.put("deadline", deadlineDate);
        params.put("toRecipient", "email@email");
        params.put("ccRecipients", List.of("cc1@email", "cc2@email"));

        TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, params);
        
        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
        
        ArgumentCaptor<byte[]> postProcessedDocumentCaptor = ArgumentCaptor.forClass(byte[].class);
        
        verify(documentGeneratorClientService, times(1)).generateDocument(postProcessedDocumentCaptor.capture(), eq(fileNameToGenerate));
        
        byte[] postProcessedDocument = postProcessedDocumentCaptor.getValue();
        
        try (InputStream bais = new ByteArrayInputStream(postProcessedDocument);
                XWPFDocument document = new XWPFDocument(bais);
        		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(document)) {
            final String docText = xwpfWordExtractor.getText();
			assertThat(docText).contains(templateParams.getPermitId());
			assertThat(docText).contains(templateParams.getPermitId());
	        assertThat(docText).contains("ca central info");
	        assertThat(docText).contains(templateParams.getCompetentAuthorityParams().getName());
	        assertThat(docText).contains(templateParams.getSignatoryParams().getFullName());
	        assertThat(docText).contains(((InstallationAccountTemplateParams) templateParams.getAccountParams()).getLegalEntityName());
	        assertThat(docText).contains(((InstallationAccountTemplateParams) templateParams.getAccountParams()).getLegalEntityLocation().split("\\n")[0]);
	        assertThat(docText).contains(new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(extensionDate));
        };
    }

    @Test
    void generate_dre() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.NORTHERN_IRELAND;
        String signatoryUser = "Signatory user full name";
        Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "northern_ireland", "installation", "L019 Determination Notice.docx");
        FileDTO templateFile = createFile(templateFilePath);

        Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signatureFile = createFile(signatureFilePath);

        Map<String, Object> params = new HashMap<>();
        params.put("toRecipient", "email@email");
        params.put("ccRecipients", List.of("cc1@email", "cc2@email"));
        params.put("determinationReasonDescription", "Verified report not submitted in accordance with the order");
        params.put("chargeOperator", true);
        params.put("officialNoticeReason", "Official Notice Reason");
        params.put("informationSources", List.of("Info source 1", "Info source 2"));
        params.put("feeAmount", BigDecimal.valueOf(1200L));
        params.put("feeDetails", DreFeeDetails.builder()
                .dueDate(LocalDate.now())
                .hourlyRate(BigDecimal.valueOf(400))
                .totalBillableHours(BigDecimal.valueOf(3))
                .build());
        params.put("initiatorRequest", AerInitiatorRequest.builder().type(RequestType.PERMIT_REVOCATION).submissionDateTime(LocalDateTime.now()).build());
        params.put("totalReportableEmissions", BigDecimal.valueOf(100));
        params.put("transferredEmissions", BigDecimal.valueOf(133));
        params.put("reportingYear", Year.of(2022));
        Map<MonitoringApproachType, DreMonitoringApproachReportingEmissions> monApproaches = new HashMap<>();
        monApproaches.put(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2ReportingEmissions.builder()
                .combustionEmissions(ReportableAndBiomassEmission.builder()
                        .reportableEmissions(BigDecimal.valueOf(123L))
                        .build())
                .processEmissions(ReportableAndBiomassEmission.builder()
                        .reportableEmissions(BigDecimal.valueOf(124L))
                        .build())
                .build());
        monApproaches.put(MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2ReportingEmissions.builder()
                .emissions(ReportableAndBiomassEmission.builder()
                        .reportableEmissions(BigDecimal.valueOf(125L))
                        .build())
                .build());
        monApproaches.put(MonitoringApproachType.FALLBACK, FallbackReportingEmissions.builder()
                .emissions(ReportableAndBiomassEmission.builder()
                        .reportableEmissions(BigDecimal.valueOf(126L))
                        .build())
                .build());
        monApproaches.put(MonitoringApproachType.MEASUREMENT_N2O, MeasurementOfN2OReportingEmissions.builder()
                .emissions(ReportableAndBiomassEmission.builder()
                        .reportableEmissions(BigDecimal.valueOf(127L))
                        .build())
                .build());
        monApproaches.put(MonitoringApproachType.CALCULATION_PFC, CalculationOfPFCReportingEmissions.builder()
                .totalEmissions(ReportableAndBiomassEmission.builder()
                        .reportableEmissions(BigDecimal.valueOf(128L))
                        .build())
                .build());

        params.put("reportableEmissions", monApproaches);
        TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, params);
        
        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
        
        ArgumentCaptor<byte[]> postProcessedDocumentCaptor = ArgumentCaptor.forClass(byte[].class);
        
        verify(documentGeneratorClientService, times(1)).generateDocument(postProcessedDocumentCaptor.capture(), eq(fileNameToGenerate));
        
        byte[] postProcessedDocument = postProcessedDocumentCaptor.getValue();
        
        try (InputStream bais = new ByteArrayInputStream(postProcessedDocument);
                XWPFDocument document = new XWPFDocument(bais);
        		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(document)) {
            final String docText = xwpfWordExtractor.getText();
            assertThat(docText).contains("400");
        };
    }

    @Test
    void generate_permit() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.NORTHERN_IRELAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "northern_ireland", "installation", "INPermitApplication_Permit_UK ETS Final v5.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final SortedMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2022", new BigDecimal(100));
        annualEmissionsTargets.put("2021", new BigDecimal(200));

        final Map<String, List<DocumentTemplatePermitParamsProvider.ReferenceSource>> referenceSources = new HashMap<>();
        referenceSources.put(MonitoringApproachType.CALCULATION_CO2.name(), List.of(
                DocumentTemplatePermitParamsProvider.ReferenceSource.builder()
                        .sourceStream("ss ref1")
                        .emissionSources(Set.of("source ref 1", "source ref 2"))
                        .parameter(DocumentTemplatePermitParamsProvider.Parameter.NCV)
                        .type("Conservative Estimation")
                        .defaultValue("net cal value")
                        .build(),
                DocumentTemplatePermitParamsProvider.ReferenceSource.builder()
                        .sourceStream("ss ref1")
                        .emissionSources(Set.of("source ref 1", "source ref 2"))
                        .parameter(DocumentTemplatePermitParamsProvider.Parameter.EF)
                        .type("British Ceramic Confederation")
                        .defaultValue("ef value")
                        .build(),
                DocumentTemplatePermitParamsProvider.ReferenceSource.builder()
                        .sourceStream("ss ref2")
                        .emissionSources(Set.of("source ref 1"))
                        .parameter(DocumentTemplatePermitParamsProvider.Parameter.EF)
                        .type("British Ceramic Confederation")
                        .defaultValue("ef value")
                        .build()
        ));
        final Map<String, List<DocumentTemplatePermitParamsProvider.AnalysisMethod>> analysisMethods = new HashMap<>();
        analysisMethods.put(MonitoringApproachType.CALCULATION_CO2.name(), List.of(
                DocumentTemplatePermitParamsProvider.AnalysisMethod.builder()
                        .sourceStream("ss ref1")
                        .emissionSources(Set.of("source ref 1", "source ref 2"))
                        .parameter(DocumentTemplatePermitParamsProvider.Parameter.OxF)
                        .analysis("analysis 1")
                        .samplingFrequency(CalculationSamplingFrequency.CONTINUOUS.getDescription())
                        .laboratoryName("lab 1")
                        .laboratoryAccredited(true)
                        .build(),
                DocumentTemplatePermitParamsProvider.AnalysisMethod.builder()
                        .sourceStream("ss ref1")
                        .emissionSources(Set.of("source ref 1", "source ref 2"))
                        .parameter(DocumentTemplatePermitParamsProvider.Parameter.OxF)
                        .analysis("analysis 2")
                        .samplingFrequency(CalculationSamplingFrequency.BI_ANNUALLY.getDescription())
                        .laboratoryName("lab 2")
                        .laboratoryAccredited(true)
                        .build(),
                DocumentTemplatePermitParamsProvider.AnalysisMethod.builder()
                        .sourceStream("ss ref2")
                        .emissionSources(Set.of("source ref 1"))
                        .parameter(DocumentTemplatePermitParamsProvider.Parameter.CC)
                        .analysis("analysis 3")
                        .samplingFrequency(CalculationSamplingFrequency.CONTINUOUS.getDescription())
                        .laboratoryName("lab 3")
                        .laboratoryAccredited(false)
                        .build()
        ));

        final UUID procedurePlanId1 = UUID.randomUUID();
        final UUID procedurePlanId2 = UUID.randomUUID();
        final UUID someOtherFileId = UUID.randomUUID();
        final UUID assessAndControlRiskFileId1 = UUID.randomUUID();
        final UUID assessAndControlRiskFileId2 = UUID.randomUUID();
        final Set<UUID> assessAndControlRiskFiles = new HashSet<>(Arrays.asList(assessAndControlRiskFileId1, assessAndControlRiskFileId2));

        final PermitContainer permitContainer = PermitContainer.builder()
                .permitAttachments(Map.of(
                        procedurePlanId1, "procedurePlan1.txt",
                        procedurePlanId2, "procedurePlan2.txt",
                        someOtherFileId, "someOtherFile.txt",
                        assessAndControlRiskFileId1, "assessAndControlRiskFile.txt",
                        assessAndControlRiskFileId2, "assessAndControlRiskFile2.txt"
                    )
                )
                .installationOperatorDetails(InstallationOperatorDetails.builder()
                        .companyReferenceNumber("companyReferenceNumber")
                        .operatorDetailsAddress(
                                AddressDTO.builder()
                                        .line1("line1")
                                        .line2("line2")
                                        .city("city")
                                        .country("country")
                                        .postcode("postcode")
                                        .build())
                        .installationLocation(LocationOnShoreDTO.builder().gridReference("gridReference").build())
                        .build())
                .permit(Permit.builder().regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(
                                RegulatedActivity.builder().type(RegulatedActivityType.COMBUSTION).build(),
                                RegulatedActivity.builder().type(RegulatedActivityType.COKE_PRODUCTION).build())).build())
                        .installationDescription(
                                InstallationDescription.builder()
                                        .mainActivitiesDesc("mainActivitiesDesc\r\n"
                                                + " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.\r\n"
                                                + "\r\n"
                                                + "In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.\r\n"
                                                + "\r\n"
                                                + "Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis."
                                        )
                                        .siteDescription("siteDescription\r\n"
                                                + " Vestibulum finibus consectetur velit, sed laoreet neque varius nec. Suspendisse mollis ipsum vitae dui suscipit, vel tristique mi finibus. In viverra cursus sem, non tincidunt erat viverra finibus. In viverra dictum sem id blandit. Aliquam arcu erat, dapibus vel risus id, tincidunt ultricies quam. Curabitur quis ultricies felis. Nullam gravida eros metus, non ultrices tellus eleifend sit amet. Nulla sagittis facilisis malesuada. Vestibulum blandit, sapien vel luctus posuere, neque tortor laoreet velit, sed tristique eros nisi eget justo. Vivamus ac eros at enim molestie viverra vel nec orci. In rhoncus mauris eget nisi porta, ut tristique est imperdiet. Vestibulum feugiat nec nulla eget consequat.\r\n"
                                                + "\r\n"
                                                + "Duis mauris mi, maximus a dolor vel, rhoncus maximus nulla. Nulla eu erat porta, pellentesque neque at, porta purus. Sed luctus purus est, non rhoncus libero accumsan ac. Vestibulum sit amet aliquet risus. Nam sollicitudin lacus id placerat suscipit. Maecenas et diam suscipit, fermentum ex nec, lacinia orci. Suspendisse posuere fringilla tortor. In efficitur leo nulla, et tristique mi euismod nec. Nunc ut leo pretium, tempus turpis eget, porttitor ligula. Phasellus congue euismod enim quis tincidunt. Curabitur id sollicitudin ante, non maximus est. Duis laoreet mattis felis sit amet laoreet. Fusce quis faucibus sem, vitae suscipit velit.\r\n"
                                                + "\r\n"
                                                + "Duis bibendum nulla id sapien sodales pretium. Nulla facilisi. Mauris at turpis orci. Pellentesque ac sem nibh. Fusce tristique vulputate diam a suscipit. Aenean venenatis mi quis libero convallis, eu tempor sapien placerat. Suspendisse hendrerit purus in felis pretium, nec congue quam posuere. In condimentum magna rutrum sagittis pulvinar. Aliquam vestibulum metus ut ex facilisis, a tempor odio mollis. Fusce et est ullamcorper, sagittis libero a, imperdiet eros. Maecenas bibendum felis vel nunc convallis ullamcorper. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc tristique justo non erat pulvinar, at semper elit ornare. Suspendisse ultrices ullamcorper sem eget fringilla.\r\n"
                                                + "\r\n"
                                                + "Phasellus at ante posuere, fringilla odio id, feugiat eros. Aliquam dignissim orci non magna dapibus, eu lacinia eros auctor. Aenean scelerisque ante nec pulvinar convallis. Etiam ut eros lectus. In sit amet aliquet enim, vel egestas lacus. Fusce porttitor tortor aliquam, hendrerit dui eu, sagittis nisi. Donec ipsum tortor, blandit et gravida at, feugiat at velit. Ut a hendrerit justo. Morbi vestibulum orci suscipit egestas mattis. Donec convallis quis mi nec pharetra. Phasellus volutpat, lectus at volutpat accumsan, purus turpis vestibulum enim, ornare tempus ante neque non nisi. Sed laoreet condimentum dolor, in euismod orci aliquam sit amet. Donec a elementum turpis. Morbi posuere tempor enim sit amet hendrerit. Phasellus laoreet dictum egestas.\r\n"
                                                + "\r\n"
                                                + "Vivamus sollicitudin maximus est, a scelerisque nulla aliquam sed. Vivamus varius ut lorem sit amet pulvinar. Pellentesque mauris orci, ultricies eu elementum in, finibus ac augue. Morbi arcu augue, lacinia vel augue ut, condimentum pretium eros. Curabitur quis velit purus. Duis accumsan ex at nunc consequat, et volutpat nibh pretium. Vivamus egestas blandit dolor. Donec rhoncus euismod enim, id scelerisque orci eleifend eget. Pellentesque tristique nisi condimentum diam dictum volutpat. Sed malesuada elit enim. Quisque ullamcorper dolor dolor, malesuada sodales nisl rhoncus nec. In semper laoreet nisi, eget porta felis condimentum quis. Aliquam tempus odio nec ligula placerat, et imperdiet quam egestas. Mauris tortor sapien, suscipit ut dignissim quis, egestas eget sapien.\r\n"
                                                + "\r\n"
                                                + "Donec a sem diam. Nam aliquet fermentum tortor, ut vehicula enim imperdiet quis. Suspendisse interdum sem et purus vestibulum laoreet. Aliquam nibh erat, fermentum sed dui non, congue vestibulum ligula. Vestibulum tincidunt lobortis fringilla. Maecenas volutpat nisi nec arcu mattis pellentesque ac non massa. Suspendisse viverra vulputate ligula at vulputate. Duis nec enim justo. Aenean id est in odio aliquet posuere sit amet ut lorem. Maecenas vulputate sapien ac nulla varius viverra. Aenean metus metus, tempor ut metus mattis, facilisis rutrum quam. Donec convallis tincidunt ante ac tempor. Duis euismod neque urna, id dictum dolor aliquet et. Ut cursus at nisl et laoreet.\r\n"
                                                + "\r\n"
                                                + "Morbi ut leo pretium, dictum ante non, facilisis nisi. Praesent porta, nunc ut ultricies semper, neque risus ullamcorper erat, sit amet elementum ex augue ut purus. Aliquam feugiat diam felis, ut faucibus nisl rutrum vitae. Sed dolor tellus, pretium a lectus eu, semper vestibulum enim. Proin pretium lobortis dictum. Vivamus purus elit, suscipit quis lectus ut, ultricies imperdiet dolor. Sed pharetra, eros eget maximus pellentesque, lacus justo fringilla arcu, at vehicula turpis ipsum vitae lectus. Aliquam sodales porta elit, vel aliquam orci consequat eget. Donec mi erat, condimentum quis dolor non, porttitor luctus lectus. Phasellus efficitur ligula quam, eget malesuada felis interdum semper. Aenean mollis venenatis ipsum."
                                        )
                                        .build()
                        )
                        .environmentalPermitsAndLicences(EnvironmentalPermitsAndLicences.builder().exist(true)
                                .envPermitOrLicences(List.of(
                                        EnvPermitOrLicence.builder().issuingAuthority("ia1").num("1").type("type1").build(),
                                        EnvPermitOrLicence.builder().permitHolder("ph2").issuingAuthority("ia2").num("2").type("type2").build())
                                )
                                .build())
                        .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(new BigDecimal(444)).build())
                        .regulatedActivities(
                                RegulatedActivities.builder().regulatedActivities(List.of(
                                                RegulatedActivity.builder()
                                                        .id("ra1")
                                                        .type(RegulatedActivityType.BULK_ORGANIC_CHEMICAL_PRODUCTION)
                                                        .build()))
                                        .build())
                        .sourceStreams(SourceStreams.builder().sourceStreams(List.of(
                                SourceStream.builder()
                                        .id("ss1")
                                        .type(SourceStreamType.AMMONIA_FUEL_AS_PROCESS_INPUT)
                                        .description(SourceStreamDescription.ACETYLENE)
                                        .reference("ss ref1")
                                        .build(),
                                SourceStream.builder()
                                        .id("ss2")
                                        .type(SourceStreamType.COMBUSTION_FLARES)
                                        .description(SourceStreamDescription.BIOGASOLINE)
                                        .reference("ss ref2")
                                        .build()
                        )).build())
                        .emissionSources(EmissionSources.builder().emissionSources(List.of(
                                EmissionSource.builder().id("es1").description("source desc 1").reference("source ref 1").build(),
                                EmissionSource.builder().id("es2").description("source desc 2").reference("source ref 2").build()
                        )).build())
                        .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(
                                        EmissionPoint.builder().id("ep1").description("point desc 1").reference("point ref 1").build(),
                                        EmissionPoint.builder().id("ep2").description("point desc 2").reference("point ref 2").build()
                                )
                        ).build())
                        .emissionSummaries(EmissionSummaries.builder().emissionSummaries(List.of(
                                EmissionSummary.builder()
                                        .emissionSources(Set.of("es1", "es2"))
                                        .emissionPoints(Set.of("ep1", "ep2"))
                                        .sourceStream("ss1")
                                        .excludedRegulatedActivity(false)
                                        .regulatedActivity("ra1")
                                        .build(),
                                EmissionSummary.builder()
                                        .emissionSources(Set.of("es2", "es1"))
                                        .emissionPoints(Set.of("ep2", "ep1"))
                                        .sourceStream("ss2")
                                        .excludedRegulatedActivity(true)
                                        .build()
                        )).build())
                        .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(List.of(
                                MeasurementDeviceOrMethod.builder()
                                        .reference("md ref 1")
                                        .measurementRange("range 1")
                                        .type(MeasurementDeviceType.BALANCE)
                                        .id("md id 1")
                                        .location("location 1")
                                        .meteringRangeUnits("unit 1")
                                        .uncertaintySpecified(true)
                                        .specifiedUncertaintyPercentage(new BigDecimal(12))
                                        .build(),
                                MeasurementDeviceOrMethod.builder()
                                        .reference("md ref 2")
                                        .measurementRange("range 2")
                                        .type(MeasurementDeviceType.GAS_CHROMATOGRAPH)
                                        .id("md id 2")
                                        .location("location 2")
                                        .meteringRangeUnits("unit 2")
                                        .uncertaintySpecified(false)
                                        .build()
                        )).build())
                        .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(Map.of(
                                MonitoringApproachType.CALCULATION_CO2, createCalculationMonitoringApproach(procedurePlanId1, procedurePlanId2),
                                MonitoringApproachType.FALLBACK, createFallbackMonitoringApproach(),
                                MonitoringApproachType.CALCULATION_PFC, createPfcMonitoringApproach(),
                                MonitoringApproachType.MEASUREMENT_CO2, createMeasurementMonitoringApproach(Set.of(procedurePlanId1, procedurePlanId2)),
                                MonitoringApproachType.MEASUREMENT_N2O, createN2OMonitoringApproach(Set.of(procedurePlanId1, procedurePlanId2)),
                                MonitoringApproachType.INHERENT_CO2, createInherentMonitoringApproach(),
                                MonitoringApproachType.TRANSFERRED_CO2_N2O, createTransferCO2MonitoringApproach()
                        )).build())
                        .managementProcedures(ManagementProcedures.builder()
                                .monitoringReporting(MonitoringReporting.builder()
                                        .organisationCharts(Set.of(UUID.randomUUID()))
                                        .monitoringRoles(List.of(
                                                        MonitoringRole.builder().jobTitle("Awesome Job").mainDuties("Doing awesome job")
                                                                .build(),
                                                        MonitoringRole.builder()
                                                                .jobTitle("Not Awesome Job")
                                                                .mainDuties("Doing not so awesome job")
                                                                .build()
                                                )
                                        )
                                        .build())
                                .assessAndControlRisk(buildAssessAndControlRisk(assessAndControlRiskFiles))
                                .assignmentOfResponsibilities(buildManagementProceduresDefinition())
                                .controlOfOutsourcedActivities(buildManagementProceduresDefinition())
                                .correctionsAndCorrectiveActions(buildManagementProceduresDefinition())
                                .monitoringPlanAppropriateness(buildManagementProceduresDefinition())
                                .reviewAndValidationOfData(buildManagementProceduresDefinition())
                                .recordKeepingAndDocumentation(buildManagementProceduresDefinition())
                                .qaMeteringAndMeasuringEquipment(buildManagementProceduresDefinition())
                                .qaDataFlowActivities(buildManagementProceduresDefinition())
                                .dataFlowActivities(buildDataflowActivities(procedurePlanId1))
                                .controlOfOutsourcedActivities(buildManagementProceduresDefinition())
                                .environmentalManagementSystem(
                                        EnvironmentalManagementSystem.builder()
                                                .exist(true)
                                                .certified(Boolean.FALSE)
                                                .certificationStandard(null)
                                                .build()
                                )
                                .build())
                        .abbreviations(Abbreviations.builder()
                                .exist(true)
                                .abbreviationDefinitions(List.of(
                                        AbbreviationDefinition.builder()
                                                .abbreviation("Some abbreviation")
                                                .definition("The definition of abbreviation")
                                                .build(),
                                        AbbreviationDefinition.builder()
                                                .abbreviation("Another abbreviation")
                                                .definition("The definition of another abbreviation")
                                                .build()
                                ))
                                .build())
                        .build())
                .activationDate(LocalDate.of(2022, 1, 1))
                .annualEmissionsTargets(annualEmissionsTargets)
                .build();

        final PermitIssuanceRequestMetadata issuanceRequestMetadata = PermitIssuanceRequestMetadata.builder()
                .rfiResponseDates(List.of(
                                LocalDateTime.of(2021, 1, 1, 1, 1),
                                LocalDateTime.of(2022, 2, 2, 2, 2)
                        )
                ).build();

        final TransferCO2 transfer = TransferCO2.builder()
                .transferDirection(TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION)
                .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
                .installationEmitter(InstallationEmitter.builder()
                        .emitterId("EmitterId")
                        .email("test@test.com")
                        .build())
                .entryAccountingForTransfer(true)
                .build();

        final List<PermitVariationRequestInfo> variationRequestInfoList = List.of(
                PermitVariationRequestInfo.builder()
                        .id("AEMV12345-24")
                        .submissionDate(LocalDateTime.of(2021, 1, 1, 1, 1))
                        .endDate(LocalDateTime.of(2022, 1, 1, 1, 1))
                        .metadata(PermitVariationRequestMetadata.builder()
                                .rfiResponseDates(List.of(
                                        LocalDateTime.of(2021, 1, 1, 1, 1),
                                        LocalDateTime.of(2021, 10, 1, 1, 1),
                                        LocalDateTime.of(2022, 2, 2, 2, 2)))
                                .logChanges(
                                        "01. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "02. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "03. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "04. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "05. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "06. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "07. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "08. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "09. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "10. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "11. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "12. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "13. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "14. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "15. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "16. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "17. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "18. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "19. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "20. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "21. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "22. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "23. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "24. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "25. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "26. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "27. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "28. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "29. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
                                )
                                .build())
                        .build(),
                PermitVariationRequestInfo.builder()
                        .id("AEMV12345-25")
                        .submissionDate(LocalDateTime.of(2022, 1, 1, 1, 1))
                        .endDate(LocalDateTime.of(2022, 1, 10, 1, 1))
                        .metadata(PermitVariationRequestMetadata.builder()
                                .rfiResponseDates(List.of(
                                        LocalDateTime.of(2022, 1, 2, 1, 1),
                                        LocalDateTime.of(2022, 1, 3, 1, 1),
                                        LocalDateTime.of(2022, 1, 4, 2, 2)))
                                .logChanges(
                                        "01. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "02. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "03. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\r\n"
                                                + "04. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
                                )
                                .build())
                        .build()
        );

        final TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, Map.of(
                "permitContainer", permitContainer,
                "consolidationNumber", 24,
                "issuanceMetadata", issuanceRequestMetadata,
                "variationRequestInfoList", variationRequestInfoList,
                "referenceSources", referenceSources,
                "analysisMethods", analysisMethods,
                "transfers", List.of(transfer))
        );
        
        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path empTemplateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "UK ETS monitoring plan_template_aviation_MR.docx");
        final FileDTO empTemplateFile = createFile(empTemplateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .serviceContactDetails(ServiceContactDetails.builder()
                        .name("service contact name")
                        .email("service-contact@mail")
                        .roleCode("service contact role code")
                        .build())
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach. builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                                .build())
//                        .emissionsMonitoringApproach(SupportFacilityMonitoringApproach.builder()
//                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
//                                .explanation("My explanation")
//                                .build())
                        .operatorDetails(EmpOperatorDetails.builder()
                                .crcoCode("crco code")
                                .operatorName("operator name")
                                .flightIdentification(FlightIdentification.builder()
                                        .flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                                        .aircraftRegistrationMarkings(Set.of("registration marking 1",
                                                "registration marking 2", "registration maqrking 3"))
                                        .build())
                                .airOperatingCertificate(AirOperatingCertificate.builder()
                                        .certificateExist(Boolean.TRUE)
                                        .certificateNumber("Certificate Number 123")
                                        .issuingAuthority("Certificate issuing authority")
                                        .build())
                                .operatingLicense(OperatingLicense.builder()
                                        .licenseExist(Boolean.TRUE)
                                        .licenseNumber("License number 123")
                                        .issuingAuthority("License issuing authority")
                                        .build())
                                .organisationStructure(LimitedCompanyOrganisation.builder()
                                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                                        .registrationNumber("Limited company registration number")
                                        .organisationLocation(LocationOnShoreStateDTO.builder()
                                                .type(LocationType.ONSHORE_STATE)
                                                .line1("line1")
                                                .city("city")
                                                .state("state")
                                                .postcode("1234")
                                                .country("country")
                                                .build())
                                        .differentContactLocationExist(Boolean.TRUE)
                                        .differentContactLocation(LocationOnShoreStateDTO.builder()
                                                .type(LocationType.ONSHORE_STATE)
                                                .line1("diff line1")
                                                .line2("diff line 2")
                                                .city("diff city")
                                                .postcode("1234")
                                                .country("country")
                                                .build())
                                        .build())
                                .activitiesDescription(ActivitiesDescription.builder()
                                        .operatorType(OperatorType.COMMERCIAL)
                                        .flightTypes(Set.of(FlightType.SCHEDULED, FlightType.NON_SCHEDULED))
                                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC, OperationScope.UK_TO_EEA_COUNTRIES))
                                        .activityDescription("""
activity description

Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                         """)
                                        .build())
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .manufacturer("manufacturer 1")
                                                        .model("model 1")
                                                        .designatorType("designator type 1")
                                                        .build())
                                                .subtype("subtype 1")
                                                .numberOfAircrafts(10L)
                                                .isCurrentlyUsed(Boolean.TRUE)
                                                .fuelTypes(List.of(FuelType.JET_KEROSENE))
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .manufacturer("manufacturer 2")
                                                        .model("model 2")
                                                        .designatorType("designator type 2")
                                                        .build())
                                                .subtype("subtype 2")
                                                .numberOfAircrafts(20L)
                                                .isCurrentlyUsed(Boolean.TRUE)
                                                .fuelTypes(List.of(FuelType.JET_KEROSENE, FuelType.JET_GASOLINE))
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_B)
                                                .build(),
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .manufacturer("manufacturer 3")
                                                        .model("model 3")
                                                        .designatorType("designator type 3")
                                                        .build())
                                                .subtype("subtype 3")
                                                .numberOfAircrafts(30L)
                                                .isCurrentlyUsed(Boolean.TRUE)
                                                .fuelTypes(List.of(FuelType.AVIATION_GASOLINE))
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF)
                                                .build(),
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .manufacturer("manufacturer 4")
                                                        .model("model 4")
                                                        .designatorType("designator type 4")
                                                        .build())
                                                .subtype("subtype 4")
                                                .numberOfAircrafts(40L)
                                                .isCurrentlyUsed(Boolean.TRUE)
                                                .fuelTypes(List.of(FuelType.OTHER, FuelType.AVIATION_GASOLINE))
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.FUEL_UPLIFT)
                                                .build(),
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .manufacturer("manufacturer 5")
                                                        .model("model 5")
                                                        .designatorType("designator type 5")
                                                        .build())
                                                .numberOfAircrafts(50L)
                                                .isCurrentlyUsed(Boolean.FALSE)
                                                .fuelTypes(List.of(FuelType.JET_KEROSENE, FuelType.JET_GASOLINE))
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .manufacturer("manufacturer 6")
                                                        .model("model 6")
                                                        .designatorType("designator type 6")
                                                        .build())
                                                .subtype("subtype 6")
                                                .numberOfAircrafts(60L)
                                                .isCurrentlyUsed(Boolean.FALSE)
                                                .fuelTypes(List.of(FuelType.JET_KEROSENE, FuelType.JET_GASOLINE, FuelType.AVIATION_GASOLINE))
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.BLOCK_HOUR)
                                                .build()
                                ))
                                .otherFuelExplanation("""
other fuel explanation
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                                .multipleFuelConsumptionMethodsExplanation("""
Multiple fuel consumption methods explanation                                        
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                                .additionalAircraftMonitoringApproach(EmpProcedureForm.builder()
                                        .procedureDocumentName("Additional Aircraft Monitoring Approach document name")
                                        .procedureReference("Additional Aircraft Monitoring Approach procedure reference")
                                        .procedureDescription("""
Additional Aircraft Monitoring Approach procedure description
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                          """)
                                        .responsibleDepartmentOrRole("Additional Aircraft Monitoring Approach responsible department")
                                        .locationOfRecords("Additional Aircraft Monitoring Approach records location")
                                        .itSystemUsed("Additional Aircraft Monitoring Approach IT system")
                                        .build())
                                .build())
                        .flightAndAircraftProcedures(EmpFlightAndAircraftProcedures.builder()
                                .aircraftUsedDetails(EmpProcedureForm.builder()
                                        .procedureDocumentName("Aircraft used document name")
                                        .procedureReference("Aircraft used procedure reference")
                                        .procedureDescription("Aircraft used procedure description")
                                        .responsibleDepartmentOrRole("""
Aircraft used responsible department
01. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
02. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
03. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
04. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
05. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
06. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
07. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
08. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
09. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
10. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
11. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
12. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
13. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
14. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
15. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
16. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
17. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
18. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
19. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
20. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
21. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
22. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
23. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
24. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
25. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
26. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
27. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
28. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
29. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                                          """)
                                        .locationOfRecords("Aircraft used records location")
                                        .itSystemUsed("Aircraft used IT system")
                                        .build())
                                .flightListCompletenessDetails(EmpProcedureForm.builder()
                                        .procedureDocumentName("Flights completeness document name")
                                        .procedureReference("Flights completeness procedure reference")
                                        .procedureDescription("Flights completeness procedure description")
                                        .responsibleDepartmentOrRole("Flights completeness responsible department")
                                        .locationOfRecords("Flights completeness records location")
                                        .itSystemUsed("Flights completeness IT system")
                                        .build())
                                .ukEtsFlightsCoveredDetails(EmpProcedureForm.builder()
                                        .procedureDocumentName("Flights covered document name")
                                        .procedureReference("Flights covered procedure reference")
                                        .procedureDescription("Flights covered procedure description")
                                        .responsibleDepartmentOrRole("Flights covered responsible department")
                                        .locationOfRecords("Flights covered records location")
                                        .itSystemUsed("Flights covered IT system")
                                        .build())
                                .build())
                        .dataGaps(EmpDataGaps.builder()
                                .dataGaps("""
data gaps
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                                .secondaryDataSources("""
secondary data sources
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                                .substituteData("""
substitute data
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                                .otherDataGapsTypes("""
other data gaps types
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                                .build())
                        .managementProcedures(EmpManagementProcedures.builder()
                                .monitoringReportingRoles(EmpMonitoringReportingRoles.builder()
                                        .monitoringReportingRoles(List.of(EmpMonitoringReportingRole.builder()
                                                        .jobTitle("job title 1")
                                                        .mainDuties("""
main duty 1
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                                                """)
                                                        .build(),
                                                EmpMonitoringReportingRole.builder()
                                                        .jobTitle("job title 2")
                                                        .mainDuties("main duty 2")
                                                        .build()))
                                        .build())
                                .assignmentOfResponsibilities(EmpProcedureForm.builder()
                                        .procedureDocumentName("Assignment responsibilities covered document name")
                                        .procedureReference("Assignment responsibilities procedure reference")
                                        .procedureDescription("Assignment responsibilities procedure description")
                                        .responsibleDepartmentOrRole("Assignment responsibilities responsible department")
                                        .locationOfRecords("Assignment responsibilities records location")
                                        .itSystemUsed("Assignment responsibilities IT system")
                                        .build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder()
                                        .procedureDocumentName("Monitoring appropriateness document name")
                                        .procedureReference("Monitoring appropriateness procedure reference")
                                        .procedureDescription("Monitoring appropriateness procedure description")
                                        .responsibleDepartmentOrRole("Monitoring appropriateness responsible department")
                                        .locationOfRecords("Monitoring appropriateness records location")
                                        .itSystemUsed("Monitoring appropriateness IT system")
                                        .build())
                                .correctionsAndCorrectiveActions(EmpProcedureForm.builder()
                                        .procedureDocumentName("Corrections document name")
                                        .procedureReference("Corrections procedure reference")
                                        .procedureDescription("Corrections procedure description")
                                        .responsibleDepartmentOrRole("Corrections responsible department")
                                        .locationOfRecords("Corrections records location")
                                        .itSystemUsed("Corrections IT system")
                                        .build())
                                .controlOfOutsourcedActivities(EmpProcedureForm.builder()
                                        .procedureDocumentName("Outsource activities document name")
                                        .procedureReference("Outsource activities procedure reference")
                                        .procedureDescription("Outsource activities procedure description")
                                        .responsibleDepartmentOrRole("Outsource activities responsible department")
                                        .locationOfRecords("Outsource activities records location")
                                        .itSystemUsed("Outsource activities IT system")
                                        .build())
                                .assessAndControlRisks(EmpProcedureForm.builder()
                                        .procedureDocumentName("Control risks covered document name")
                                        .procedureReference("Control risks procedure reference")
                                        .procedureDescription("Control risks procedure description")
                                        .responsibleDepartmentOrRole("Control risks responsible department")
                                        .locationOfRecords("Control risks records location")
                                        .itSystemUsed("Control risks IT system")
                                        .build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder()
                                        .procedureDocumentName("Monitoring plan appropriateness document name")
                                        .procedureReference("Monitoring plan appropriateness procedure reference")
                                        .procedureDescription("Monitoring plan appropriateness procedure description")
                                        .responsibleDepartmentOrRole("Monitoring plan appropriateness responsible department")
                                        .locationOfRecords("Monitoring plan appropriateness records location")
                                        .itSystemUsed("Monitoring plan appropriateness IT system")
                                        .build())
                                .dataValidation(EmpProcedureForm.builder()
                                        .procedureDocumentName("Data validation  document name")
                                        .procedureReference("Data validation procedure reference")
                                        .procedureDescription("Data validation procedure description")
                                        .responsibleDepartmentOrRole("Data validation responsible department")
                                        .locationOfRecords("Data validation records location")
                                        .itSystemUsed("Data validation IT system")
                                        .build())
                                .dataFlowActivities(EmpDataFlowActivities.builder()
                                        .procedureDocumentName("Data flow activities document name")
                                        .procedureReference("Data flow activities procedure reference")
                                        .procedureDescription("Data flow activities procedure description")
                                        .responsibleDepartmentOrRole("Data flow activities responsible department")
                                        .locationOfRecords("Data flow activities records location")
                                        .itSystemUsed("Data flow activities IT system")
                                        .diagramReference("Data flow activities Diagram reference")
                                        .otherStandardsApplied("Data flow activities Other standards applied")
                                        .primaryDataSources("""
Data flow activities Primary data sources
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                                """)
                                        .processingSteps("""
Data flow activities Processing steps
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                                """)
                                        .build())
                                .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder()
                                        .procedureDocumentName("QA metering document name")
                                        .procedureReference("QA metering procedure reference")
                                        .procedureDescription("QA metering procedure description")
                                        .responsibleDepartmentOrRole("QA metering responsible department")
                                        .locationOfRecords("QA metering records location")
                                        .itSystemUsed("QA metering IT system")
                                        .build())
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder()
                                        .procedureDocumentName("Record keeping document name")
                                        .procedureReference("Record keeping procedure reference")
                                        .procedureDescription("Record keeping procedure description")
                                        .responsibleDepartmentOrRole("Record keeping responsible department")
                                        .locationOfRecords("Record keeping records location")
                                        .itSystemUsed("Record keeping IT system")
                                        .build())
                                .upliftQuantityCrossChecks(EmpProcedureForm.builder()
                                        .procedureDocumentName("Uplift quantity document name")
                                        .procedureReference("Uplift quantity procedure reference")
                                        .procedureDescription("Uplift quantity procedure description")
                                        .responsibleDepartmentOrRole("Uplift quantity responsible department")
                                        .locationOfRecords("Uplift quantity location")
                                        .itSystemUsed("Uplift quantity IT system")
                                        .build())
                                .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder()
                                        .exist(true)
                                        .certified(Boolean.TRUE)
                                        .certificationStandard("Environmental Management System certification standard")
                                        .build())
                                .build())
                        .methodAProcedures(EmpMethodAProcedures.builder()
                                .fuelConsumptionPerFlight(EmpProcedureForm.builder()
                                        .procedureDocumentName("Method A fuel consumption document name")
                                        .procedureReference("Method A fuel consumption procedure reference")
                                        .procedureDescription("Method A fuel consumption procedure description")
                                        .responsibleDepartmentOrRole("Method A fuel consumption responsible department")
                                        .locationOfRecords("Method A fuel consumption location")
                                        .itSystemUsed("Method A fuel consumption IT system")
                                        .build())
                                .fuelDensity(EmpProcedureForm.builder()
                                        .procedureDocumentName("Method A fuel density document name")
                                        .procedureReference("Method A fuel density procedure reference")
                                        .procedureDescription("Method A fuel density procedure description")
                                        .responsibleDepartmentOrRole("Method A fuel density responsible department")
                                        .locationOfRecords("Method A fuel density location")
                                        .itSystemUsed("Method A fuel density IT system")
                                        .build())
                                .build())
                        .methodBProcedures(EmpMethodBProcedures.builder()
                                .fuelConsumptionPerFlight(EmpProcedureForm.builder()
                                        .procedureDocumentName("Method B fuel consumption document name")
                                        .procedureReference("Method B fuel consumption procedure reference")
                                        .procedureDescription("Method B fuel consumption procedure description")
                                        .responsibleDepartmentOrRole("Method B fuel consumption responsible department")
                                        .locationOfRecords("Method B fuel consumption location")
                                        .itSystemUsed("Method B fuel consumption IT system")
                                        .build())
                                .fuelDensity(EmpProcedureForm.builder()
                                        .procedureDocumentName("Method B fuel density document name")
                                        .procedureReference("Method B fuel density procedure reference")
                                        .procedureDescription("Method B fuel density procedure description")
                                        .responsibleDepartmentOrRole("Method B fuel density responsible department")
                                        .locationOfRecords("Method B fuel density location")
                                        .itSystemUsed("Method B fuel density IT system")
                                        .build())
                                .build())
                        .blockOnBlockOffMethodProcedures(EmpBlockOnBlockOffMethodProcedures.builder()
                                .fuelConsumptionPerFlight(EmpProcedureForm.builder()
                                        .procedureDocumentName("Block off block on fuel consumption document name")
                                        .procedureReference("Block off block on fuel consumption procedure reference")
                                        .procedureDescription("Block off block on fuel consumption procedure description")
                                        .responsibleDepartmentOrRole("Block off block on fuel consumption responsible department")
                                        .locationOfRecords("Block off block on fuel consumption location")
                                        .itSystemUsed("Block off block on fuel consumption IT system")
                                        .build())
                                .build())
                        .fuelUpliftMethodProcedures(EmpFuelUpliftMethodProcedures.builder()
                                .blockHoursPerFlight(EmpProcedureForm.builder()
                                        .procedureDocumentName("Fuel uplift block hours document name")
                                        .procedureReference("Fuel uplift block hours procedure reference")
                                        .procedureDescription("Fuel uplift block hours procedure description")
                                        .responsibleDepartmentOrRole("Fuel uplift block hours responsible department")
                                        .locationOfRecords("Fuel uplift block hours location")
                                        .itSystemUsed("Fuel uplift block hours IT system")
                                        .build())
                                .zeroFuelUplift("""
Fuel uplift zero fuel uplift
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                                .fuelDensity(EmpProcedureForm.builder()
                                        .procedureDocumentName("Fuel uplift fuel density document name")
                                        .procedureReference("Fuel uplift fuel density procedure reference")
                                        .procedureDescription("Fuel uplift fuel density procedure description")
                                        .responsibleDepartmentOrRole("Fuel uplift fuel density responsible department")
                                        .locationOfRecords("Fuel uplift fuel density location")
                                        .itSystemUsed("Fuel uplift fuel density IT system")
                                        .build())
                                .build())
                        .blockHourMethodProcedures(EmpBlockHourMethodProcedures.builder()
                                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.CLEAR_DISTINGUISHION, FuelBurnCalculationType.NOT_CLEAR_DISTINGUISHION))
                                .clearDistinguishionIcaoAircraftDesignators(Set.of("clear icao designator 1", "clear icao designator 2"))
                                .notClearDistinguishionIcaoAircraftDesignators(Set.of("not clear icao designator 1", "not clear icao designator 2"))
                                .assignmentAndAdjustment("""
Block hour assignment and adjustment
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                                .blockHoursMeasurement(EmpProcedureForm.builder()
                                        .procedureDocumentName("Block hour block hours measurement document name")
                                        .procedureReference("Block hour block hours measurement procedure reference")
                                        .procedureDescription("Block hour block hours measurement procedure description")
                                        .responsibleDepartmentOrRole("Block hour block hours measurement responsible department")
                                        .locationOfRecords("Block hour block hours measurement location")
                                        .itSystemUsed("Block hour block hours measurement IT system")
                                        .build())
                                .fuelDensity(EmpProcedureForm.builder()
                                        .procedureDocumentName("Block hour fuel density document name")
                                        .procedureReference("Block hour fuel density procedure reference")
                                        .procedureDescription("Block hour fuel density procedure description")
                                        .responsibleDepartmentOrRole("Block hour fuel density responsible department")
                                        .locationOfRecords("Block hour fuel density location")
                                        .itSystemUsed("Block hour fuel density IT system")
                                        .build())
                                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_INVOICES)
                                .build())
                        .emissionsReductionClaim(EmpEmissionsReductionClaim.builder()
                                .exist(true)
                                .safMonitoringSystemsAndProcesses(EmpProcedureForm.builder()
                                        .procedureDocumentName("Emissions reduction claim saf document name")
                                        .procedureReference("Emissions reduction claim saf procedure reference")
                                        .procedureDescription("Emissions reduction claim saf procedure description")
                                        .responsibleDepartmentOrRole("Emissions reduction claim saf responsible department")
                                        .locationOfRecords("Emissions reduction claim saf location")
                                        .itSystemUsed("Emissions reduction claim saf IT system")
                                        .build())
                                .rtfoSustainabilityCriteria(EmpProcedureForm.builder()
                                        .procedureDocumentName("Emissions reduction claim rtfo document name")
                                        .procedureReference("Emissions reduction claim rtfo procedure reference")
                                        .procedureDescription("Emissions reduction claim rtfo procedure description")
                                        .responsibleDepartmentOrRole("Emissions reduction claim rtfo responsible department")
                                        .locationOfRecords("Emissions reduction claim rtfo location")
                                        .itSystemUsed("Emissions reduction claim rtfo IT system")
                                        .build())
                                .safDuplicationPrevention(EmpProcedureForm.builder()
                                        .procedureDocumentName("Emissions reduction claim saf duplication document name")
                                        .procedureReference("Emissions reduction claim saf duplication procedure reference")
                                        .procedureDescription("Emissions reduction claim saf duplication procedure description")
                                        .responsibleDepartmentOrRole("Emissions reduction claim saf duplication responsible department")
                                        .locationOfRecords("Emissions reduction claim saf duplication location")
                                        .itSystemUsed("Emissions reduction claim saf duplication IT system")
                                        .build())
                                .build())
                        .abbreviations(EmpAbbreviations.builder()
                                .exist(true)
                                .abbreviationDefinitions(List.of(EmpAbbreviationDefinition.builder()
                                                .abbreviation("abbreviation 1")
                                                .definition("""
definition 1
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                                        """)
                                                .build(),
                                        EmpAbbreviationDefinition.builder()
                                                .abbreviation("abbreviation 2")
                                                .definition("definition 2")
                                                .build()))
                                .build())
                        .build())
                .build();

        final TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, Map.of(
                "empContainer", empContainer,
                "consolidationNumber", 24)
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(empTemplateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);

//        var fileContent = assertDoesNotThrow(() -> new DocumentTemplateProcessService(freemarkerTemplateEngine)
//                .generateFileDocumentFromTemplate(empTemplateFile, templateParams, "fileNameToGenerate"));
//
//        File outputFile = new File("empOutput.pdf");
//        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
//            outputStream.write(fileContent);
//        }
    }

    @Test
    void generate_emp_approval_official_letter() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "UK ETS EMP Approval Notice_METS.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient", "to recipient"
                )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);

//        var fileContent = assertDoesNotThrow(() -> new DocumentTemplateProcessService(freemarkerTemplateEngine)
//                .generateFileDocumentFromTemplate(empApprovalTemplateFile, templateParams, "fileNameToGenerate"));
//
//        File outputFile = new File("empApprovedOfficialNoticeOutput.pdf");
//        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
//            outputStream.write(fileContent);
//        }
    }

    @Test
    void generate_emp_deemed_withdrawn_official_letter() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "UK ETS EMP Withdrawn Notice_METS.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                        "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                        "toRecipient", "to recipient"
                )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);

//        var fileContent = assertDoesNotThrow(() -> new DocumentTemplateProcessService(freemarkerTemplateEngine)
//                .generateFileDocumentFromTemplate(empDeemedWithdrawnTemplateFile, templateParams, "fileNameToGenerate"));
//
//        File outputFile = new File("empWithdrawnOfficialNoticeOutput.pdf");
//        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
//            outputStream.write(fileContent);
//        }
    }

    private MeasurementOfCO2MonitoringApproach createMeasurementMonitoringApproach(Set<UUID> attachments) {
        return MeasurementOfCO2MonitoringApproach.builder()
                .type(MonitoringApproachType.MEASUREMENT_CO2)
                .approachDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.\r\n"
                        + "\r\n"
                        + "In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.\r\n"
                        + "\r\n"
                        + "Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.\r\n"
                        + "\r\n"
                        + "Vestibulum finibus consectetur velit, sed laoreet neque varius nec. Suspendisse mollis ipsum vitae dui suscipit, vel tristique mi finibus. In viverra cursus sem, non tincidunt erat viverra finibus. In viverra dictum sem id blandit. Aliquam arcu erat, dapibus vel risus id, tincidunt ultricies quam. Curabitur quis ultricies felis. Nullam gravida eros metus, non ultrices tellus eleifend sit amet. Nulla sagittis facilisis malesuada. Vestibulum blandit, sapien vel luctus posuere, neque tortor laoreet velit, sed tristique eros nisi eget justo. Vivamus ac eros at enim molestie viverra vel nec orci. In rhoncus mauris eget nisi porta, ut tristique est imperdiet. Vestibulum feugiat nec nulla eget consequat.\r\n"
                        + "\r\n"
                        + "Duis mauris mi, maximus a dolor vel, rhoncus maximus nulla. Nulla eu erat porta, pellentesque neque at, porta purus. Sed luctus purus est, non rhoncus libero accumsan ac. Vestibulum sit amet aliquet risus. Nam sollicitudin lacus id placerat suscipit. Maecenas et diam suscipit, fermentum ex nec, lacinia orci. Suspendisse posuere fringilla tortor. In efficitur leo nulla, et tristique mi euismod nec. Nunc ut leo pretium, tempus turpis eget, porttitor ligula. Phasellus congue euismod enim quis tincidunt. Curabitur id sollicitudin ante, non maximus est. Duis laoreet mattis felis sit amet laoreet. Fusce quis faucibus sem, vitae suscipit velit.\r\n"
                        + "\r\n"
                        + "Duis bibendum nulla id sapien sodales pretium. Nulla facilisi. Mauris at turpis orci. Pellentesque ac sem nibh. Fusce tristique vulputate diam a suscipit. Aenean venenatis mi quis libero convallis, eu tempor sapien placerat. Suspendisse hendrerit purus in felis pretium, nec congue quam posuere. In condimentum magna rutrum sagittis pulvinar. Aliquam vestibulum metus ut ex facilisis, a tempor odio mollis. Fusce et est ullamcorper, sagittis libero a, imperdiet eros. Maecenas bibendum felis vel nunc convallis ullamcorper. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc tristique justo non erat pulvinar, at semper elit ornare. Suspendisse ultrices ullamcorper sem eget fringilla.\r\n"
                        + "\r\n"
                        + "Phasellus at ante posuere, fringilla odio id, feugiat eros. Aliquam dignissim orci non magna dapibus, eu lacinia eros auctor. Aenean scelerisque ante nec pulvinar convallis. Etiam ut eros lectus. In sit amet aliquet enim, vel egestas lacus. Fusce porttitor tortor aliquam, hendrerit dui eu, sagittis nisi. Donec ipsum tortor, blandit et gravida at, feugiat at velit. Ut a hendrerit justo. Morbi vestibulum orci suscipit egestas mattis. Donec convallis quis mi nec pharetra. Phasellus volutpat, lectus at volutpat accumsan, purus turpis vestibulum enim, ornare tempus ante neque non nisi. Sed laoreet condimentum dolor, in euismod orci aliquam sit amet. Donec a elementum turpis. Morbi posuere tempor enim sit amet hendrerit. Phasellus laoreet dictum egestas.\r\n"
                        + "\r\n"
                        + "Vivamus sollicitudin maximus est, a scelerisque nulla aliquam sed. Vivamus varius ut lorem sit amet pulvinar. Pellentesque mauris orci, ultricies eu elementum in, finibus ac augue. Morbi arcu augue, lacinia vel augue ut, condimentum pretium eros. Curabitur quis velit purus. Duis accumsan ex at nunc consequat, et volutpat nibh pretium. Vivamus egestas blandit dolor. Donec rhoncus euismod enim, id scelerisque orci eleifend eget. Pellentesque tristique nisi condimentum diam dictum volutpat. Sed malesuada elit enim. Quisque ullamcorper dolor dolor, malesuada sodales nisl rhoncus nec. In semper laoreet nisi, eget porta felis condimentum quis. Aliquam tempus odio nec ligula placerat, et imperdiet quam egestas. Mauris tortor sapien, suscipit ut dignissim quis, egestas eget sapien.\r\n"
                        + "\r\n"
                        + "Donec a sem diam. Nam aliquet fermentum tortor, ut vehicula enim imperdiet quis. Suspendisse interdum sem et purus vestibulum laoreet. Aliquam nibh erat, fermentum sed dui non, congue vestibulum ligula. Vestibulum tincidunt lobortis fringilla. Maecenas volutpat nisi nec arcu mattis pellentesque ac non massa. Suspendisse viverra vulputate ligula at vulputate. Duis nec enim justo. Aenean id est in odio aliquet posuere sit amet ut lorem. Maecenas vulputate sapien ac nulla varius viverra. Aenean metus metus, tempor ut metus mattis, facilisis rutrum quam. Donec convallis tincidunt ante ac tempor. Duis euismod neque urna, id dictum dolor aliquet et. Ut cursus at nisl et laoreet.\r\n"
                        + "\r\n"
                        + "Morbi ut leo pretium, dictum ante non, facilisis nisi. Praesent porta, nunc ut ultricies semper, neque risus ullamcorper erat, sit amet elementum ex augue ut purus. Aliquam feugiat diam felis, ut faucibus nisl rutrum vitae. Sed dolor tellus, pretium a lectus eu, semper vestibulum enim. Proin pretium lobortis dictum. Vivamus purus elit, suscipit quis lectus ut, ultricies imperdiet dolor. Sed pharetra, eros eget maximus pellentesque, lacus justo fringilla arcu, at vehicula turpis ipsum vitae lectus. Aliquam sodales porta elit, vel aliquam orci consequat eget. Donec mi erat, condimentum quis dolor non, porttitor luctus lectus. Phasellus efficitur ligula quam, eget malesuada felis interdum semper. Aenean mollis venenatis ipsum."
                )
                .hasTransfer(true)
                .biomassEmissions(ProcedureOptionalForm.builder()
                        .exist(true)
                        .procedureForm(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                .procedureReference("Procedure Reference")
                                .procedureDocumentName("Procedure Document Name")
                                .itSystemUsed("System Used")
                                .appliedStandards("Applied Standards")
                                .diagramReference("Diagram Reference")
                                .locationOfRecords("Location of Records")
                                .responsibleDepartmentOrRole("Responsible department")
                                .build()).build()
                )
                .corroboratingCalculations(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .emissionDetermination(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .referencePeriodDetermination(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .gasFlowCalculation(ProcedureOptionalForm.builder()
                        .exist(true)
                        .procedureForm(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                .procedureReference("Procedure Reference")
                                .procedureDocumentName("Procedure Document Name")
                                .itSystemUsed("System Used")
                                .appliedStandards("Applied Standards")
                                .diagramReference("Diagram Reference")
                                .locationOfRecords("Location of Records")
                                .responsibleDepartmentOrRole("Responsible department")
                                .build())
                        .build())
                .emissionPointCategoryAppliedTiers(List.of(
                        MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
                                .emissionPointCategory(MeasurementOfCO2EmissionPointCategory.builder()
                                        .emissionPoint("ep1")
                                        .sourceStreams(Set.of("ss1", "ss2"))
                                        .categoryType(CategoryType.MINOR)
                                        .annualEmittedCO2Tonnes(BigDecimal.ONE)
                                        .emissionSources(Set.of("es1", "es2"))
                                        .transfer(TransferCO2.builder()
                                                .transferDirection(TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION)
                                                .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
                                                .installationEmitter(InstallationEmitter.builder()
                                                        .emitterId("EmitterId")
                                                        .email("test@test.com")
                                                        .build())
                                                .entryAccountingForTransfer(true)
                                                .build())
                                        .build())
                                .appliedStandard(AppliedStandard.builder()
                                        .laboratory(Laboratory.builder()
                                                .laboratoryName("Laboratory Name")
                                                .laboratoryAccredited(Boolean.FALSE)
                                                .laboratoryAccreditationEvidence("Some Evidence")
                                                .build())
                                        .appliedStandard("Some standard")
                                        .parameter("Some parameter")
                                        .deviationFromAppliedStandardExist(true)
                                        .deviationFromAppliedStandardDetails("Some details")
                                        .build())
                                .measuredEmissions(MeasurementOfCO2MeasuredEmissions.builder()
                                        .measurementDevicesOrMethods(Set.of("md id 1", "md id 2"))
                                        .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_1)
                                        .samplingFrequency(MeasuredEmissionsSamplingFrequency.OTHER)
                                        .otherSamplingFrequency("Some Other Frequency")
                                        .highestRequiredTier(HighestRequiredTier.builder()
                                                .isHighestRequiredTier(Boolean.FALSE)
                                                .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                                                        .isTechnicallyInfeasible(Boolean.TRUE)
                                                        .technicalInfeasibilityExplanation("Some Explanation")
                                                        .files(attachments)
                                                        .build())
                                                .build())
                                        .build())
                                .build()
                ))
                .build();
    }

    private MeasurementOfN2OMonitoringApproach createN2OMonitoringApproach(Set<UUID> attachments) {
        return MeasurementOfN2OMonitoringApproach.builder()
                .type(MonitoringApproachType.MEASUREMENT_N2O)
                .approachDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.\r\n"
                        + "\r\n"
                        + "In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.\r\n"
                        + "\r\n"
                        + "Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.\r\n"
                        + "\r\n"
                        + "Vestibulum finibus consectetur velit, sed laoreet neque varius nec. Suspendisse mollis ipsum vitae dui suscipit, vel tristique mi finibus. In viverra cursus sem, non tincidunt erat viverra finibus. In viverra dictum sem id blandit. Aliquam arcu erat, dapibus vel risus id, tincidunt ultricies quam. Curabitur quis ultricies felis. Nullam gravida eros metus, non ultrices tellus eleifend sit amet. Nulla sagittis facilisis malesuada. Vestibulum blandit, sapien vel luctus posuere, neque tortor laoreet velit, sed tristique eros nisi eget justo. Vivamus ac eros at enim molestie viverra vel nec orci. In rhoncus mauris eget nisi porta, ut tristique est imperdiet. Vestibulum feugiat nec nulla eget consequat.\r\n"
                        + "\r\n"
                        + "Duis mauris mi, maximus a dolor vel, rhoncus maximus nulla. Nulla eu erat porta, pellentesque neque at, porta purus. Sed luctus purus est, non rhoncus libero accumsan ac. Vestibulum sit amet aliquet risus. Nam sollicitudin lacus id placerat suscipit. Maecenas et diam suscipit, fermentum ex nec, lacinia orci. Suspendisse posuere fringilla tortor. In efficitur leo nulla, et tristique mi euismod nec. Nunc ut leo pretium, tempus turpis eget, porttitor ligula. Phasellus congue euismod enim quis tincidunt. Curabitur id sollicitudin ante, non maximus est. Duis laoreet mattis felis sit amet laoreet. Fusce quis faucibus sem, vitae suscipit velit.\r\n"
                        + "\r\n"
                        + "Duis bibendum nulla id sapien sodales pretium. Nulla facilisi. Mauris at turpis orci. Pellentesque ac sem nibh. Fusce tristique vulputate diam a suscipit. Aenean venenatis mi quis libero convallis, eu tempor sapien placerat. Suspendisse hendrerit purus in felis pretium, nec congue quam posuere. In condimentum magna rutrum sagittis pulvinar. Aliquam vestibulum metus ut ex facilisis, a tempor odio mollis. Fusce et est ullamcorper, sagittis libero a, imperdiet eros. Maecenas bibendum felis vel nunc convallis ullamcorper. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc tristique justo non erat pulvinar, at semper elit ornare. Suspendisse ultrices ullamcorper sem eget fringilla.\r\n"
                        + "\r\n"
                        + "Phasellus at ante posuere, fringilla odio id, feugiat eros. Aliquam dignissim orci non magna dapibus, eu lacinia eros auctor. Aenean scelerisque ante nec pulvinar convallis. Etiam ut eros lectus. In sit amet aliquet enim, vel egestas lacus. Fusce porttitor tortor aliquam, hendrerit dui eu, sagittis nisi. Donec ipsum tortor, blandit et gravida at, feugiat at velit. Ut a hendrerit justo. Morbi vestibulum orci suscipit egestas mattis. Donec convallis quis mi nec pharetra. Phasellus volutpat, lectus at volutpat accumsan, purus turpis vestibulum enim, ornare tempus ante neque non nisi. Sed laoreet condimentum dolor, in euismod orci aliquam sit amet. Donec a elementum turpis. Morbi posuere tempor enim sit amet hendrerit. Phasellus laoreet dictum egestas.\r\n"
                        + "\r\n"
                        + "Vivamus sollicitudin maximus est, a scelerisque nulla aliquam sed. Vivamus varius ut lorem sit amet pulvinar. Pellentesque mauris orci, ultricies eu elementum in, finibus ac augue. Morbi arcu augue, lacinia vel augue ut, condimentum pretium eros. Curabitur quis velit purus. Duis accumsan ex at nunc consequat, et volutpat nibh pretium. Vivamus egestas blandit dolor. Donec rhoncus euismod enim, id scelerisque orci eleifend eget. Pellentesque tristique nisi condimentum diam dictum volutpat. Sed malesuada elit enim. Quisque ullamcorper dolor dolor, malesuada sodales nisl rhoncus nec. In semper laoreet nisi, eget porta felis condimentum quis. Aliquam tempus odio nec ligula placerat, et imperdiet quam egestas. Mauris tortor sapien, suscipit ut dignissim quis, egestas eget sapien.\r\n"
                        + "\r\n"
                        + "Donec a sem diam. Nam aliquet fermentum tortor, ut vehicula enim imperdiet quis. Suspendisse interdum sem et purus vestibulum laoreet. Aliquam nibh erat, fermentum sed dui non, congue vestibulum ligula. Vestibulum tincidunt lobortis fringilla. Maecenas volutpat nisi nec arcu mattis pellentesque ac non massa. Suspendisse viverra vulputate ligula at vulputate. Duis nec enim justo. Aenean id est in odio aliquet posuere sit amet ut lorem. Maecenas vulputate sapien ac nulla varius viverra. Aenean metus metus, tempor ut metus mattis, facilisis rutrum quam. Donec convallis tincidunt ante ac tempor. Duis euismod neque urna, id dictum dolor aliquet et. Ut cursus at nisl et laoreet.\r\n"
                        + "\r\n"
                        + "Morbi ut leo pretium, dictum ante non, facilisis nisi. Praesent porta, nunc ut ultricies semper, neque risus ullamcorper erat, sit amet elementum ex augue ut purus. Aliquam feugiat diam felis, ut faucibus nisl rutrum vitae. Sed dolor tellus, pretium a lectus eu, semper vestibulum enim. Proin pretium lobortis dictum. Vivamus purus elit, suscipit quis lectus ut, ultricies imperdiet dolor. Sed pharetra, eros eget maximus pellentesque, lacus justo fringilla arcu, at vehicula turpis ipsum vitae lectus. Aliquam sodales porta elit, vel aliquam orci consequat eget. Donec mi erat, condimentum quis dolor non, porttitor luctus lectus. Phasellus efficitur ligula quam, eget malesuada felis interdum semper. Aenean mollis venenatis ipsum."
                )
                .hasTransfer(false)
                .emissionDetermination(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .gasFlowCalculation(ProcedureOptionalForm.builder()
                        .exist(true)
                        .procedureForm(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                .procedureReference("Procedure Reference")
                                .procedureDocumentName("Procedure Document Name")
                                .itSystemUsed("System Used")
                                .appliedStandards("Applied Standards")
                                .diagramReference("Diagram Reference")
                                .locationOfRecords("Location of Records")
                                .responsibleDepartmentOrRole("Responsible department")
                                .build())
                        .build())
                .nitrousOxideConcentrationDetermination(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .nitrousOxideEmissionsDetermination(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .operationalManagement(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .quantityMaterials(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .quantityProductDetermination(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .referenceDetermination(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .emissionPointCategoryAppliedTiers(List.of(
                        MeasurementOfN2OEmissionPointCategoryAppliedTier.builder()
                                .emissionPointCategory(MeasurementOfN2OEmissionPointCategory.builder()
                                        .emissionPoint("ep1")
                                        .emissionType(MeasurementOfN2OEmissionType.ABATED)
                                        .sourceStreams(Set.of("ss1", "ss2"))
                                        .categoryType(CategoryType.MINOR)
                                        .annualEmittedCO2Tonnes(BigDecimal.ONE)
                                        .monitoringApproachType(MeasurementOfN2OMonitoringApproachType.MEASUREMENT)
                                        .emissionSources(Set.of("es1", "es2"))
                                        .build())
                                .measuredEmissions(MeasurementOfN2OMeasuredEmissions.builder()
                                        .measurementDevicesOrMethods(Set.of("md id 1", "md id 2"))
                                        .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_1)
                                        .samplingFrequency(MeasuredEmissionsSamplingFrequency.OTHER)
                                        .otherSamplingFrequency("Some Other Frequency")
                                        .highestRequiredTier(HighestRequiredTier.builder()
                                                .isHighestRequiredTier(Boolean.FALSE)
                                                .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                                                        .isTechnicallyInfeasible(Boolean.TRUE)
                                                        .technicalInfeasibilityExplanation("Some Explanation")
                                                        .files(attachments)
                                                        .build())
                                                .build())
                                        .build())
                                .appliedStandard(AppliedStandard.builder()
                                        .laboratory(Laboratory.builder()
                                                .laboratoryName("Laboratory Name")
                                                .laboratoryAccredited(Boolean.FALSE)
                                                .laboratoryAccreditationEvidence("Some Evidence")
                                                .build())
                                        .appliedStandard("Some standard")
                                        .parameter("Some parameter")
                                        .deviationFromAppliedStandardExist(true)
                                        .deviationFromAppliedStandardDetails("Some details")
                                        .build())
                                .build()
                ))
                .build();
    }

    private TransferredCO2AndN2OMonitoringApproach createTransferCO2MonitoringApproach() {
        return TransferredCO2AndN2OMonitoringApproach.builder()
                .type(MonitoringApproachType.TRANSFERRED_CO2_N2O)
                .deductionsToAmountOfTransferredCO2(ProcedureOptionalForm.builder()
                        .exist(true)
                        .procedureForm(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                .procedureReference("Procedure Reference")
                                .procedureDocumentName("Procedure Document Name")
                                .itSystemUsed("System Used")
                                .appliedStandards("Applied Standards")
                                .diagramReference("Diagram Reference")
                                .locationOfRecords("Location of Records")
                                .responsibleDepartmentOrRole("Responsible department")
                                .build())
                        .build())
                .geologicalStorage(ProcedureOptionalForm.builder()
                        .exist(true)
                        .procedureForm(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                .procedureReference("Procedure Reference")
                                .procedureDocumentName("Procedure Document Name")
                                .itSystemUsed("System Used")
                                .appliedStandards("Applied Standards")
                                .diagramReference("Diagram Reference")
                                .locationOfRecords("Location of Records")
                                .responsibleDepartmentOrRole("Responsible department")
                                .build())
                        .build())
                .transportCO2AndN2OPipelineSystems(TransportCO2AndN2OPipelineSystems.builder()
                        .exist(true)
                        .proceduresForTransferredCO2AndN2O(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                .procedureReference("Procedure Reference")
                                .procedureDocumentName("Procedure Document Name")
                                .itSystemUsed("System Used")
                                .appliedStandards("Applied Standards")
                                .diagramReference("Diagram Reference")
                                .locationOfRecords("Location of Records")
                                .responsibleDepartmentOrRole("Responsible department")
                                .build())
                        .procedureForLeakageEvents(ProcedureOptionalForm.builder()
                                .exist(true)
                                .procedureForm(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                        .procedureReference("Procedure Reference")
                                        .procedureDocumentName("Procedure Document Name")
                                        .itSystemUsed("System Used")
                                        .appliedStandards("Applied Standards")
                                        .diagramReference("Diagram Reference")
                                        .locationOfRecords("Location of Records")
                                        .responsibleDepartmentOrRole("Responsible department")
                                        .build())
                                .build())
                        .temperaturePressure(TemperaturePressure.builder()
                                .exist(true)
                                .measurementDevices(List.of(
                                        MeasurementDevice.builder()
                                                .reference("MD Reference")
                                                .type(MeasurementDeviceType.BALANCE)
                                                .location("Some Location")
                                                .build()
                                ))
                                .build())
                        .build())
                .build();
    }

    private CalculationOfCO2MonitoringApproach createCalculationMonitoringApproach(UUID procedurePlanId1, UUID procedurePlanId2) {
        return CalculationOfCO2MonitoringApproach.builder()
                .approachDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.\r\n"
                        + "\r\n"
                        + "In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.\r\n"
                        + "\r\n"
                        + "Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.\r\n"
                        + "\r\n"
                        + "Vestibulum finibus consectetur velit, sed laoreet neque varius nec. Suspendisse mollis ipsum vitae dui suscipit, vel tristique mi finibus. In viverra cursus sem, non tincidunt erat viverra finibus. In viverra dictum sem id blandit. Aliquam arcu erat, dapibus vel risus id, tincidunt ultricies quam. Curabitur quis ultricies felis. Nullam gravida eros metus, non ultrices tellus eleifend sit amet. Nulla sagittis facilisis malesuada. Vestibulum blandit, sapien vel luctus posuere, neque tortor laoreet velit, sed tristique eros nisi eget justo. Vivamus ac eros at enim molestie viverra vel nec orci. In rhoncus mauris eget nisi porta, ut tristique est imperdiet. Vestibulum feugiat nec nulla eget consequat.\r\n"
                        + "\r\n"
                        + "Duis mauris mi, maximus a dolor vel, rhoncus maximus nulla. Nulla eu erat porta, pellentesque neque at, porta purus. Sed luctus purus est, non rhoncus libero accumsan ac. Vestibulum sit amet aliquet risus. Nam sollicitudin lacus id placerat suscipit. Maecenas et diam suscipit, fermentum ex nec, lacinia orci. Suspendisse posuere fringilla tortor. In efficitur leo nulla, et tristique mi euismod nec. Nunc ut leo pretium, tempus turpis eget, porttitor ligula. Phasellus congue euismod enim quis tincidunt. Curabitur id sollicitudin ante, non maximus est. Duis laoreet mattis felis sit amet laoreet. Fusce quis faucibus sem, vitae suscipit velit.\r\n"
                        + "\r\n"
                        + "Duis bibendum nulla id sapien sodales pretium. Nulla facilisi. Mauris at turpis orci. Pellentesque ac sem nibh. Fusce tristique vulputate diam a suscipit. Aenean venenatis mi quis libero convallis, eu tempor sapien placerat. Suspendisse hendrerit purus in felis pretium, nec congue quam posuere. In condimentum magna rutrum sagittis pulvinar. Aliquam vestibulum metus ut ex facilisis, a tempor odio mollis. Fusce et est ullamcorper, sagittis libero a, imperdiet eros. Maecenas bibendum felis vel nunc convallis ullamcorper. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc tristique justo non erat pulvinar, at semper elit ornare. Suspendisse ultrices ullamcorper sem eget fringilla.\r\n"
                        + "\r\n"
                        + "Phasellus at ante posuere, fringilla odio id, feugiat eros. Aliquam dignissim orci non magna dapibus, eu lacinia eros auctor. Aenean scelerisque ante nec pulvinar convallis. Etiam ut eros lectus. In sit amet aliquet enim, vel egestas lacus. Fusce porttitor tortor aliquam, hendrerit dui eu, sagittis nisi. Donec ipsum tortor, blandit et gravida at, feugiat at velit. Ut a hendrerit justo. Morbi vestibulum orci suscipit egestas mattis. Donec convallis quis mi nec pharetra. Phasellus volutpat, lectus at volutpat accumsan, purus turpis vestibulum enim, ornare tempus ante neque non nisi. Sed laoreet condimentum dolor, in euismod orci aliquam sit amet. Donec a elementum turpis. Morbi posuere tempor enim sit amet hendrerit. Phasellus laoreet dictum egestas.\r\n"
                        + "\r\n"
                        + "Vivamus sollicitudin maximus est, a scelerisque nulla aliquam sed. Vivamus varius ut lorem sit amet pulvinar. Pellentesque mauris orci, ultricies eu elementum in, finibus ac augue. Morbi arcu augue, lacinia vel augue ut, condimentum pretium eros. Curabitur quis velit purus. Duis accumsan ex at nunc consequat, et volutpat nibh pretium. Vivamus egestas blandit dolor. Donec rhoncus euismod enim, id scelerisque orci eleifend eget. Pellentesque tristique nisi condimentum diam dictum volutpat. Sed malesuada elit enim. Quisque ullamcorper dolor dolor, malesuada sodales nisl rhoncus nec. In semper laoreet nisi, eget porta felis condimentum quis. Aliquam tempus odio nec ligula placerat, et imperdiet quam egestas. Mauris tortor sapien, suscipit ut dignissim quis, egestas eget sapien.\r\n"
                        + "\r\n"
                        + "Donec a sem diam. Nam aliquet fermentum tortor, ut vehicula enim imperdiet quis. Suspendisse interdum sem et purus vestibulum laoreet. Aliquam nibh erat, fermentum sed dui non, congue vestibulum ligula. Vestibulum tincidunt lobortis fringilla. Maecenas volutpat nisi nec arcu mattis pellentesque ac non massa. Suspendisse viverra vulputate ligula at vulputate. Duis nec enim justo. Aenean id est in odio aliquet posuere sit amet ut lorem. Maecenas vulputate sapien ac nulla varius viverra. Aenean metus metus, tempor ut metus mattis, facilisis rutrum quam. Donec convallis tincidunt ante ac tempor. Duis euismod neque urna, id dictum dolor aliquet et. Ut cursus at nisl et laoreet.\r\n"
                        + "\r\n"
                        + "Morbi ut leo pretium, dictum ante non, facilisis nisi. Praesent porta, nunc ut ultricies semper, neque risus ullamcorper erat, sit amet elementum ex augue ut purus. Aliquam feugiat diam felis, ut faucibus nisl rutrum vitae. Sed dolor tellus, pretium a lectus eu, semper vestibulum enim. Proin pretium lobortis dictum. Vivamus purus elit, suscipit quis lectus ut, ultricies imperdiet dolor. Sed pharetra, eros eget maximus pellentesque, lacus justo fringilla arcu, at vehicula turpis ipsum vitae lectus. Aliquam sodales porta elit, vel aliquam orci consequat eget. Donec mi erat, condimentum quis dolor non, porttitor luctus lectus. Phasellus efficitur ligula quam, eget malesuada felis interdum semper. Aenean mollis venenatis ipsum."
                )
                .type(MonitoringApproachType.CALCULATION_CO2)
                .sourceStreamCategoryAppliedTiers(List.of(
                        CalculationSourceStreamCategoryAppliedTier.builder()
                                .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                                        .sourceStream("ss1")
                                        .emissionSources(Set.of("es2", "es1"))
                                        .categoryType(CategoryType.DE_MINIMIS)
                                        .build())
                                .activityData(CalculationActivityData.builder()
                                        .tier(CalculationActivityDataTier.TIER_3)
                                        .measurementDevicesOrMethods(Set.of("md id 1", "md id 2"))
                                        .uncertainty(MeteringUncertainty.LESS_OR_EQUAL_5_0)
                                        .build())
                                .netCalorificValue(CalculationNetCalorificValue.builder()
                                        .defaultValueApplied(true)
                                        .standardReferenceSource(
                                                CalculationNetCalorificValueStandardReferenceSource.builder()
                                                        .type(
                                                                CalculationNetCalorificValueStandardReferenceSourceType.CONSERVATIVE_ESTIMATION)
                                                        .defaultValue("net cal value")
                                                        .build())
                                        .tier(CalculationNetCalorificValueTier.TIER_1).build())
                                .emissionFactor(CalculationEmissionFactor.builder()
                                        .defaultValueApplied(true)
                                        .standardReferenceSource(CalculationEmissionFactorStandardReferenceSource.builder()
                                                .type(
                                                        CalculationEmissionFactorStandardReferenceSourceType.BRITISH_CERAMIC_CONFEDERATION)
                                                .defaultValue("ef value")
                                                .build())
                                        .tier(CalculationEmissionFactorTier.TIER_1).build())
                                .oxidationFactor(CalculationOxidationFactor.builder()
                                        .defaultValueApplied(false)
                                        .tier(CalculationOxidationFactorTier.TIER_3).build())
                                .carbonContent(CalculationCarbonContent.builder()
                                        .defaultValueApplied(false)
                                        .tier(CalculationCarbonContentTier.TIER_1).build())
                                .conversionFactor(CalculationConversionFactor.builder()
                                        .exist(false).build())
                                .biomassFraction(CalculationBiomassFraction.builder()
                                        .exist(false).build())
                                .build()
                ))
                .samplingPlan(SamplingPlan.builder()
                        .exist(true)
                        .details(SamplingPlanDetails.builder()
                                .analysis(ProcedureForm.builder()
                                        .procedureDescription("analysis procedureDescription\r\n"
                                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                        .procedureDocumentName("analysis procedureDocumentName")
                                        .procedureReference("analysis procedureReference")
                                        .diagramReference("analysis diagramReference")
                                        .responsibleDepartmentOrRole("analysis responsibleDepartmentOrRole")
                                        .locationOfRecords("analysis locationOfRecords")
                                        .itSystemUsed("analysis itSystemUsed")
                                        .appliedStandards("analysis appliedStandards")
                                        .build())
                                .procedurePlan(ProcedurePlan.builder()
                                        .procedureDescription("procedurePlan procedureDescription\r\n"
                                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                        .procedureDocumentName("procedurePlan procedureDocumentName")
                                        .procedureReference("procedurePlan procedureReference")
                                        .diagramReference("procedurePlan diagramReference")
                                        .responsibleDepartmentOrRole("procedurePlan responsibleDepartmentOrRole")
                                        .locationOfRecords("procedurePlan locationOfRecords")
                                        .itSystemUsed("procedurePlan itSystemUsed")
                                        .appliedStandards("procedurePlan appliedStandards")
                                        .procedurePlanIds(Set.of(procedurePlanId1, procedurePlanId2))
                                        .build())
                                .appropriateness(ProcedureForm.builder()
                                        .procedureDescription("appropriateness procedureDescription\r\n"
                                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                        .procedureDocumentName("appropriateness procedureDocumentName")
                                        .procedureReference("appropriateness procedureReference")
                                        .diagramReference("appropriateness diagramReference")
                                        .responsibleDepartmentOrRole("appropriateness responsibleDepartmentOrRole")
                                        .locationOfRecords("appropriateness locationOfRecords")
                                        .itSystemUsed("appropriateness itSystemUsed")
                                        .appliedStandards("appropriateness appliedStandards")
                                        .build())
                                .yearEndReconciliation(ProcedureOptionalForm.builder()
                                        .exist(true)
                                        .procedureForm(ProcedureForm.builder()
                                                .procedureDescription("yearEndReconciliation procedureDescription\r\n"
                                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                                .procedureDocumentName("yearEndReconciliation procedureDocumentName")
                                                .procedureReference("yearEndReconciliation procedureReference")
                                                .diagramReference("yearEndReconciliation diagramReference")
                                                .responsibleDepartmentOrRole("yearEndReconciliation responsibleDepartmentOrRole")
                                                .locationOfRecords("yearEndReconciliation locationOfRecords")
                                                .itSystemUsed("yearEndReconciliation itSystemUsed")
                                                .appliedStandards("yearEndReconciliation appliedStandards")
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    private InherentCO2MonitoringApproach createInherentMonitoringApproach() {
        return InherentCO2MonitoringApproach.builder()
                .type(MonitoringApproachType.INHERENT_CO2)
                .inherentReceivingTransferringInstallations(List.of(
                        InherentReceivingTransferringInstallation.builder()
                                .installationDetailsType(InstallationDetailsType.INSTALLATION_DETAILS)
                                .totalEmissions(BigDecimal.TEN)
                                .inherentCO2Direction(InherentCO2Direction.EXPORTED_TO_ETS_INSTALLATION)
                                .inherentReceivingTransferringInstallationDetailsType(
                                        InherentReceivingTransferringInstallationDetails.builder()
                                                .installationDetails(InstallationDetails.builder()
                                                        .line1("line1")
                                                        .line2("line2")
                                                        .city("City")
                                                        .postcode("Postcode")
                                                        .email("test@test.com")
                                                        .installationName("Installation Name")
                                                        .build())
                                                .build()
                                )
                                .build(),
                        InherentReceivingTransferringInstallation.builder()
                                .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
                                .totalEmissions(BigDecimal.TEN)
                                .inherentCO2Direction(InherentCO2Direction.EXPORTED_TO_ETS_INSTALLATION)
                                .inherentReceivingTransferringInstallationDetailsType(
                                        InherentReceivingTransferringInstallationEmitter.builder()
                                                .installationEmitter(InstallationEmitter.builder()
                                                        .email("test@test.com")
                                                        .emitterId("EmitterId")
                                                        .build())
                                                .build()
                                )
                                .build()
                ))
                .build();
    }

    private FallbackMonitoringApproach createFallbackMonitoringApproach() {
        return FallbackMonitoringApproach.builder()
                .approachDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.\r\n"
                        + "\r\n"
                        + "In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.\r\n"
                        + "\r\n"
                        + "Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.\r\n"
                        + "\r\n"
                        + "Vestibulum finibus consectetur velit, sed laoreet neque varius nec. Suspendisse mollis ipsum vitae dui suscipit, vel tristique mi finibus. In viverra cursus sem, non tincidunt erat viverra finibus. In viverra dictum sem id blandit. Aliquam arcu erat, dapibus vel risus id, tincidunt ultricies quam. Curabitur quis ultricies felis. Nullam gravida eros metus, non ultrices tellus eleifend sit amet. Nulla sagittis facilisis malesuada. Vestibulum blandit, sapien vel luctus posuere, neque tortor laoreet velit, sed tristique eros nisi eget justo. Vivamus ac eros at enim molestie viverra vel nec orci. In rhoncus mauris eget nisi porta, ut tristique est imperdiet. Vestibulum feugiat nec nulla eget consequat.\r\n"
                        + "\r\n"
                        + "Duis mauris mi, maximus a dolor vel, rhoncus maximus nulla. Nulla eu erat porta, pellentesque neque at, porta purus. Sed luctus purus est, non rhoncus libero accumsan ac. Vestibulum sit amet aliquet risus. Nam sollicitudin lacus id placerat suscipit. Maecenas et diam suscipit, fermentum ex nec, lacinia orci. Suspendisse posuere fringilla tortor. In efficitur leo nulla, et tristique mi euismod nec. Nunc ut leo pretium, tempus turpis eget, porttitor ligula. Phasellus congue euismod enim quis tincidunt. Curabitur id sollicitudin ante, non maximus est. Duis laoreet mattis felis sit amet laoreet. Fusce quis faucibus sem, vitae suscipit velit.\r\n"
                        + "\r\n"
                        + "Duis bibendum nulla id sapien sodales pretium. Nulla facilisi. Mauris at turpis orci. Pellentesque ac sem nibh. Fusce tristique vulputate diam a suscipit. Aenean venenatis mi quis libero convallis, eu tempor sapien placerat. Suspendisse hendrerit purus in felis pretium, nec congue quam posuere. In condimentum magna rutrum sagittis pulvinar. Aliquam vestibulum metus ut ex facilisis, a tempor odio mollis. Fusce et est ullamcorper, sagittis libero a, imperdiet eros. Maecenas bibendum felis vel nunc convallis ullamcorper. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc tristique justo non erat pulvinar, at semper elit ornare. Suspendisse ultrices ullamcorper sem eget fringilla.\r\n"
                        + "\r\n"
                        + "Phasellus at ante posuere, fringilla odio id, feugiat eros. Aliquam dignissim orci non magna dapibus, eu lacinia eros auctor. Aenean scelerisque ante nec pulvinar convallis. Etiam ut eros lectus. In sit amet aliquet enim, vel egestas lacus. Fusce porttitor tortor aliquam, hendrerit dui eu, sagittis nisi. Donec ipsum tortor, blandit et gravida at, feugiat at velit. Ut a hendrerit justo. Morbi vestibulum orci suscipit egestas mattis. Donec convallis quis mi nec pharetra. Phasellus volutpat, lectus at volutpat accumsan, purus turpis vestibulum enim, ornare tempus ante neque non nisi. Sed laoreet condimentum dolor, in euismod orci aliquam sit amet. Donec a elementum turpis. Morbi posuere tempor enim sit amet hendrerit. Phasellus laoreet dictum egestas.\r\n"
                        + "\r\n"
                        + "Vivamus sollicitudin maximus est, a scelerisque nulla aliquam sed. Vivamus varius ut lorem sit amet pulvinar. Pellentesque mauris orci, ultricies eu elementum in, finibus ac augue. Morbi arcu augue, lacinia vel augue ut, condimentum pretium eros. Curabitur quis velit purus. Duis accumsan ex at nunc consequat, et volutpat nibh pretium. Vivamus egestas blandit dolor. Donec rhoncus euismod enim, id scelerisque orci eleifend eget. Pellentesque tristique nisi condimentum diam dictum volutpat. Sed malesuada elit enim. Quisque ullamcorper dolor dolor, malesuada sodales nisl rhoncus nec. In semper laoreet nisi, eget porta felis condimentum quis. Aliquam tempus odio nec ligula placerat, et imperdiet quam egestas. Mauris tortor sapien, suscipit ut dignissim quis, egestas eget sapien.\r\n"
                        + "\r\n"
                        + "Donec a sem diam. Nam aliquet fermentum tortor, ut vehicula enim imperdiet quis. Suspendisse interdum sem et purus vestibulum laoreet. Aliquam nibh erat, fermentum sed dui non, congue vestibulum ligula. Vestibulum tincidunt lobortis fringilla. Maecenas volutpat nisi nec arcu mattis pellentesque ac non massa. Suspendisse viverra vulputate ligula at vulputate. Duis nec enim justo. Aenean id est in odio aliquet posuere sit amet ut lorem. Maecenas vulputate sapien ac nulla varius viverra. Aenean metus metus, tempor ut metus mattis, facilisis rutrum quam. Donec convallis tincidunt ante ac tempor. Duis euismod neque urna, id dictum dolor aliquet et. Ut cursus at nisl et laoreet.\r\n"
                        + "\r\n"
                        + "Morbi ut leo pretium, dictum ante non, facilisis nisi. Praesent porta, nunc ut ultricies semper, neque risus ullamcorper erat, sit amet elementum ex augue ut purus. Aliquam feugiat diam felis, ut faucibus nisl rutrum vitae. Sed dolor tellus, pretium a lectus eu, semper vestibulum enim. Proin pretium lobortis dictum. Vivamus purus elit, suscipit quis lectus ut, ultricies imperdiet dolor. Sed pharetra, eros eget maximus pellentesque, lacus justo fringilla arcu, at vehicula turpis ipsum vitae lectus. Aliquam sodales porta elit, vel aliquam orci consequat eget. Donec mi erat, condimentum quis dolor non, porttitor luctus lectus. Phasellus efficitur ligula quam, eget malesuada felis interdum semper. Aenean mollis venenatis ipsum."
                )
                .type(MonitoringApproachType.FALLBACK)
                .sourceStreamCategoryAppliedTiers(List.of(
                        FallbackSourceStreamCategoryAppliedTier.builder()
                                .sourceStreamCategory(FallbackSourceStreamCategory.builder()
                                        .sourceStream("ss1")
                                        .emissionSources(Set.of("es2", "es1"))
                                        .categoryType(CategoryType.DE_MINIMIS)
                                        .measurementDevicesOrMethods(Set.of("md id 1", "md id 2"))
                                        .uncertainty(MeteringUncertainty.LESS_OR_EQUAL_5_0)
                                        .build())
                                .build()
                ))
                .justification("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\\r\\n\"\n" +
                        "                        + \"Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\\r\\n\"\n" +
                        "                        + \"Praesent finibus volutpat")
                .annualUncertaintyAnalysis(ProcedureForm.builder().procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .build();
    }

    private CalculationOfPFCMonitoringApproach createPfcMonitoringApproach() {
        return CalculationOfPFCMonitoringApproach.builder()
                .approachDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.\r\n"
                        + "\r\n"
                        + "In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.\r\n"
                        + "\r\n"
                        + "Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.\r\n"
                        + "\r\n"
                        + "Vestibulum finibus consectetur velit, sed laoreet neque varius nec. Suspendisse mollis ipsum vitae dui suscipit, vel tristique mi finibus. In viverra cursus sem, non tincidunt erat viverra finibus. In viverra dictum sem id blandit. Aliquam arcu erat, dapibus vel risus id, tincidunt ultricies quam. Curabitur quis ultricies felis. Nullam gravida eros metus, non ultrices tellus eleifend sit amet. Nulla sagittis facilisis malesuada. Vestibulum blandit, sapien vel luctus posuere, neque tortor laoreet velit, sed tristique eros nisi eget justo. Vivamus ac eros at enim molestie viverra vel nec orci. In rhoncus mauris eget nisi porta, ut tristique est imperdiet. Vestibulum feugiat nec nulla eget consequat.\r\n"
                        + "\r\n"
                        + "Duis mauris mi, maximus a dolor vel, rhoncus maximus nulla. Nulla eu erat porta, pellentesque neque at, porta purus. Sed luctus purus est, non rhoncus libero accumsan ac. Vestibulum sit amet aliquet risus. Nam sollicitudin lacus id placerat suscipit. Maecenas et diam suscipit, fermentum ex nec, lacinia orci. Suspendisse posuere fringilla tortor. In efficitur leo nulla, et tristique mi euismod nec. Nunc ut leo pretium, tempus turpis eget, porttitor ligula. Phasellus congue euismod enim quis tincidunt. Curabitur id sollicitudin ante, non maximus est. Duis laoreet mattis felis sit amet laoreet. Fusce quis faucibus sem, vitae suscipit velit.\r\n"
                        + "\r\n"
                        + "Duis bibendum nulla id sapien sodales pretium. Nulla facilisi. Mauris at turpis orci. Pellentesque ac sem nibh. Fusce tristique vulputate diam a suscipit. Aenean venenatis mi quis libero convallis, eu tempor sapien placerat. Suspendisse hendrerit purus in felis pretium, nec congue quam posuere. In condimentum magna rutrum sagittis pulvinar. Aliquam vestibulum metus ut ex facilisis, a tempor odio mollis. Fusce et est ullamcorper, sagittis libero a, imperdiet eros. Maecenas bibendum felis vel nunc convallis ullamcorper. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc tristique justo non erat pulvinar, at semper elit ornare. Suspendisse ultrices ullamcorper sem eget fringilla.\r\n"
                        + "\r\n"
                        + "Phasellus at ante posuere, fringilla odio id, feugiat eros. Aliquam dignissim orci non magna dapibus, eu lacinia eros auctor. Aenean scelerisque ante nec pulvinar convallis. Etiam ut eros lectus. In sit amet aliquet enim, vel egestas lacus. Fusce porttitor tortor aliquam, hendrerit dui eu, sagittis nisi. Donec ipsum tortor, blandit et gravida at, feugiat at velit. Ut a hendrerit justo. Morbi vestibulum orci suscipit egestas mattis. Donec convallis quis mi nec pharetra. Phasellus volutpat, lectus at volutpat accumsan, purus turpis vestibulum enim, ornare tempus ante neque non nisi. Sed laoreet condimentum dolor, in euismod orci aliquam sit amet. Donec a elementum turpis. Morbi posuere tempor enim sit amet hendrerit. Phasellus laoreet dictum egestas.\r\n"
                        + "\r\n"
                        + "Vivamus sollicitudin maximus est, a scelerisque nulla aliquam sed. Vivamus varius ut lorem sit amet pulvinar. Pellentesque mauris orci, ultricies eu elementum in, finibus ac augue. Morbi arcu augue, lacinia vel augue ut, condimentum pretium eros. Curabitur quis velit purus. Duis accumsan ex at nunc consequat, et volutpat nibh pretium. Vivamus egestas blandit dolor. Donec rhoncus euismod enim, id scelerisque orci eleifend eget. Pellentesque tristique nisi condimentum diam dictum volutpat. Sed malesuada elit enim. Quisque ullamcorper dolor dolor, malesuada sodales nisl rhoncus nec. In semper laoreet nisi, eget porta felis condimentum quis. Aliquam tempus odio nec ligula placerat, et imperdiet quam egestas. Mauris tortor sapien, suscipit ut dignissim quis, egestas eget sapien.\r\n"
                        + "\r\n"
                        + "Donec a sem diam. Nam aliquet fermentum tortor, ut vehicula enim imperdiet quis. Suspendisse interdum sem et purus vestibulum laoreet. Aliquam nibh erat, fermentum sed dui non, congue vestibulum ligula. Vestibulum tincidunt lobortis fringilla. Maecenas volutpat nisi nec arcu mattis pellentesque ac non massa. Suspendisse viverra vulputate ligula at vulputate. Duis nec enim justo. Aenean id est in odio aliquet posuere sit amet ut lorem. Maecenas vulputate sapien ac nulla varius viverra. Aenean metus metus, tempor ut metus mattis, facilisis rutrum quam. Donec convallis tincidunt ante ac tempor. Duis euismod neque urna, id dictum dolor aliquet et. Ut cursus at nisl et laoreet.\r\n"
                        + "\r\n"
                        + "Morbi ut leo pretium, dictum ante non, facilisis nisi. Praesent porta, nunc ut ultricies semper, neque risus ullamcorper erat, sit amet elementum ex augue ut purus. Aliquam feugiat diam felis, ut faucibus nisl rutrum vitae. Sed dolor tellus, pretium a lectus eu, semper vestibulum enim. Proin pretium lobortis dictum. Vivamus purus elit, suscipit quis lectus ut, ultricies imperdiet dolor. Sed pharetra, eros eget maximus pellentesque, lacus justo fringilla arcu, at vehicula turpis ipsum vitae lectus. Aliquam sodales porta elit, vel aliquam orci consequat eget. Donec mi erat, condimentum quis dolor non, porttitor luctus lectus. Phasellus efficitur ligula quam, eget malesuada felis interdum semper. Aenean mollis venenatis ipsum."
                )
                .type(MonitoringApproachType.CALCULATION_PFC)
                .sourceStreamCategoryAppliedTiers(List.of(
                        PFCSourceStreamCategoryAppliedTier.builder()
                                .activityData(PFCActivityData.builder()
                                        .highestRequiredTier(HighestRequiredTier.builder()
                                                .isHighestRequiredTier(true)
                                                .build())
                                        .build())
                                .emissionFactor(PFCEmissionFactor.builder()
                                        .tier(PFCEmissionFactorTier.TIER_2)
                                        .build())
                                .sourceStreamCategory(PFCSourceStreamCategory.builder()
                                        .sourceStream("ss1")
                                        .emissionSources(Set.of("es2", "es1"))
                                        .categoryType(CategoryType.DE_MINIMIS)
                                        .annualEmittedCO2Tonnes(BigDecimal.TEN)
                                        .emissionPoints(Set.of("ep1", "ep2"))
                                        .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                                        .build())
                                .build()
                ))
                .collectionEfficiency(ProcedureForm.builder()
                        .procedureDescription("Procedure Description\r\n"
                                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                        .procedureReference("Procedure Reference")
                        .procedureDocumentName("Procedure Document Name")
                        .itSystemUsed("System Used")
                        .appliedStandards("Applied Standards")
                        .diagramReference("Diagram Reference")
                        .locationOfRecords("Location of Records")
                        .responsibleDepartmentOrRole("Responsible department")
                        .build())
                .tier2EmissionFactor(PFCTier2EmissionFactor.builder()
                        .exist(true)
                        .determinationInstallation(ProcedureForm.builder()
                                .procedureDescription("Procedure Description\r\n"
                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                .procedureReference("Procedure Reference")
                                .procedureDocumentName("Procedure Document Name")
                                .itSystemUsed("System Used")
                                .appliedStandards("Applied Standards")
                                .diagramReference("Diagram Reference")
                                .locationOfRecords("Location of Records")
                                .responsibleDepartmentOrRole("Responsible department")
                                .build())
                        .scheduleMeasurements(ProcedureForm.builder()
                                .procedureDescription("Procedure Description\r\n"
                                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                                .procedureReference("Procedure Reference")
                                .procedureDocumentName("Procedure Document Name")
                                .itSystemUsed("System Used")
                                .appliedStandards("Applied Standards")
                                .diagramReference("Diagram Reference")
                                .locationOfRecords("Location of Records")
                                .responsibleDepartmentOrRole("Responsible department")
                                .build())
                        .build())
                .cellAndAnodeTypes(List.of(
                        CellAndAnodeType.builder()
                                .cellType("Some cell type")
                                .anodeType("Some ANode type")
                                .build()
                ))
                .build();
    }

    private ManagementProceduresDefinition buildManagementProceduresDefinition() {
        return ManagementProceduresDefinition.builder()
                .procedureDescription("Procedure Description\r\n"
                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                        + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                        + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
                .procedureReference("Procedure Reference")
                .procedureDocumentName("Procedure Document Name")
                .itSystemUsed("System Used")
                .appliedStandards("Applied Standards")
                .diagramReference("Diagram Reference")
                .locationOfRecords("Location of Records")
                .responsibleDepartmentOrRole("Responsible department")
                .build();
    }

    private AssessAndControlRisk buildAssessAndControlRisk(Set<UUID> assessAndControlRiskFiles) {
        return AssessAndControlRisk.builder()
            .procedureDescription("Procedure Description\r\n"
                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer vestibulum et tortor nec aliquam. Duis ac feugiat risus, sit amet laoreet urna. Duis consectetur quam non ex vestibulum consequat. Fusce in sem leo. Suspendisse venenatis eget mi at pulvinar. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer quis faucibus tortor. Maecenas feugiat posuere augue, a sagittis massa blandit non. Nunc interdum felis eget nunc semper porta. Integer consectetur, mi vitae feugiat interdum, nisi nulla bibendum metus, eget suscipit dui nulla vitae massa. Morbi quis dictum nunc. Vivamus luctus ante nunc, id accumsan ex bibendum eu. Suspendisse in ligula lectus.\r\n"
                + "Quisque arcu est, pellentesque sit amet tincidunt eu, luctus nec sem. Aliquam vulputate arcu sed justo venenatis luctus. Proin quis rutrum dolor, nec accumsan ex. Sed vestibulum, tellus ut euismod commodo, purus dui faucibus tellus, eu consectetur ipsum nisi ut elit. Nam sit amet dui non nunc viverra blandit ac a velit. Pellentesque elementum vulputate libero ut varius. Sed convallis tempus malesuada. Etiam eget fringilla erat. Aliquam a finibus dolor. Morbi scelerisque convallis vestibulum. Vestibulum suscipit lacus a ex fringilla, vitae facilisis nulla tempor. Nam porta sem id condimentum ornare. Phasellus non felis a urna tincidunt rhoncus sit amet eu augue.\r\n"
                + "Praesent finibus volutpat sem eu volutpat. Ut ullamcorper sapien ligula, suscipit ullamcorper velit tincidunt vel. Morbi vel volutpat risus, vel vulputate neque. Mauris lobortis sit amet lorem sed posuere. Morbi tincidunt, orci at viverra dapibus, leo ipsum hendrerit quam, vel facilisis justo augue eu nisl. Mauris molestie neque eu efficitur bibendum. Aliquam imperdiet aliquet aliquam. Donec lacus neque, fringilla vitae eros ac, vehicula tempus nunc. Nulla ut commodo purus. Phasellus eu interdum nibh, in mollis velit. Ut bibendum rutrum erat non posuere. Integer vitae porttitor enim, vitae consequat nisi. ")
            .procedureReference("Procedure Reference")
            .procedureDocumentName("Procedure Document Name")
            .itSystemUsed("System Used")
            .appliedStandards("Applied Standards")
            .diagramReference("Diagram Reference")
            .locationOfRecords("Location of Records")
            .responsibleDepartmentOrRole("Responsible department")
            .riskAssessmentAttachments(assessAndControlRiskFiles)
            .build();
    }

    private DataFlowActivities buildDataflowActivities(UUID diagramAttachmentId) {
        return DataFlowActivities.builder()
                .procedureDescription("Procedure Description")
                .procedureReference("Procedure Reference")
                .procedureDocumentName("Procedure Document Name")
                .itSystemUsed("System Used")
                .appliedStandards("Applied Standards")
                .diagramReference("Diagram Reference")
                .locationOfRecords("Location of Records")
                .responsibleDepartmentOrRole("Responsible department")
                .primaryDataSources("Primary data sources")
                .processingSteps("Processing steps")
                .diagramAttachmentId(diagramAttachmentId)
                .build();
    }


    private TemplateParams buildTemplateParamsForOfficialNotice(CompetentAuthorityEnum ca, String signatoryUser, FileDTO signatureFile,
                                               Map<String, Object> params) {
        CompetentAuthorityDTO caDto = CompetentAuthorityDTO.builder().id(ca).email("email").name("name").build();
        return TemplateParams.builder()
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(caDto)
                        .logo(PmrvCompetentAuthorityService.getCompetentAuthorityLogo(ca))
                        .build())
                .competentAuthorityCentralInfo("ca central info")
                .signatoryParams(SignatoryTemplateParams.builder()
                        .fullName(signatoryUser)
                        .signature(signatureFile.getFileContent())
                        .jobTitle("Project Manager")
                        .build())
                .accountParams(AviationAccountTemplateParams.builder()
                        .name("account name")
                        .primaryContact("primary contact")
                        .primaryContactEmail("primary contact email")
                        .serviceContact("service contact")
                        .serviceContactEmail("service contact email")
                        .crcoCode("CRCO code")
                        .build())
                .permitId("UK-E-IN-12345")
                .workflowParams(WorkflowTemplateParams.builder()
                        .requestId("123")
                        .requestType("PERMIT_VARIATION") //("PERMIT_ISSUANCE")
                        .requestTypeInfo("your permit variation")
                        .requestSubmissionDate(new Date())
                        .requestEndDate(LocalDateTime.of(1998, 1, 1, 1, 1))
                        .build())
                .params(params)
                .build();
    }
    
    @Test
    void generate_Withholding_of_allowances_notice() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "installation", "L008 IN Withholding Allowances Notice UKETS_Draft_KB_Temp_CL_IW.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient,", "to recipient",
                "reasonType", WithholdingOfAllowancesReasonType.DETERMINING_A_SURRENDER_APPLICATION
                )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_Withdrawal_of_withholding_of_allowances_notice() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.OPRED;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "opred", "installation", "L009 P3 Issue Allowances.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient,", "to recipient"
                )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_approval_official_letter_corsia() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "CORSIA_EMP_Approval_Notice_METS_final.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient", "to recipient"
            )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_deemed_withdrawn_official_letter_corsia() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "CORSIA_EMP_Withdrawn_Notice_METS_final.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient", "to recipient"
            )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_corsia() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "CORSIA_monitoring_plan_template_METS_final.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .scheme(EmissionTradingScheme.CORSIA)
            .serviceContactDetails(ServiceContactDetails.builder()
                .name("service contact name")
                .email("service-contact@mail")
                .roleCode("service contact role code")
                .build())
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .emissionsMonitoringApproach(uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach
                    .CertMonitoringApproach.builder()
                    .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING)
                    .certEmissionsType(CertEmissionsType.GREAT_CIRCLE_DISTANCE)
                    .explanation("Cert explanation")
                    .build())
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .operatorName("operator name")
                    .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                        .aircraftRegistrationMarkings(Set.of("registration marking 1",
                            "registration marking 2", "registration maqrking 3"))
                        .build())
                    .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(Boolean.TRUE)
                        .restrictionsExist(Boolean.TRUE)
                        .restrictionsDetails("Restriction Details")
                        .certificateNumber("Certificate Number 123")
                        .issuingAuthority("Certificate issuing authority")
                        .build())
                    .organisationStructure(LimitedCompanyOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                        .registrationNumber("Limited company registration number")
                        .organisationLocation(LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1("line1")
                            .city("city")
                            .state("state")
                            .postcode("1234")
                            .country("country")
                            .build())
                        .differentContactLocationExist(Boolean.TRUE)
                        .differentContactLocation(LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1("diff line1")
                            .line2("diff line 2")
                            .city("diff city")
                            .postcode("1234")
                            .country("country")
                            .build())
                        .build())
                        .subsidiaryCompanyExist(Boolean.TRUE)
                        .subsidiaryCompanies(List.of(SubsidiaryCompanyCorsia.builder()
                            .operatorName("subsidiary Company")
                            .activityDescription("activity description")
                                .flightTypes(Set.of(FlightType.SCHEDULED))
                                .flightIdentification(FlightIdentification.builder()
                                .flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                                .aircraftRegistrationMarkings(Set.of("registration marking 1",
                                    "registration marking 2", "registration maqrking 3"))
                                .build())
                            .registeredLocation(LocationOnShoreStateDTO.builder()
                                .type(LocationType.ONSHORE_STATE)
                                .line1("line1")
                                .city("city")
                                .state("state")
                                .postcode("1234")
                                .country("country")
                                .build())
                                .companyRegistrationNumber("companyNumber")
                            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                                .certificateExist(Boolean.TRUE)
                                .restrictionsExist(Boolean.TRUE)
                                .restrictionsDetails("Restriction Details")
                                .certificateNumber("Certificate Number 123")
                                .issuingAuthority("Certificate issuing authority")
                                .build())
                        .build()))
                    .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED, FlightType.NON_SCHEDULED))
                        .activityDescription("""
activity description

Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                         """)
                        .build())
                    .build())
                .emissionSources(EmpEmissionSourcesCorsia.builder()
                    .aircraftTypes(Set.of(
                        AircraftTypeDetailsCorsia.builder()
                            .aircraftTypeInfo(AircraftTypeInfo.builder()
                                .manufacturer("manufacturer 1")
                                .model("model 1")
                                .designatorType("designator type 1")
                                .build())
                            .subtype("subtype 1")
                            .numberOfAircrafts(10L)
                            .fuelTypes(List.of(FuelTypeCorsia.JET_KEROSENE))
                            .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                            .build(),
                        AircraftTypeDetailsCorsia.builder()
                            .aircraftTypeInfo(AircraftTypeInfo.builder()
                                .manufacturer("manufacturer 2")
                                .model("model 2")
                                .designatorType("designator type 2")
                                .build())
                            .subtype("subtype 2")
                            .numberOfAircrafts(20L)
                            .fuelTypes(List.of(FuelTypeCorsia.JET_KEROSENE, FuelTypeCorsia.JET_GASOLINE))
                            .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_B)
                            .build(),
                        AircraftTypeDetailsCorsia.builder()
                            .aircraftTypeInfo(AircraftTypeInfo.builder()
                                .manufacturer("manufacturer 3")
                                .model("model 3")
                                .designatorType("designator type 3")
                                .build())
                            .subtype("subtype 3")
                            .numberOfAircrafts(30L)
                            .fuelTypes(List.of(FuelTypeCorsia.AVIATION_GASOLINE))
                            .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF)
                            .build(),
                        AircraftTypeDetailsCorsia.builder()
                            .aircraftTypeInfo(AircraftTypeInfo.builder()
                                .manufacturer("manufacturer 4")
                                .model("model 4")
                                .designatorType("designator type 4")
                                .build())
                            .subtype("subtype 4")
                            .numberOfAircrafts(40L)
                            .fuelTypes(List.of(FuelTypeCorsia.TS_1, FuelTypeCorsia.AVIATION_GASOLINE))
                            .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.FUEL_UPLIFT)
                            .build(),
                        AircraftTypeDetailsCorsia.builder()
                            .aircraftTypeInfo(AircraftTypeInfo.builder()
                                .manufacturer("manufacturer 5")
                                .model("model 5")
                                .designatorType("designator type 5")
                                .build())
                            .numberOfAircrafts(50L)
                            .fuelTypes(List.of(FuelTypeCorsia.JET_KEROSENE, FuelTypeCorsia.NO_3_JET_FUEL))
                            .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                            .build(),
                        AircraftTypeDetailsCorsia.builder()
                            .aircraftTypeInfo(AircraftTypeInfo.builder()
                                .manufacturer("manufacturer 6")
                                .model("model 6")
                                .designatorType("designator type 6")
                                .build())
                            .subtype("subtype 6")
                            .numberOfAircrafts(60L)
                            .fuelTypes(List.of(FuelTypeCorsia.JET_KEROSENE, FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.AVIATION_GASOLINE))
                            .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.BLOCK_HOUR)
                            .build()
                    ))
                    .multipleFuelConsumptionMethodsExplanation("""
Multiple fuel consumption methods explanation                                        
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                    .build())
                .flightAndAircraftProcedures(EmpFlightAndAircraftProceduresCorsia.builder()
                    .aircraftUsedDetails(EmpProcedureForm.builder()
                        .procedureDocumentName("Aircraft used document name")
                        .procedureReference("Aircraft used procedure reference")
                        .procedureDescription("Aircraft used procedure description")
                        .responsibleDepartmentOrRole("""
Aircraft used responsible department
01. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
02. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
03. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
04. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
05. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
06. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
07. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
08. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
09. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
10. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
11. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
12. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
13. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
14. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
15. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
16. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
17. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
18. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
19. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
20. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
21. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
22. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
23. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
24. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
25. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
26. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
27. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
28. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
29. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                                          """)
                        .locationOfRecords("Aircraft used records location")
                        .itSystemUsed("Aircraft used IT system")
                        .build())
                    .flightListCompletenessDetails(EmpProcedureForm.builder()
                        .procedureDocumentName("Flights completeness document name")
                        .procedureReference("Flights completeness procedure reference")
                        .procedureDescription("Flights completeness procedure description")
                        .responsibleDepartmentOrRole("Flights completeness responsible department")
                        .locationOfRecords("Flights completeness records location")
                        .itSystemUsed("Flights completeness IT system")
                        .build())
                    .internationalFlightsDetermination(EmpProcedureForm.builder()
                        .procedureDocumentName("Flights completeness document name")
                        .procedureReference("Flights completeness procedure reference")
                        .procedureDescription("Flights completeness procedure description")
                        .responsibleDepartmentOrRole("Flights completeness responsible department")
                        .locationOfRecords("Flights completeness records location")
                        .itSystemUsed("Flights completeness IT system")
                        .build())
                    .internationalFlightsDeterminationOffset(EmpProcedureForm.builder()
                        .procedureDocumentName("Flights completeness document name")
                        .procedureReference("Flights completeness procedure reference")
                        .procedureDescription("Flights completeness procedure description")
                        .responsibleDepartmentOrRole("Flights completeness responsible department")
                        .locationOfRecords("Flights completeness records location")
                        .itSystemUsed("Flights completeness IT system")
                        .build())
                    .internationalFlightsDeterminationNoMonitoring(EmpProcedureForm.builder()
                        .procedureDocumentName("Flights completeness document name")
                        .procedureReference("Flights completeness procedure reference")
                        .procedureDescription("Flights completeness procedure description")
                        .responsibleDepartmentOrRole("Flights completeness responsible department")
                        .locationOfRecords("Flights completeness records location")
                        .itSystemUsed("Flights completeness IT system")
                        .build())
                    .operatingStatePairs(EmpOperatingStatePairsCorsia.builder()
                        .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                                .stateA("stateA")
                                .stateB("stateB")
                            .build()))
                        .build())
                    .build())
                .dataGaps(EmpDataGapsCorsia.builder()
                    .dataGaps("""
data gaps
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                    .secondaryDataSources("""
secondary data sources
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                    .secondarySourcesDataGapsExist(Boolean.TRUE)
                    .secondarySourcesDataGapsConditions("Secondary sources data gaps conditions")
                    .build())
                .managementProcedures(EmpManagementProceduresCorsia.builder()
                    .monitoringReportingRoles(EmpMonitoringReportingRolesCorsia.builder()
                        .monitoringReportingRoles(List.of(EmpMonitoringReportingRoleCorsia.builder()
                                .jobTitle("job title 1")
                                .mainDuties("""
main duty 1
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                                                """)
                                .build(),
                            EmpMonitoringReportingRoleCorsia.builder()
                                .jobTitle("job title 2")
                                .mainDuties("main duty 2")
                                .build()))
                        .build())
                    .dataManagement(EmpDataManagement.builder()
                        .description("Corrections document name")
                        .build())
                    .recordKeepingAndDocumentation(EmpDataManagement.builder()
                        .description("Outsource activities document name")
                        .build())
                    .riskExplanation(EmpDataManagement.builder()
                        .description("Control risks covered document name")
                        .build())
                    .empRevisions(EmpDataManagement.builder()
                        .description("Monitoring plan appropriateness document name")
                        .build())
                    .build())
                .methodAProcedures(EmpMethodAProcedures.builder()
                    .fuelConsumptionPerFlight(EmpProcedureForm.builder()
                        .procedureDocumentName("Method A fuel consumption document name")
                        .procedureReference("Method A fuel consumption procedure reference")
                        .procedureDescription("Method A fuel consumption procedure description")
                        .responsibleDepartmentOrRole("Method A fuel consumption responsible department")
                        .locationOfRecords("Method A fuel consumption location")
                        .itSystemUsed("Method A fuel consumption IT system")
                        .build())
                    .fuelDensity(EmpProcedureForm.builder()
                        .procedureDocumentName("Method A fuel density document name")
                        .procedureReference("Method A fuel density procedure reference")
                        .procedureDescription("Method A fuel density procedure description")
                        .responsibleDepartmentOrRole("Method A fuel density responsible department")
                        .locationOfRecords("Method A fuel density location")
                        .itSystemUsed("Method A fuel density IT system")
                        .build())
                    .build())
                .methodBProcedures(EmpMethodBProcedures.builder()
                    .fuelConsumptionPerFlight(EmpProcedureForm.builder()
                        .procedureDocumentName("Method B fuel consumption document name")
                        .procedureReference("Method B fuel consumption procedure reference")
                        .procedureDescription("Method B fuel consumption procedure description")
                        .responsibleDepartmentOrRole("Method B fuel consumption responsible department")
                        .locationOfRecords("Method B fuel consumption location")
                        .itSystemUsed("Method B fuel consumption IT system")
                        .build())
                    .fuelDensity(EmpProcedureForm.builder()
                        .procedureDocumentName("Method B fuel density document name")
                        .procedureReference("Method B fuel density procedure reference")
                        .procedureDescription("Method B fuel density procedure description")
                        .responsibleDepartmentOrRole("Method B fuel density responsible department")
                        .locationOfRecords("Method B fuel density location")
                        .itSystemUsed("Method B fuel density IT system")
                        .build())
                    .build())
                .blockOnBlockOffMethodProcedures(EmpBlockOnBlockOffMethodProcedures.builder()
                    .fuelConsumptionPerFlight(EmpProcedureForm.builder()
                        .procedureDocumentName("Block off block on fuel consumption document name")
                        .procedureReference("Block off block on fuel consumption procedure reference")
                        .procedureDescription("Block off block on fuel consumption procedure description")
                        .responsibleDepartmentOrRole("Block off block on fuel consumption responsible department")
                        .locationOfRecords("Block off block on fuel consumption location")
                        .itSystemUsed("Block off block on fuel consumption IT system")
                        .build())
                    .build())
                .fuelUpliftMethodProcedures(EmpFuelUpliftMethodProcedures.builder()
                    .blockHoursPerFlight(EmpProcedureForm.builder()
                        .procedureDocumentName("Fuel uplift block hours document name")
                        .procedureReference("Fuel uplift block hours procedure reference")
                        .procedureDescription("Fuel uplift block hours procedure description")
                        .responsibleDepartmentOrRole("Fuel uplift block hours responsible department")
                        .locationOfRecords("Fuel uplift block hours location")
                        .itSystemUsed("Fuel uplift block hours IT system")
                        .build())
                    .zeroFuelUplift("""
Fuel uplift zero fuel uplift
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                    .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                    .fuelDensity(EmpProcedureForm.builder()
                        .procedureDocumentName("Fuel uplift fuel density document name")
                        .procedureReference("Fuel uplift fuel density procedure reference")
                        .procedureDescription("Fuel uplift fuel density procedure description")
                        .responsibleDepartmentOrRole("Fuel uplift fuel density responsible department")
                        .locationOfRecords("Fuel uplift fuel density location")
                        .itSystemUsed("Fuel uplift fuel density IT system")
                        .build())
                    .build())
                .blockHourMethodProcedures(EmpBlockHourMethodProcedures.builder()
                    .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.CLEAR_DISTINGUISHION, FuelBurnCalculationType.NOT_CLEAR_DISTINGUISHION))
                    .clearDistinguishionIcaoAircraftDesignators(Set.of("clear icao designator 1", "clear icao designator 2"))
                    .notClearDistinguishionIcaoAircraftDesignators(Set.of("not clear icao designator 1", "not clear icao designator 2"))
                    .assignmentAndAdjustment("""
Block hour assignment and adjustment
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                        """)
                    .blockHoursMeasurement(EmpProcedureForm.builder()
                        .procedureDocumentName("Block hour block hours measurement document name")
                        .procedureReference("Block hour block hours measurement procedure reference")
                        .procedureDescription("Block hour block hours measurement procedure description")
                        .responsibleDepartmentOrRole("Block hour block hours measurement responsible department")
                        .locationOfRecords("Block hour block hours measurement location")
                        .itSystemUsed("Block hour block hours measurement IT system")
                        .build())
                    .fuelDensity(EmpProcedureForm.builder()
                        .procedureDocumentName("Block hour fuel density document name")
                        .procedureReference("Block hour fuel density procedure reference")
                        .procedureDescription("Block hour fuel density procedure description")
                        .responsibleDepartmentOrRole("Block hour fuel density responsible department")
                        .locationOfRecords("Block hour fuel density location")
                        .itSystemUsed("Block hour fuel density IT system")
                        .build())
                    .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_INVOICES)
                    .build())
                .abbreviations(EmpAbbreviations.builder()
                    .exist(true)
                    .abbreviationDefinitions(List.of(EmpAbbreviationDefinition.builder()
                            .abbreviation("abbreviation 1")
                            .definition("""
definition 1
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam elementum tincidunt magna. Vestibulum nunc lacus, dapibus eu dui eu, venenatis fringilla tortor. Curabitur viverra blandit risus, ac scelerisque ligula vestibulum sed. Maecenas finibus felis at tellus eleifend, molestie ultricies justo maximus. Integer sagittis lectus id nibh imperdiet, mattis congue erat ultrices. Morbi non pretium libero. Integer vel magna nulla. Curabitur nec risus est. Nunc tortor erat, pellentesque sit amet finibus a, sollicitudin ac nisi. Curabitur tincidunt dolor non efficitur pharetra. Mauris elementum, enim sit amet tempor cursus, justo tortor fermentum eros, quis efficitur dui orci sit amet massa. Integer sit amet elit viverra, dictum nibh scelerisque, consequat tellus. Morbi sollicitudin rutrum fermentum. Nullam sed justo vitae purus aliquam bibendum quis vel nibh. Nulla dictum congue nibh, ac sodales turpis aliquam sit amet. Aenean tortor est, facilisis sed mauris sit amet, congue efficitur metus.
In ac posuere mauris, id blandit magna. Donec tempor nulla nunc, a ultrices nulla malesuada ac. Nunc viverra pulvinar nulla, non condimentum justo imperdiet non. Vestibulum eget libero quis ligula consectetur ultrices. Praesent tincidunt hendrerit tortor, nec vestibulum ligula placerat eget. Duis ac semper nulla, quis convallis ligula. Donec non eros at justo rutrum facilisis eu ac metus. Duis enim justo, convallis in lacus vel, condimentum dapibus turpis. Aenean nec molestie lorem. Duis imperdiet posuere turpis, tempus convallis arcu mattis non. Pellentesque sed enim ut neque tempus congue eget eget erat. Cras condimentum malesuada ex, vitae fringilla elit lacinia vitae. Cras sed commodo dolor, at tristique metus. Nunc dapibus, enim sit amet dapibus semper, velit est ultrices magna, lobortis feugiat arcu ligula sed dolor.
Aliquam nec ligula ipsum. Duis sit amet neque ut eros fermentum pharetra. Nunc imperdiet faucibus dignissim. Phasellus vitae tincidunt erat. In nec ex eu est porta venenatis. In feugiat cursus commodo. Praesent at dolor imperdiet, sollicitudin ipsum vel, sollicitudin nisi. Maecenas eget lorem nec arcu tincidunt faucibus. Nam lacinia mollis magna, non congue ante volutpat at. Mauris laoreet nibh sit amet eros ultrices, in ornare lectus facilisis.
                                                        """)
                            .build(),
                        EmpAbbreviationDefinition.builder()
                            .abbreviation("abbreviation 2")
                            .definition("definition 2")
                            .build()))
                    .build())
                .build())
            .build();

        final TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, Map.of(
            "empContainer", empContainer,
            "consolidationNumber", 24,
            "organisationLocation", "line1 \n " +
                        "line2 \n " +
                        "city \n " +
                        "state \n " +
                        "postcode \n " +
                        "country",
            "differentContactLocation", "diff line1 \n " +
                        "diff city \n " +
                        "diff state \n " +
                        "diff postcode \n " +
                        "diff country",
                "subsidiaryCompanies", List.of(
                        TemplateSubsidiaryCompany.builder()
                                .subsidiaryCompany(
                                        SubsidiaryCompanyCorsia.builder()
                                                .operatorName("subsidiary Company")
                                                .activityDescription("activity description")
                                                .flightTypes(Set.of(FlightType.SCHEDULED))
                                                .flightIdentification(FlightIdentification.builder()
                                                        .flightIdentificationType(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS)
                                                        .aircraftRegistrationMarkings(Set.of("registration marking 1",
                                                                "registration marking 2", "registration maqrking 3"))
                                                        .build())
                                                .registeredLocation(LocationOnShoreStateDTO.builder()
                                                        .type(LocationType.ONSHORE_STATE)
                                                        .line1("line1")
                                                        .city("city")
                                                        .state("state")
                                                        .postcode("1234")
                                                        .country("country")
                                                        .build())
                                                .companyRegistrationNumber("companyNumber")
                                                .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                                                        .certificateExist(Boolean.TRUE)
                                                        .restrictionsExist(Boolean.TRUE)
                                                        .restrictionsDetails("Restriction Details")
                                                        .certificateNumber("Certificate Number 123")
                                                        .issuingAuthority("Certificate issuing authority")
                                                        .build())
                                                .build())
                                .registeredAddress("subsidiary line1 \n " +
                                        "subsidiary city \n " +
                                        "subsidiary state \n " +
                                        "subsidiary postcode \n " +
                                        "subsidiary country")
                                .build(),
                        TemplateSubsidiaryCompany.builder()
                                .subsidiaryCompany(
                                        SubsidiaryCompanyCorsia.builder()
                                                .operatorName("subsidiary Company 2")
                                                .activityDescription("activity description 2")
                                                .flightTypes(Set.of(FlightType.SCHEDULED, FlightType.NON_SCHEDULED))
                                                .flightIdentification(FlightIdentification.builder()
                                                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                                                        .icaoDesignators("icao designators")
                                                        .build())
                                                .registeredLocation(LocationOnShoreStateDTO.builder()
                                                        .type(LocationType.ONSHORE_STATE)
                                                        .line1("line1")
                                                        .city("city")
                                                        .state("state")
                                                        .postcode("1234")
                                                        .country("country")
                                                        .build())
                                                .companyRegistrationNumber("companyNumber 2")
                                                .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                                                        .certificateExist(Boolean.TRUE)
                                                        .restrictionsExist(Boolean.TRUE)
                                                        .restrictionsDetails("Restriction Details")
                                                        .certificateNumber("Certificate Number 234")
                                                        .issuingAuthority("Certificate issuing authority")
                                                        .restrictionsExist(Boolean.TRUE)
                                                        .restrictionsDetails("restrictions details")
                                                        .build())
                                                .build())
                                .registeredAddress("subsidiary line1 \n " +
                                        "subsidiary city 2 \n " +
                                        "subsidiary state 2 \n " +
                                        "subsidiary postcode 2 \n " +
                                        "subsidiary country 2")
                                .build(),
                        TemplateSubsidiaryCompany.builder()
                                .subsidiaryCompany(
                                        SubsidiaryCompanyCorsia.builder()
                                                .operatorName("subsidiary Company 3")
                                                .activityDescription("activity description 3")
                                                .flightTypes(Set.of(FlightType.SCHEDULED, FlightType.NON_SCHEDULED))
                                                .flightIdentification(FlightIdentification.builder()
                                                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                                                        .icaoDesignators("icao designators 3")
                                                        .build())
                                                .registeredLocation(LocationOnShoreStateDTO.builder()
                                                        .type(LocationType.ONSHORE_STATE)
                                                        .line1("line1")
                                                        .city("city")
                                                        .state("state")
                                                        .postcode("1234")
                                                        .country("country")
                                                        .build())
                                                .companyRegistrationNumber("companyNumber 3")
                                                .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                                                        .certificateExist(Boolean.TRUE)
                                                        .restrictionsExist(Boolean.TRUE)
                                                        .restrictionsDetails("Restriction Details 3")
                                                        .certificateNumber("Certificate Number 456")
                                                        .issuingAuthority("Certificate issuing authority 3")
                                                        .restrictionsExist(Boolean.TRUE)
                                                        .restrictionsDetails("restrictions details 3")
                                                        .build())
                                                .build())
                                .registeredAddress("subsidiary line1 \n " +
                                        "subsidiary city 3 \n " +
                                        "subsidiary state 3 \n " +
                                        "subsidiary postcode 3 \n " +
                                        "subsidiary country 3")
                                .build()
                        )
                )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_approval_official_letter_corsia_blank() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.NORTHERN_IRELAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "northern_ireland", "aviation", "CORSIA_EMP_Approval_Notice_NIEA.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient", "to recipient"
            )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_deemed_withdrawn_official_letter_corsia_blank() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.NORTHERN_IRELAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "northern_ireland", "aviation", "CORSIA_EMP_Withdrawn_Notice_NIEA.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient", "to recipient"
            )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_corsia_blank() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.NORTHERN_IRELAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "northern_ireland", "aviation", "CORSIA_monitoring_plan_template_NIEA.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        EmissionsMonitoringPlanCorsiaContainer emissionsMonitoringPlanCorsiaContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
            .build();

        final TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, Map.of(
            "empContainer", emissionsMonitoringPlanCorsiaContainer,
            "consolidationNumber", 24)
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_variation_approval_official_letter_corsia() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "CORSIA_EMP_Variation_Approve_Notice_METS_final.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams =
            buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                    "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                    "toRecipient", "to recipient",
                    "variationScheduleItems", List.of("item 1", "item 2")
                )
            );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_variation_deemed_withdrawn_official_letter_corsia() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "CORSIA_EMP_Withdrawn_Notice_METS_final.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient", "to recipient"
            )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    @Test
    void generate_emp_variation_refusal_official_letter_corsia() throws Exception {
    	String fileNameToGenerate = "fileNameToGenerate";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String signatoryUser = "Signatory user full name";
        final Path templateFilePath = Paths.get("src", "main", "resources", "templates", "ca", "england", "aviation", "CORSIA_EMP_Variation_Refusal_Notice_METS_final.docx");
        final FileDTO templateFile = createFile(templateFilePath);

        final Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        final FileDTO signatureFile = createFile(signatureFilePath);

        final TemplateParams templateParams = buildTemplateParamsForOfficialNotice(ca, signatoryUser, signatureFile, Map.of(
                "ccRecipients", List.of("cc recipient 1", "cc recipient 2"),
                "toRecipient", "to recipient"
            )
        );

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);
        
        assertThat(resultActual).isEqualTo(resultExpected);
    }

    private FileDTO createFile(Path sampleFilePath) throws IOException {
        byte[] bytes = Files.readAllBytes(sampleFilePath);
        return FileDTO.builder()
                .fileContent(bytes)
                .fileName(sampleFilePath.getFileName().toString())
                .fileSize(sampleFilePath.toFile().length())
                .fileType(MimeTypeUtils.detect(bytes, sampleFilePath.getFileName().toString()))
                .build();
    }
    
    private TemplateParams buildTemplateParams(CompetentAuthorityEnum ca, String signatoryUser, FileDTO signatureFile,
            Map<String, Object> params) {
		CompetentAuthorityDTO caDto = CompetentAuthorityDTO.builder().id(ca).email("email").name("name").build();
		return TemplateParams.builder()
				.competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
				.competentAuthority(caDto)
				.logo(PmrvCompetentAuthorityService.getCompetentAuthorityLogo(ca))
				.build())
				.competentAuthorityCentralInfo("ca central info")
				.signatoryParams(SignatoryTemplateParams.builder()
				.fullName(signatoryUser)
				.signature(signatureFile.getFileContent())
				.jobTitle("Project Manager")
				.build())
				.accountParams(InstallationAccountTemplateParams.builder()
				.emitterType(EmitterType.GHGE.name())
				.legalEntityName("LE name")
				.legalEntityLocation("LE ethnikis antistaseos\nLE street number 124\nLE postal code 15125")
				.name("account name")
				.siteName("Account site name")
				.location("Account ethnikis  \nAccount street number 125 \nAccount postal code 15126")
				.primaryContact("primary contact")
				.primaryContactEmail("primary contact email")
				.serviceContact("service contact")
				.serviceContactEmail("service contact email")
				.installationCategory("B")
				.build())
				.permitId("UK-E-IN-12345")
				.workflowParams(WorkflowTemplateParams.builder()
				.requestId("123")
				.requestType("PERMIT_VARIATION") //("PERMIT_ISSUANCE")
				.requestTypeInfo("your permit variation")
				.requestSubmissionDate(new Date())
				.requestEndDate(LocalDateTime.of(1998, 1, 1, 1, 1))
				.build())
				.params(params)
				.build();
	}
}
