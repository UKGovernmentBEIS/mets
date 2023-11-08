package uk.gov.pmrv.api.web.controller.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryDataYearExistenceDTO;
import uk.gov.pmrv.api.reporting.domain.dto.ChargingZoneDTO;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.NationalInventoryDataDTO;
import uk.gov.pmrv.api.reporting.domain.dto.RegionalInventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.SourceStreamCalculationParametersInfo;
import uk.gov.pmrv.api.reporting.service.ChargingZoneService;
import uk.gov.pmrv.api.reporting.service.NationalInventoryDataService;
import uk.gov.pmrv.api.reporting.service.RegionalInventoryDataService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.SourceStreamCalculationParametersInfoService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportingDataControllerTest {

    private static final String REPORTING_DATA_BASE_CONTROLLER_PATH = "/v1.0/reporting-data";

    @InjectMocks
    private ReportingDataController reportingDataController;

    @Mock
    private ChargingZoneService chargingZoneService;

    @Mock
    private RegionalInventoryDataService regionalInventoryDataService;

    @Mock
    private NationalInventoryDataService nationalInventoryDataService;

    @Mock
    private SourceStreamCalculationParametersInfoService calculationParametersInfoService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(reportingDataController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        reportingDataController = (ReportingDataController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(reportingDataController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getInventoryDataExistenceByYear_national() throws Exception {
        final Year year = Year.of(2023);
        final InventoryDataYearExistenceDTO inventoryDataYearExistence = InventoryDataYearExistenceDTO.builder()
                .year(year)
                .exist(true)
                .build();

        when(nationalInventoryDataService.getInventoryDataExistenceByYear(year)).thenReturn(inventoryDataYearExistence);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(REPORTING_DATA_BASE_CONTROLLER_PATH)
                        .param("year", String.valueOf(year))
                        .param("method", String.valueOf(InventoryCalculationMethodType.NATIONAL))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        InventoryDataYearExistenceDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                InventoryDataYearExistenceDTO.class);

        assertThat(actual).isEqualTo(inventoryDataYearExistence);

        verify(nationalInventoryDataService, times(1)).getInventoryDataExistenceByYear(year);
        verifyNoInteractions(regionalInventoryDataService);
    }

    @Test
    void getInventoryDataExistenceByYear_regional() throws Exception {
        final Year year = Year.of(2023);
        final InventoryDataYearExistenceDTO inventoryDataYearExistence = InventoryDataYearExistenceDTO.builder()
                .year(year)
                .exist(true)
                .build();

        when(regionalInventoryDataService.getInventoryDataExistenceByYear(year)).thenReturn(inventoryDataYearExistence);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get(REPORTING_DATA_BASE_CONTROLLER_PATH)
                        .param("year", String.valueOf(year))
                        .param("method", String.valueOf(InventoryCalculationMethodType.REGIONAL))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        InventoryDataYearExistenceDTO actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                InventoryDataYearExistenceDTO.class);

        assertThat(actual).isEqualTo(inventoryDataYearExistence);

        verify(regionalInventoryDataService, times(1)).getInventoryDataExistenceByYear(year);
        verifyNoInteractions(nationalInventoryDataService);
    }

    @Test
    void getChargingZonesByPostCode() throws Exception {
        String code = "A2 2";
        ChargingZoneDTO chargingZoneDTO = ChargingZoneDTO.builder().code("EA").name("Eastern").build();
        List<ChargingZoneDTO> chargingZones = List.of(chargingZoneDTO);

        when(chargingZoneService.getChargingZonesByPostCode(code)).thenReturn(chargingZones);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/charging-zones")
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        List<ChargingZoneDTO> actualChargingZones =
            Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), ChargingZoneDTO[].class));

        assertThat(actualChargingZones).isNotEmpty().containsExactlyElementsOf(chargingZones);

        verify(chargingZoneService, times(1)).getChargingZonesByPostCode(code);
    }

    @Test
    void getChargingZonesByPostCode_forbidden() throws Exception {
        String code = "A2 2";
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/charging-zones")
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(chargingZoneService);
    }

    @Test
    void getRegionalEmissionCalculationParameters() throws Exception {
        Year year = Year.of(2021);
        String chargingZoneCode = "SE";
        RegionalInventoryEmissionCalculationParamsDTO regionalEmissionCalculationParameters =
            RegionalInventoryEmissionCalculationParamsDTO.builder()
                .emissionFactor(BigDecimal.valueOf(20.2))
                .netCalorificValue(BigDecimal.valueOf(19.87))
                .calculationFactor(BigDecimal.valueOf(24.09))
                .oxidationFactor(BigDecimal.valueOf(0.99))
                .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
                .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
                .build();

        when(regionalInventoryDataService.getRegionalInventoryEmissionCalculationParams(year, chargingZoneCode))
            .thenReturn(Optional.of(regionalEmissionCalculationParameters));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/regional-inventory-data")
                .param("year", String.valueOf(year))
                .param("chargingZoneCode", chargingZoneCode)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        InventoryEmissionCalculationParamsDTO actualResult =
            objectMapper.readValue(result.getResponse().getContentAsString(), RegionalInventoryEmissionCalculationParamsDTO.class);

        assertEquals(regionalEmissionCalculationParameters, actualResult);

        verify(regionalInventoryDataService, times(1)).getRegionalInventoryEmissionCalculationParams(year, chargingZoneCode);
    }

    @Test
    void getRegionalEmissionCalculationParameters_not_found() throws Exception {
        Year year = Year.of(2021);
        String chargingZoneCode = "SE";

        when(regionalInventoryDataService.getRegionalInventoryEmissionCalculationParams(year, chargingZoneCode)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/regional-inventory-data")
                .param("year", String.valueOf(year))
                .param("chargingZoneCode", chargingZoneCode)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(regionalInventoryDataService, times(1)).getRegionalInventoryEmissionCalculationParams(year, chargingZoneCode);
    }

    @Test
    void getRegionalEmissionCalculationParameters_forbidden() throws Exception {
        Year year = Year.of(2021);
        String chargingZoneCode = "SE";
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/regional-inventory-data")
                .param("year", String.valueOf(year))
                .param("chargingZoneCode", chargingZoneCode)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(regionalInventoryDataService);
    }

    @Test
    void getNationalInventoryData() throws Exception {
        Year year = Year.of(2021);
        NationalInventoryDataDTO nationalInventoryData = NationalInventoryDataDTO.builder()
            .sectors(Set.of(NationalInventoryDataDTO.Sector.builder()
                .name("1A1a")
                .fuels(Set.of(NationalInventoryDataDTO.Sector.Fuel.builder()
                    .name("Gas")
                    .emissionCalculationParameters(InventoryEmissionCalculationParamsDTO.builder()
                        .emissionFactor(BigDecimal.valueOf(134.43))
                        .netCalorificValue(BigDecimal.valueOf(45.523))
                        .oxidationFactor(BigDecimal.valueOf(0.98))
                        .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
                        .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
                        .build())
                    .build()))
                .build()))
            .build();

        when(nationalInventoryDataService.getNationalInventoryDataByReportingYear(year)).thenReturn(nationalInventoryData);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/national-inventory-data")
                .param("year", String.valueOf(year))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        NationalInventoryDataDTO actualResult =
            objectMapper.readValue(result.getResponse().getContentAsString(), NationalInventoryDataDTO.class);

        assertEquals(nationalInventoryData, actualResult);

        verify(nationalInventoryDataService, times(1)).getNationalInventoryDataByReportingYear(year);
    }

    @Test
    void getNationalInventoryData_forbidden() throws Exception {
        Year year = Year.of(2021);
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/national-inventory-data")
                .param("year", String.valueOf(year))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(nationalInventoryDataService);
    }

    @Test
    void getCalculationParameterTypesBySourceStreamType() throws Exception {
        SourceStreamType sourceStreamType = SourceStreamType.REFINERIES_MASS_BALANCE;
        List<CalculationParameterMeasurementUnits> measurementUnitsCombinations = List.of(
            CalculationParameterMeasurementUnits.builder()
                .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
                .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
                .build()
        );

        SourceStreamCalculationParametersInfo calculationParametersInfo = SourceStreamCalculationParametersInfo.builder()
            .applicableTypes(sourceStreamType.getCategory().getApplicableCalculationParameterTypes())
            .measurementUnitsCombinations(measurementUnitsCombinations)
            .build();

        when(calculationParametersInfoService.getCalculationParametersInfoBySourceStreamType(sourceStreamType))
            .thenReturn(calculationParametersInfo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/calculation-parameters-info")
                .param("sourceStreamType", String.valueOf(sourceStreamType))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        SourceStreamCalculationParametersInfo actualResult =
            objectMapper.readValue(result.getResponse().getContentAsString(), SourceStreamCalculationParametersInfo.class);

        assertEquals(calculationParametersInfo, actualResult);

        verify(calculationParametersInfoService, times(1))
            .getCalculationParametersInfoBySourceStreamType(sourceStreamType);
    }

    @Test
    void getCalculationParameterTypesBySourceStreamType_forbidden() throws Exception {
        SourceStreamType sourceStreamType = SourceStreamType.REFINERIES_MASS_BALANCE;
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                .get(REPORTING_DATA_BASE_CONTROLLER_PATH + "/calculation-parameters-info")
                .param("sourceStreamType", String.valueOf(sourceStreamType))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(calculationParametersInfoService);
    }
}