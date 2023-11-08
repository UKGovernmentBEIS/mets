package uk.gov.pmrv.api.web.controller.emp;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchCriteria;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchResults;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.AircraftTypeQueryService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;

@ExtendWith(MockitoExtension.class)
class AircraftTypesControllerTest {

    @InjectMocks
    private AircraftTypesController aircraftTypesController;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private AircraftTypeQueryService aircraftTypeQueryService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(aircraftTypesController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        aircraftTypesController = (AircraftTypesController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(aircraftTypesController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getAircraftTypes() throws Exception {
        AircraftTypeSearchCriteria searchCriteria = AircraftTypeSearchCriteria.builder()
            .term("747")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build())
            .build();
        AircraftTypeSearchResults searchResults = AircraftTypeSearchResults.builder()
            .aircraftTypes(List.of(
                new AircraftTypeDTO("BOEING", "747", "B747"),
                new AircraftTypeDTO("SHANGHAI", "MD-747", "MD747")
            ))
            .total(2L)
            .build();


        when(aircraftTypeQueryService.getAircraftTypesBySearchCriteria(searchCriteria)).thenReturn(searchResults);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/v1.0/aircraft-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteria)))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals(objectMapper.writeValueAsString(searchResults), mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getCurrentUserDocumentTemplates_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("userId")
            .build();
        AircraftTypeSearchCriteria searchCriteria = AircraftTypeSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{OPERATOR, REGULATOR, VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1.0/aircraft-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteria)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(aircraftTypeQueryService);
    }
}