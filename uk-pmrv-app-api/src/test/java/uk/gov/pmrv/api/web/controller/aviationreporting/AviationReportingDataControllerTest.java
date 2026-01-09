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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsYearDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationRptAirportsService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class AviationReportingDataControllerTest {

    private static final String BASE_CONTROLLER_PATH = "/v1.0/aviation-reporting-data";

    private static final String AIRPORT_STATES_PATH = "airports";

    @InjectMocks
    private AviationReportingDataController controller;

    @Mock
    private AviationRptAirportsService aviationRptAirportsService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

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
        controller = (AviationReportingDataController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    
    @Test
    void getReportedAirport() throws Exception {
        AviationRptAirportsYearDTO airportsYearDTO  = AviationRptAirportsYearDTO.builder().icaos(Set.of("LEPA", "EGLL")).year(Year.of(2022)).build();
        AviationRptAirportsDTO airport1 = AviationRptAirportsDTO.builder()
            .icao("LEPA")
            .name("PALMA DE MALLORCA")
            .country("Spain")
            .countryType(CountryType.EEA_COUNTRY)
            .build();
        AviationRptAirportsDTO airport2 = AviationRptAirportsDTO.builder()
            .icao("EGLL")
            .name("LONDON HEATHROW")
            .country("United Kingdom")
            .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
            .build();

        when(aviationRptAirportsService.getAirportsByIcaoCodesAndYear(Set.of("LEPA", "EGLL"), Year.of(2022))).thenReturn(List.of(airport1, airport2));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(BASE_CONTROLLER_PATH + "/" + AIRPORT_STATES_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(airportsYearDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].icao").value("LEPA"))
            .andExpect(jsonPath("$[1].icao").value("EGLL"));

        verify(aviationRptAirportsService, times(1)).getAirportsByIcaoCodesAndYear(Set.of("LEPA", "EGLL"), Year.of(2022));
    }

    @Test
    void getReportedAirports_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().build();
        AviationRptAirportsYearDTO airportsYearDTO  = AviationRptAirportsYearDTO.builder().icaos(Set.of("LEPA", "EGLL")).year(Year.of(2022)).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.OPERATOR, RoleTypeConstants.REGULATOR, RoleTypeConstants.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_CONTROLLER_PATH + "/" + AIRPORT_STATES_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(airportsYearDTO)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(aviationRptAirportsService);
    }
}
