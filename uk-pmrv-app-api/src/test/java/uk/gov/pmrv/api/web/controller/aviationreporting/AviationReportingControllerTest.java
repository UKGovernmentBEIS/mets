package uk.gov.pmrv.api.web.controller.aviationreporting;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.dto.AviationAerCorsiaEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.dto.AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaAerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaInternationalFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaInternationalFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaStandardFuelsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.dto.AviationAerEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerNonDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerNonDomesticFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.StandardFuelsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AviationReportingControllerTest {

    private static final String BASE_CONTROLLER_PATH = "/v1.0/aviation-reporting";

    private static final String TOTAL_EMISSIONS_PATH = "ukets/total-emissions";

    private static final String STANDARD_FUELS_TOTAL_EMISSIONS_PATH = "ukets/standard-fuels-emissions";

    private static final String AERODROME_PAIRS_EMISSIONS_PATH = "ukets/aerodrome-pairs-emissions";

    private static final String DOMESTIC_FLIGHTS_EMISSIONS_PATH = "ukets/domestic-flights-emissions";

    private static final String NON_DOMESTIC_FLIGHTS_EMISSIONS_PATH = "ukets/non-domestic-flights-emissions";

    private static final String TOTAL_EMISSIONS_CORSIA_PATH = "corsia/total-emissions";

    private static final String AERODROME_PAIRS_EMISSIONS_CORSIA_PATH = "corsia/aerodrome-pairs-emissions";

    private static final String STANDARD_FUELS_TOTAL_EMISSIONS_CORSIA_PATH = "corsia/standard-fuels-emissions";

    private static final String CORSIA_FLIGHTS_EMISSIONS_PATH = "corsia/international-flights-emissions";


    @InjectMocks
    private AviationReportingController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;

    @Mock
    private AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaEmissionsCalculationService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
                authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (AviationReportingController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getTotalEmissionsUkEts() throws Exception {
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();
        AviationAerTotalEmissions expectedResponse = AviationAerTotalEmissions.builder()
                .numFlightsCoveredByUkEts(30)
                .standardFuelEmissions(new BigDecimal("9350.00"))
                .reductionClaimEmissions(new BigDecimal("4725"))
                .totalEmissions(BigDecimal.valueOf(4625))
                .build();

        when(aviationAerUkEtsEmissionsCalculationService.calculateTotalEmissions(aviationAerEmissionsCalculationDTO)).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + TOTAL_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numFlightsCoveredByUkEts").value(30))
                .andExpect(jsonPath("$.standardFuelEmissions").value(9350.00))
                .andExpect(jsonPath("$.reductionClaimEmissions").value(4725))
                .andExpect(jsonPath("$.totalEmissions").value(4625));
    }

    @Test
    void getTotalEmissionsUkEts_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_CONTROLLER_PATH + "/" + TOTAL_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerUkEtsEmissionsCalculationService);
    }

    @Test
    void calculateStandardFuelsTotalEmissions() throws Exception {
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();
        final List<StandardFuelsTotalEmissions> standardFuelsTotalEmissions = List.of(StandardFuelsTotalEmissions.builder()
                .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                .emissionsFactor(AviationAerUkEtsFuelType.AVIATION_GASOLINE.getEmissionFactor())
                .netCalorificValue(AviationAerUkEtsFuelType.AVIATION_GASOLINE.getNetCalorificValue())
                .fuelConsumption(BigDecimal.valueOf(123.45))
                .emissions(BigDecimal.valueOf(382.695))
                .build());

        when(aviationAerUkEtsEmissionsCalculationService.calculateStandardFuelsTotalEmissions(aviationAerEmissionsCalculationDTO))
                .thenReturn(standardFuelsTotalEmissions);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + STANDARD_FUELS_TOTAL_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fuelType").value(AviationAerUkEtsFuelType.AVIATION_GASOLINE.name()))
                .andExpect(jsonPath("$[0].emissionsFactor").value(BigDecimal.valueOf(3.10)))
                .andExpect(jsonPath("$[0].netCalorificValue").value(BigDecimal.valueOf(43.30)))
                .andExpect(jsonPath("$[0].fuelConsumption").value(123.45))
                .andExpect(jsonPath("$[0].emissions").value(382.695));

        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateStandardFuelsTotalEmissions(aviationAerEmissionsCalculationDTO);
    }

    @Test
    void calculateStandardFuelsTotalEmissions_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_CONTROLLER_PATH + "/" + STANDARD_FUELS_TOTAL_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerUkEtsEmissionsCalculationService);
    }

    @Test
    void calculateAerodromePairsTotalEmissions() throws Exception {
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();
        final List<AerodromePairsTotalEmissions> aerodromePairsTotalEmissions = List.of(AerodromePairsTotalEmissions.builder()
                .departureAirport(AviationRptAirportsDTO.builder()
                        .icao("ICAO1")
                        .name("Airport1")
                        .country("country1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state")
                        .build())
                .arrivalAirport(AviationRptAirportsDTO.builder()
                        .icao("ICAO2")
                        .name("Airport2")
                        .country("country2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                        .state("state")
                        .build())
                .flightsNumber(20)
                .emissions(BigDecimal.valueOf(123.45))
                .build());

        when(aviationAerUkEtsEmissionsCalculationService.calculateAerodromePairsTotalEmissions(aviationAerEmissionsCalculationDTO))
                .thenReturn(aerodromePairsTotalEmissions);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + AERODROME_PAIRS_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departureAirport.icao").value("ICAO1"))
                .andExpect(jsonPath("$[0].departureAirport.name").value("Airport1"))
                .andExpect(jsonPath("$[0].arrivalAirport.icao").value("ICAO2"))
                .andExpect(jsonPath("$[0].arrivalAirport.name").value("Airport2"))
                .andExpect(jsonPath("$[0].flightsNumber").value(20))
                .andExpect(jsonPath("$[0].emissions").value(123.45));

        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateAerodromePairsTotalEmissions(aviationAerEmissionsCalculationDTO);
    }

    @Test
    void calculateAerodromePairsTotalEmissions_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_CONTROLLER_PATH + "/" + AERODROME_PAIRS_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerUkEtsEmissionsCalculationService);
    }

    @Test
    void getDomesticFlightsEmissionsUkEts() throws Exception {
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();

        List<AviationAerDomesticFlightsEmissionsDetails> domesticFlightsEmissionsDetails = List.of(
                AviationAerDomesticFlightsEmissionsDetails.builder()
                        .country("United Kingdom")
                        .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                                .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                .flightsNumber(20)
                                .fuelConsumption(BigDecimal.valueOf(100.00))
                                .emissions(BigDecimal.valueOf(315.00))
                                .build())
                        .build(),
                AviationAerDomesticFlightsEmissionsDetails.builder()
                        .country("United Kingdom")
                        .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                                .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                .flightsNumber(30)
                                .fuelConsumption(BigDecimal.valueOf(200.00))
                                .emissions(BigDecimal.valueOf(620.00))
                                .build())
                        .build(),
                AviationAerDomesticFlightsEmissionsDetails.builder()
                        .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                                .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                .flightsNumber(40)
                                .fuelConsumption(BigDecimal.valueOf(100.00))
                                .emissions(BigDecimal.valueOf(310.00))
                                .build())
                        .build()
        );

        final AviationAerDomesticFlightsEmissions domesticFlightsEmissions = AviationAerDomesticFlightsEmissions.builder()
                .domesticFlightsEmissionsDetails(domesticFlightsEmissionsDetails)
                .totalEmissions(BigDecimal.valueOf(1245.00))
                .build();

        when(aviationAerUkEtsEmissionsCalculationService.calculateDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO))
                .thenReturn(domesticFlightsEmissions);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + DOMESTIC_FLIGHTS_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmissions").value(BigDecimal.valueOf(1245.00)))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[0].fuelType").value(AviationAerUkEtsFuelType.JET_KEROSENE.name()))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[0].flightsNumber").value(20))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[0].fuelConsumption").value(BigDecimal.valueOf(100.00)))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[0].emissions").value(BigDecimal.valueOf(315.00)))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[1].fuelType").value(AviationAerUkEtsFuelType.JET_GASOLINE.name()))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[1].flightsNumber").value(30))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[1].fuelConsumption").value(BigDecimal.valueOf(200.00)))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[1].emissions").value(BigDecimal.valueOf(620.00)))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[2].fuelType").value(AviationAerUkEtsFuelType.AVIATION_GASOLINE.name()))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[2].flightsNumber").value(40))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[2].fuelConsumption").value(BigDecimal.valueOf(100.00)))
                .andExpect(jsonPath("$.domesticFlightsEmissionsDetails[2].emissions").value(BigDecimal.valueOf(310.00)));


        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO);
    }

    @Test
    void getDomesticFlightsEmissionsUkEts_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_CONTROLLER_PATH + "/" + DOMESTIC_FLIGHTS_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerUkEtsEmissionsCalculationService);
    }

    @Test
    void getNonDomesticFlightsEmissionsUkEts() throws Exception {
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();

        List<AviationAerNonDomesticFlightsEmissionsDetails> nonDomesticFlightsEmissionsDetails = List.of(
                AviationAerNonDomesticFlightsEmissionsDetails.builder()
                        .departureCountry("United Kingdom")
                        .arrivalCountry("Greece")
                        .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                                .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                .flightsNumber(20)
                                .fuelConsumption(BigDecimal.valueOf(100.00))
                                .emissions(BigDecimal.valueOf(315.00))
                                .build())
                        .build(),
                AviationAerNonDomesticFlightsEmissionsDetails.builder()
                        .departureCountry("United Kingdom")
                        .arrivalCountry("Germany")
                        .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                                .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                .flightsNumber(30)
                                .fuelConsumption(BigDecimal.valueOf(200.00))
                                .emissions(BigDecimal.valueOf(620.00))
                                .build())
                        .build(),
                AviationAerNonDomesticFlightsEmissionsDetails.builder()
                        .departureCountry("United Kingdom")
                        .arrivalCountry("France")
                        .flightsEmissionsDetails(AviationAerFlightsEmissionsDetails.builder()
                                .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                .flightsNumber(40)
                                .fuelConsumption(BigDecimal.valueOf(100.00))
                                .emissions(BigDecimal.valueOf(310.00))
                                .build())
                        .build()
        );

        final AviationAerNonDomesticFlightsEmissions nonDomesticFlightsEmissions = AviationAerNonDomesticFlightsEmissions.builder()
                .nonDomesticFlightsEmissionsDetails(nonDomesticFlightsEmissionsDetails)
                .totalEmissions(BigDecimal.valueOf(1245.00))
                .build();

        when(aviationAerUkEtsEmissionsCalculationService.calculateNonDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO))
                .thenReturn(nonDomesticFlightsEmissions);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + NON_DOMESTIC_FLIGHTS_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmissions").value(BigDecimal.valueOf(1245.00)))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[0].fuelType").value(AviationAerUkEtsFuelType.JET_KEROSENE.name()))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[0].flightsNumber").value(20))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[0].fuelConsumption").value(BigDecimal.valueOf(100.00)))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[0].emissions").value(BigDecimal.valueOf(315.00)))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[1].fuelType").value(AviationAerUkEtsFuelType.JET_GASOLINE.name()))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[1].flightsNumber").value(30))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[1].fuelConsumption").value(BigDecimal.valueOf(200.00)))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[1].emissions").value(BigDecimal.valueOf(620.00)))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[2].fuelType").value(AviationAerUkEtsFuelType.AVIATION_GASOLINE.name()))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[2].flightsNumber").value(40))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[2].fuelConsumption").value(BigDecimal.valueOf(100.00)))
                .andExpect(jsonPath("$.nonDomesticFlightsEmissionsDetails[2].emissions").value(BigDecimal.valueOf(310.00)));


        verify(aviationAerUkEtsEmissionsCalculationService, times(1)).calculateNonDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO);
    }

    @Test
    void getNonDomesticFlightsEmissionsUkEts_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createEmissionCalculationDtoObject();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_CONTROLLER_PATH + "/" + NON_DOMESTIC_FLIGHTS_EMISSIONS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerUkEtsEmissionsCalculationService);
    }

    @Test
    void getTotalEmissionsCorsia() throws Exception {
        AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO = this.createCorsiaEmissionCalculationDTO();
        AviationAerCorsiaTotalEmissions expectedResponse = AviationAerCorsiaTotalEmissions.builder()
                .allFlightsNumber(30)
                .allFlightsEmissions(BigDecimal.valueOf(1243))
                .offsetFlightsNumber(10)
                .offsetFlightsEmissions(BigDecimal.valueOf(466))
                .nonOffsetFlightsNumber(20)
                .nonOffsetFlightsEmissions(BigDecimal.valueOf(776))
                .emissionsReductionClaim(BigDecimal.valueOf(200.25))
                .build();

        when(aviationAerCorsiaEmissionsCalculationService.calculateTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO)).thenReturn(expectedResponse);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + TOTAL_EMISSIONS_CORSIA_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerCorsiaEmissionsCalculationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allFlightsNumber").value(30))
                .andExpect(jsonPath("$.allFlightsEmissions").value(1243))
                .andExpect(jsonPath("$.offsetFlightsNumber").value(10))
                .andExpect(jsonPath("$.offsetFlightsEmissions").value(466))
                .andExpect(jsonPath("$.nonOffsetFlightsNumber").value(20))
                .andExpect(jsonPath("$.nonOffsetFlightsEmissions").value(776))
                .andExpect(jsonPath("$.emissionsReductionClaim").value(200.25));

        verify(aviationAerCorsiaEmissionsCalculationService, times(1)).calculateTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO);
    }

    @Test
    void getTotalEmissionsCorsia_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO = this.createCorsiaEmissionCalculationDTO();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_CONTROLLER_PATH + "/" + TOTAL_EMISSIONS_CORSIA_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerCorsiaEmissionsCalculationDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerCorsiaEmissionsCalculationService);
    }

    @Test
    void calculateAerodromePairsTotalEmissionsCorsia() throws Exception {
        AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO = this.createCorsiaEmissionCalculationDTO();
        final List<AviationAerCorsiaAerodromePairsTotalEmissions> aerodromePairsTotalEmissions = List.of(AviationAerCorsiaAerodromePairsTotalEmissions.builder()
                        .departureAirport(AviationRptAirportsDTO.builder()
                                .icao("ICAO1")
                                .name("Airport1")
                                .country("country1")
                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                .state("state")
                                .build())
                        .arrivalAirport(AviationRptAirportsDTO.builder()
                                .icao("ICAO2")
                                .name("Airport2")
                                .country("country2")
                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                .state("state")
                                .build())
                        .flightsNumber(20)
                        .emissions(BigDecimal.valueOf(123.45))
                        .offset(Boolean.TRUE)
                        .build(),
                AviationAerCorsiaAerodromePairsTotalEmissions.builder()
                        .departureAirport(AviationRptAirportsDTO.builder()
                                .icao("ICAO3")
                                .name("Airport3")
                                .country("country3")
                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                .state("state")
                                .build())
                        .arrivalAirport(AviationRptAirportsDTO.builder()
                                .icao("ICAO4")
                                .name("Airport4")
                                .country("country4")
                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                .state("state")
                                .build())
                        .flightsNumber(50)
                        .emissions(BigDecimal.valueOf(200.25))
                        .offset(Boolean.FALSE)
                        .build()
        );

        when(aviationAerCorsiaEmissionsCalculationService.calculateAerodromePairsTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO))
                .thenReturn(aerodromePairsTotalEmissions);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + AERODROME_PAIRS_EMISSIONS_CORSIA_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerCorsiaEmissionsCalculationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departureAirport.icao").value("ICAO1"))
                .andExpect(jsonPath("$[0].departureAirport.name").value("Airport1"))
                .andExpect(jsonPath("$[0].arrivalAirport.icao").value("ICAO2"))
                .andExpect(jsonPath("$[0].arrivalAirport.name").value("Airport2"))
                .andExpect(jsonPath("$[0].flightsNumber").value(20))
                .andExpect(jsonPath("$[0].emissions").value(123.45))
                .andExpect(jsonPath("$[0].offset").value(Boolean.TRUE))
                .andExpect(jsonPath("$[1].departureAirport.icao").value("ICAO3"))
                .andExpect(jsonPath("$[1].departureAirport.name").value("Airport3"))
                .andExpect(jsonPath("$[1].arrivalAirport.icao").value("ICAO4"))
                .andExpect(jsonPath("$[1].arrivalAirport.name").value("Airport4"))
                .andExpect(jsonPath("$[1].flightsNumber").value(50))
                .andExpect(jsonPath("$[1].emissions").value(200.25))
                .andExpect(jsonPath("$[1].offset").value(Boolean.FALSE));

        verify(aviationAerCorsiaEmissionsCalculationService, times(1)).calculateAerodromePairsTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO);
    }

    @Test
    void calculateAerodromePairsTotalEmissionsCorsia_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO = this.createCorsiaEmissionCalculationDTO();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_CONTROLLER_PATH + "/" + AERODROME_PAIRS_EMISSIONS_CORSIA_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerCorsiaEmissionsCalculationDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerCorsiaEmissionsCalculationService);
    }

    @Test
    void calculateStandardFuelsTotalEmissionsCorsia() throws Exception {
        AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO = this.createCorsiaEmissionCalculationDTO();
        final List<AviationAerCorsiaStandardFuelsTotalEmissions> standardFuelsTotalEmissions = List.of(AviationAerCorsiaStandardFuelsTotalEmissions.builder()
                .fuelType(AviationAerCorsiaFuelType.AVIATION_GASOLINE)
                .emissionsFactor(AviationAerUkEtsFuelType.AVIATION_GASOLINE.getEmissionFactor())
                .fuelConsumption(BigDecimal.valueOf(123.45))
                .emissions(BigDecimal.valueOf(382.695))
                .build());

        when(aviationAerCorsiaEmissionsCalculationService.calculateStandardFuelsTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO))
                .thenReturn(standardFuelsTotalEmissions);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + STANDARD_FUELS_TOTAL_EMISSIONS_CORSIA_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerCorsiaEmissionsCalculationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fuelType").value(AviationAerUkEtsFuelType.AVIATION_GASOLINE.name()))
                .andExpect(jsonPath("$[0].emissionsFactor").value(BigDecimal.valueOf(3.10)))
                .andExpect(jsonPath("$[0].fuelConsumption").value(123.45))
                .andExpect(jsonPath("$[0].emissions").value(382.695));

        verify(aviationAerCorsiaEmissionsCalculationService, times(1)).calculateStandardFuelsTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO);
    }

    @Test
    void calculateStandardFuelsTotalEmissionsCorsia_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO = this.createCorsiaEmissionCalculationDTO();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_CONTROLLER_PATH + "/" + STANDARD_FUELS_TOTAL_EMISSIONS_CORSIA_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aviationAerCorsiaEmissionsCalculationDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerCorsiaEmissionsCalculationService);
    }

    @Test
    void getCorsiaFlightsEmissions() throws Exception {
        AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createInternationalFlightsEmissionsCalculationDTO();

        List<AviationAerCorsiaInternationalFlightsEmissionsDetails> corsiaFlightsEmissionsDetails = List.of(
            AviationAerCorsiaInternationalFlightsEmissionsDetails.builder()
                .departureState("France")
                .arrivalState("Greece")
                .flightsEmissionsDetails(AviationAerCorsiaFlightsEmissionsDetails.builder()
                    .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                    .flightsNumber(20)
                    .fuelConsumption(BigDecimal.valueOf(100.00))
                    .emissions(BigDecimal.valueOf(315.00))
                    .build())
                .build(),
            AviationAerCorsiaInternationalFlightsEmissionsDetails.builder()
                .departureState("France")
                .arrivalState("Germany")
                .flightsEmissionsDetails(AviationAerCorsiaFlightsEmissionsDetails.builder()
                    .fuelType(AviationAerCorsiaFuelType.JET_GASOLINE)
                    .flightsNumber(30)
                    .fuelConsumption(BigDecimal.valueOf(200.00))
                    .emissions(BigDecimal.valueOf(620.00))
                    .build())
                .build()
        );

        final AviationAerCorsiaInternationalFlightsEmissions corsiaFlightsEmissions = AviationAerCorsiaInternationalFlightsEmissions.builder()
            .flightsEmissionsDetails(corsiaFlightsEmissionsDetails)
            .build();

        when(aviationAerCorsiaEmissionsCalculationService.calculateInternationalFlightsEmissions(aviationAerEmissionsCalculationDTO))
            .thenReturn(corsiaFlightsEmissions);

        mockMvc.perform(post(BASE_CONTROLLER_PATH + "/" + CORSIA_FLIGHTS_EMISSIONS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.flightsEmissionsDetails[0].fuelType").value(AviationAerCorsiaFuelType.JET_KEROSENE.name()))
            .andExpect(jsonPath("$.flightsEmissionsDetails[0].flightsNumber").value(20))
            .andExpect(jsonPath("$.flightsEmissionsDetails[0].fuelConsumption").value(BigDecimal.valueOf(100.00)))
            .andExpect(jsonPath("$.flightsEmissionsDetails[0].emissions").value(BigDecimal.valueOf(315.00)))
            .andExpect(jsonPath("$.flightsEmissionsDetails[1].fuelType").value(AviationAerCorsiaFuelType.JET_GASOLINE.name()))
            .andExpect(jsonPath("$.flightsEmissionsDetails[1].flightsNumber").value(30))
            .andExpect(jsonPath("$.flightsEmissionsDetails[1].fuelConsumption").value(BigDecimal.valueOf(200.00)))
            .andExpect(jsonPath("$.flightsEmissionsDetails[1].emissions").value(BigDecimal.valueOf(620.00)));


        verify(aviationAerCorsiaEmissionsCalculationService, times(1)).calculateInternationalFlightsEmissions(aviationAerEmissionsCalculationDTO);
    }

    @Test
    void getCorsiaFlightsEmissions_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = this.createInternationalFlightsEmissionsCalculationDTO();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.OPERATOR, RoleType.REGULATOR, RoleType.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_CONTROLLER_PATH + "/" + CORSIA_FLIGHTS_EMISSIONS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aviationAerEmissionsCalculationDTO)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAerCorsiaEmissionsCalculationService);
    }

    private AviationAerEmissionsCalculationDTO createEmissionCalculationDtoObject() {
        return AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                .airportFrom(AviationRptAirportsDTO.builder()
                                        .icao("icaoFrom")
                                        .name("nameFrom")
                                        .country("countryFrom")
                                        .countryType(CountryType.EEA_COUNTRY)
                                        .state("state")                                        
                                        .build())
                                .airportTo(AviationRptAirportsDTO.builder()
                                        .icao("icaoTo")
                                        .name("nameTo")
                                        .country("countryTo")
                                        .countryType(CountryType.EEA_COUNTRY)
                                        .state("state")
                                        .build())
                                .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                .fuelConsumption(BigDecimal.valueOf(123.45))
                                .flightsNumber(10)
                                .build()))
                        .build())
                .saf(AviationAerSaf.builder()
                        .exist(Boolean.FALSE)
                        .build())
                .build();
    }

    private AviationAerCorsiaEmissionsCalculationDTO createCorsiaEmissionCalculationDTO() {
        return AviationAerCorsiaEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .icao("icaoFrom1")
                                                .name("nameFrom1")
                                                .country("countryFrom1")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .state("state")
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .icao("icaoTo2")
                                                .name("nameTo2")
                                                .country("countryTo2")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .state("state")
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(BigDecimal.valueOf(150.45))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .icao("icaoFrom3")
                                                .name("nameFrom3")
                                                .country("countryFrom3")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .state("state")
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .icao("icaoTo4")
                                                .name("nameTo4")
                                                .country("countryTo4")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .state("state")
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(BigDecimal.valueOf(250.45))
                                        .flightsNumber(20)
                                        .build()
                        ))
                        .build())
                .emissionsReductionClaim(BigDecimal.valueOf(200.25))
                .build();
    }

    private AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO createInternationalFlightsEmissionsCalculationDTO() {
        final AviationAerCorsiaEmissionsCalculationDTO corsiaEmissionCalculationDTO = this.createCorsiaEmissionCalculationDTO();
        return AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(corsiaEmissionCalculationDTO.getAggregatedEmissionsData())
                .emissionsReductionClaim(corsiaEmissionCalculationDTO.getEmissionsReductionClaim())
                .year(Year.of(2023))
                .build();

    }
}
