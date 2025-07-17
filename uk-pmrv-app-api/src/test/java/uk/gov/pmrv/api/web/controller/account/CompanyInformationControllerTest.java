package uk.gov.pmrv.api.web.controller.account;


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
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.companieshouse.CompanyInformationService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.account.companieshouse.CompanyProfileDTO;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompanyInformationControllerTest {

    private static final String COMPANY_INFORMATION_CONTROLLER_PATH = "/v1.0/company-information";

    @InjectMocks
    private CompanyInformationController controller;

    @Mock
    private CompanyInformationService companyInformationService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (CompanyInformationController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        mapper = new ObjectMapper();
    }

    @Test
    void getCompanyProfileByRegistrationNumber() throws Exception {
        String registrationNumber = "registrationNumber";
        CompanyProfileDTO expectedResponseDTO = CompanyProfileDTO.builder()
            .name("name")
            .registrationNumber(registrationNumber)
            .build();

        when(companyInformationService.getCompanyProfile(registrationNumber, CompanyProfileDTO.class)).thenReturn(expectedResponseDTO);

        MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get(COMPANY_INFORMATION_CONTROLLER_PATH + "/" + registrationNumber))
            .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals(response.getStatus(), HttpStatus.OK.value());

        CompanyProfileDTO actualResponseDTO =
            mapper.readValue(result.getResponse().getContentAsString(), CompanyProfileDTO.class);

        assertEquals(expectedResponseDTO, actualResponseDTO);

        verify(companyInformationService, times(1)).getCompanyProfile(registrationNumber, CompanyProfileDTO.class);
    }

    @Test
    void getCompanyProfileByRegistrationNumber_apiErrors() throws Exception {
        String registrationNumber = "registrationNumber";

        doThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, registrationNumber))
            .doThrow(new BusinessException(ErrorCode.UNAVAILABLE_CH_API, registrationNumber))
            .doThrow(new BusinessException(ErrorCode.INTERNAL_ERROR_CH_API, registrationNumber))
            .when(companyInformationService)
            .getCompanyProfile(registrationNumber, CompanyProfileDTO.class);

        mockMvc
            .perform(MockMvcRequestBuilders.get(COMPANY_INFORMATION_CONTROLLER_PATH + "/" + registrationNumber))
            .andExpect(status().isNotFound());

        mockMvc
            .perform(MockMvcRequestBuilders.get(COMPANY_INFORMATION_CONTROLLER_PATH + "/" + registrationNumber))
            .andExpect(status().isServiceUnavailable());

        mockMvc
            .perform(MockMvcRequestBuilders.get(COMPANY_INFORMATION_CONTROLLER_PATH + "/" + registrationNumber))
            .andExpect(status().isInternalServerError());

        verify(companyInformationService, times(3)).getCompanyProfile(registrationNumber, CompanyProfileDTO.class);
    }

}