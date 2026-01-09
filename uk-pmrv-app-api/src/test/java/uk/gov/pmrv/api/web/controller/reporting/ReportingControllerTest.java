package uk.gov.pmrv.api.web.controller.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.GlobalWarmingPotential;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.SlopeSourceStreamEmissionCalculationMethodData;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionsCalculationService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.pfc.PfcEmissionsCalculationService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportingControllerTest {

    private static final String REPORTING_BASE_CONTROLLER_PATH = "/v1.0/reporting";

    @InjectMocks
    private ReportingController reportingController;

    @Mock
    private EmissionsCalculationService emissionsCalculationService;

    @Mock
    private MeasurementN2OEmissionsCalculationService measurementN2OEmissionsCalculationService;

    @Mock
    private MeasurementCO2EmissionsCalculationService measurementCO2EmissionsCalculationService;

    @Mock
    private PfcEmissionsCalculationService pfcEmissionsCalculationService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
            new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(reportingController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        reportingController = (ReportingController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(reportingController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void calculateEmissions() throws Exception {
        BigDecimal reportableEmissions = BigDecimal.valueOf(10001.105);
        BigDecimal sustainableBiomassEmissions = BigDecimal.valueOf(923.09);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .sourceStreamType(SourceStreamType.COKE_MASS_BALANCE)
            .activityData(BigDecimal.ONE)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .build();
        EmissionsCalculationDTO emissions = EmissionsCalculationDTO.builder()
            .reportableEmissions(reportableEmissions)
            .sustainableBiomassEmissions(sustainableBiomassEmissions)
            .build();

        when(emissionsCalculationService.calculateEmissions(calculationParams)).thenReturn(emissions);

        mockMvc.perform(
                MockMvcRequestBuilders.post(REPORTING_BASE_CONTROLLER_PATH + "/calculation/calculate-emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(calculationParams)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportableEmissions").value(reportableEmissions))
            .andExpect(jsonPath("$.sustainableBiomassEmissions").value(sustainableBiomassEmissions));

        verify(emissionsCalculationService, times(1)).calculateEmissions(calculationParams);
    }

    @Test
    void calculateMeasurementCO2Emissions() throws Exception {
        BigDecimal reportableEmissions = BigDecimal.valueOf(10001.105);
        BigDecimal sustainableBiomassEmissions = BigDecimal.valueOf(923.09);
        MeasurementEmissionsCalculationParamsDTO measurementCO2EmissionsCalculationParamsDTO =
            MeasurementEmissionsCalculationParamsDTO.builder()
                .annualHourlyAverageGHGConcentration(BigDecimal.ONE)
                .biomassPercentage(BigDecimal.ONE)
                .operationalHours(BigDecimal.ONE)
                .containsBiomass(true)
                .annualHourlyAverageFlueGasFlow(BigDecimal.ONE)
                .build();

        MeasurementEmissionsCalculationDTO measurementCO2EmissionsCalculationDTO =
            MeasurementEmissionsCalculationDTO.builder()
                .reportableEmissions(reportableEmissions)
                .sustainableBiomassEmissions(sustainableBiomassEmissions)
                .annualGasFlow(BigDecimal.ONE)
                .globalWarmingPotential(BigDecimal.ONE)
                .annualFossilAmountOfGreenhouseGas(BigDecimal.ONE)
                .build();

        when(measurementCO2EmissionsCalculationService.calculateEmissions(measurementCO2EmissionsCalculationParamsDTO)).thenReturn(
            measurementCO2EmissionsCalculationDTO);

        mockMvc.perform(
                MockMvcRequestBuilders.post(REPORTING_BASE_CONTROLLER_PATH + "/measurement/co2/calculate-emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(measurementCO2EmissionsCalculationParamsDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportableEmissions").value(reportableEmissions))
            .andExpect(jsonPath("$.globalWarmingPotential").value(BigDecimal.ONE))
            .andExpect(jsonPath("$.annualGasFlow").value(BigDecimal.ONE))
            .andExpect(jsonPath("$.annualFossilAmountOfGreenhouseGas").value(BigDecimal.ONE));

        verify(measurementCO2EmissionsCalculationService, times(1)).calculateEmissions(measurementCO2EmissionsCalculationParamsDTO);
    }

    @Test
    void calculateMeasurementN2OEmissions() throws Exception {
        BigDecimal reportableEmissions = BigDecimal.valueOf(10001.105);
        BigDecimal sustainableBiomassEmissions = BigDecimal.valueOf(923.09);
        MeasurementEmissionsCalculationParamsDTO measurementN2OEmissionsCalculationParamsDTO =
            MeasurementEmissionsCalculationParamsDTO.builder()
                .annualHourlyAverageGHGConcentration(BigDecimal.ONE)
                .biomassPercentage(BigDecimal.ONE)
                .operationalHours(BigDecimal.ONE)
                .containsBiomass(true)
                .annualHourlyAverageFlueGasFlow(BigDecimal.ONE)
                .build();

        MeasurementEmissionsCalculationDTO measurementEmissionsCalculationDTO =
            MeasurementEmissionsCalculationDTO.builder()
                .reportableEmissions(reportableEmissions)
                .sustainableBiomassEmissions(sustainableBiomassEmissions)
                .annualGasFlow(BigDecimal.ONE)
                .globalWarmingPotential(BigDecimal.ONE)
                .annualFossilAmountOfGreenhouseGas(BigDecimal.ONE)
                .build();

        when(measurementN2OEmissionsCalculationService.calculateEmissions(measurementN2OEmissionsCalculationParamsDTO)).thenReturn(
            measurementEmissionsCalculationDTO);

        mockMvc.perform(
                MockMvcRequestBuilders.post(REPORTING_BASE_CONTROLLER_PATH + "/measurement/n2o/calculate-emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(measurementN2OEmissionsCalculationParamsDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportableEmissions").value(reportableEmissions))
            .andExpect(jsonPath("$.globalWarmingPotential").value(BigDecimal.ONE))
            .andExpect(jsonPath("$.annualGasFlow").value(BigDecimal.ONE))
            .andExpect(jsonPath("$.annualFossilAmountOfGreenhouseGas").value(BigDecimal.ONE));

        verify(measurementN2OEmissionsCalculationService, times(1)).calculateEmissions(measurementN2OEmissionsCalculationParamsDTO);
    }

    @Test
    void calculatePfcEmissions() throws Exception {
        PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO = PfcEmissionsCalculationParamsDTO.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .totalPrimaryAluminium(BigDecimal.ONE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .anodeEffectsPerCellDay(BigDecimal.ONE)
                .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
                .build())
            .build();

        PfcEmissionsCalculationDTO pfcEmissionsCalculationDTO = PfcEmissionsCalculationDTO.builder()
            .reportableEmissions(BigDecimal.ONE)
            .amountOfCF4(BigDecimal.ONE)
            .totalC2F6Emissions(BigDecimal.ONE)
            .totalCF4Emissions(BigDecimal.ONE)
            .amountOfC2F6(BigDecimal.ONE)
            .build();

        when(pfcEmissionsCalculationService.calculateEmissions(pfcEmissionsCalculationParamsDTO)).thenReturn(pfcEmissionsCalculationDTO);

        mockMvc.perform(
                MockMvcRequestBuilders.post(REPORTING_BASE_CONTROLLER_PATH + "/calculation/pfc/calculate-emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pfcEmissionsCalculationParamsDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportableEmissions").value(BigDecimal.ONE))
            .andExpect(jsonPath("$.globalWarmingPotentialCF4").value(GlobalWarmingPotential.PFC_CF4.getValue()))
            .andExpect(jsonPath("$.globalWarmingPotentialC2F6").value(GlobalWarmingPotential.PFC_C2F6.getValue()))
            .andExpect(jsonPath("$.amountOfCF4").value(BigDecimal.ONE))
            .andExpect(jsonPath("$.totalCF4Emissions").value(BigDecimal.ONE))
            .andExpect(jsonPath("$.amountOfC2F6").value(BigDecimal.ONE))
            .andExpect(jsonPath("$.totalC2F6Emissions").value(BigDecimal.ONE));

        verify(pfcEmissionsCalculationService, times(1)).calculateEmissions(pfcEmissionsCalculationParamsDTO);
    }

    @Test
    void calculateEmissions_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .sourceStreamType(SourceStreamType.COKE_MASS_BALANCE)
            .activityData(BigDecimal.ONE)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.OPERATOR});

        mockMvc.perform(
                MockMvcRequestBuilders.post(REPORTING_BASE_CONTROLLER_PATH + "/calculation/calculate-emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(calculationParams)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(emissionsCalculationService);
    }

    @Test
    void calculateMeasurementCO2Emissions_Forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        MeasurementEmissionsCalculationParamsDTO measurementCO2EmissionsCalculationParamsDTO =
            MeasurementEmissionsCalculationParamsDTO.builder()
                .annualHourlyAverageGHGConcentration(BigDecimal.ONE)
                .biomassPercentage(BigDecimal.ONE)
                .operationalHours(BigDecimal.ONE)
                .containsBiomass(true)
                .annualHourlyAverageFlueGasFlow(BigDecimal.ONE)
                .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.OPERATOR});

        mockMvc.perform(
                MockMvcRequestBuilders.post(REPORTING_BASE_CONTROLLER_PATH + "/measurement/co2/calculate-emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(measurementCO2EmissionsCalculationParamsDTO)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(measurementCO2EmissionsCalculationService);
    }

    @Test
    void calculateMeasurementN2OEmissions_Forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        MeasurementEmissionsCalculationParamsDTO measurementN2OEmissionsCalculationParamsDTO =
            MeasurementEmissionsCalculationParamsDTO.builder()
                .annualHourlyAverageGHGConcentration(BigDecimal.ONE)
                .biomassPercentage(BigDecimal.ONE)
                .operationalHours(BigDecimal.ONE)
                .containsBiomass(true)
                .annualHourlyAverageFlueGasFlow(BigDecimal.ONE)
                .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.OPERATOR});

        mockMvc.perform(
                MockMvcRequestBuilders.post(REPORTING_BASE_CONTROLLER_PATH + "/measurement/n2o/calculate-emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(measurementN2OEmissionsCalculationParamsDTO)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(measurementN2OEmissionsCalculationService);
    }

    @Test
    void calculatePfcEmissions_Forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO = PfcEmissionsCalculationParamsDTO.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .totalPrimaryAluminium(BigDecimal.ONE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .anodeEffectsPerCellDay(BigDecimal.ONE)
                .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
                .build())
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.OPERATOR});

        mockMvc.perform(
                MockMvcRequestBuilders.post(REPORTING_BASE_CONTROLLER_PATH + "/calculation/pfc/calculate-emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pfcEmissionsCalculationParamsDTO)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(pfcEmissionsCalculationService);
    }
}