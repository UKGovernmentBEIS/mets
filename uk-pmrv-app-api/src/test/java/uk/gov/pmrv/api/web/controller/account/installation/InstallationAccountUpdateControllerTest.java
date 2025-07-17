package uk.gov.pmrv.api.web.controller.account.installation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyAddressDTO;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateFaStatusDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateInstallationNameDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateRegistryIdDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateSiteNameDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateSopIdDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.account.transform.StringToAccountTypeEnumConverter;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.service.CountryService;
import uk.gov.netz.api.referencedata.service.CountryValidator;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.controller.utils.TestConstrainValidatorFactory;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InstallationAccountUpdateControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/installation/accounts";

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    private InstallationAccountUpdateController controller;

    @Mock
    private InstallationAccountUpdateService installationAccountUpdateService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private CountryService countryService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
            new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (InstallationAccountUpdateController) aopProxy.getProxy();

        LocalValidatorFactoryBean validatorFactoryBean = mockValidatorFactoryBean();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setValidator(validatorFactoryBean)
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void updateAccountSiteName() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        AccountUpdateSiteNameDTO siteNameDTO = AccountUpdateSiteNameDTO.builder().siteName("newSiteName").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/site-name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(siteNameDTO)))
            .andExpect(status().isNoContent());

        verify(installationAccountUpdateService, times(1)).updateAccountSiteName(accountId, siteNameDTO.getSiteName());
    }

    @Test
    void updateAccountSiteName_forbidden() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        AccountUpdateSiteNameDTO siteNameDTO = AccountUpdateSiteNameDTO.builder().siteName("newSiteName").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateInstallationAccountSiteName", String.valueOf(accountId), null, null);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/site-name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(siteNameDTO)))
            .andExpect(status().isForbidden());

        verify(installationAccountUpdateService, never())
            .updateAccountSiteName(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void updateAccountRegistryId() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        AccountUpdateRegistryIdDTO registryIdDTO = AccountUpdateRegistryIdDTO.builder().registryId(1234567).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/registry-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registryIdDTO)))
            .andExpect(status().isNoContent());

        verify(installationAccountUpdateService, times(1)).updateAccountRegistryId(accountId, registryIdDTO.getRegistryId());
    }

    @Test
    void updateAccountRegistryId_forbidden() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        AccountUpdateRegistryIdDTO registryIdDTO = AccountUpdateRegistryIdDTO.builder().registryId(1234567).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateInstallationAccountRegistryId", String.valueOf(accountId), null, null);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/registry-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registryIdDTO)))
            .andExpect(status().isForbidden());

        verify(installationAccountUpdateService, never())
            .updateAccountRegistryId(Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void updateAccountSopId() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        AccountUpdateSopIdDTO sopIdDTO = AccountUpdateSopIdDTO.builder().sopId(1234567899L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/sop-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sopIdDTO)))
            .andExpect(status().isNoContent());

        verify(installationAccountUpdateService, times(1)).updateAccountSopId(accountId, sopIdDTO.getSopId());
    }

    @Test
    void updateAccountSopId_forbidden() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        AccountUpdateSopIdDTO sopIdDTO = AccountUpdateSopIdDTO.builder().sopId(1234567899L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateInstallationAccountSopId", String.valueOf(accountId), null, null);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/sop-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sopIdDTO)))
            .andExpect(status().isForbidden());

        verify(installationAccountUpdateService, never())
            .updateAccountRegistryId(Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void updateAccountAddress() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        LocationOnShoreDTO address =
            LocationOnShoreDTO.builder().type(LocationType.ONSHORE).gridReference("te12345").address(AddressDTO.builder()
                .city("city").country("GR").line1("line1").line2("line2").postcode("postcode").build()).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/address")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(address)))
            .andExpect(status().isNoContent());

        verify(installationAccountUpdateService, times(1)).updateAccountAddress(accountId, address);
        verify(countryService, times(1)).getReferenceData();
    }

    @Test
    void updateAccountAddress_forbidden() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        LocationOnShoreDTO address =
            LocationOnShoreDTO.builder().type(LocationType.ONSHORE).gridReference("te12345").address(AddressDTO.builder()
                .city("city").country("GR").line1("line1").line2("line2").postcode("postcode").build()).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateInstallationAccountAddress", String.valueOf(accountId), null, null);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/address")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(address)))
            .andExpect(status().isForbidden());

        verify(installationAccountUpdateService, never()).updateAccountAddress(Mockito.anyLong(), Mockito.any());
        verify(countryService, times(1)).getReferenceData();
    }

    @Test
    void updateAccountLegalEntity() throws Exception {
        final var user = AppUser.builder().build();
        final var accountId = 1L;
        final var legalEntityDTO = LegalEntityDTO.builder()
            .name("TEST_LE")
            .type(LegalEntityType.LIMITED_COMPANY)
            .referenceNumber("TEST_LE_REG_NO")
            .address(
                AddressDTO.builder()
                    .city("TEST_LE_CITY")
                    .country("GR")
                    .line1("TEST_LE_L1")
                    .postcode("TEST_LE_POSTCODE")
                    .build()
            )
            .holdingCompany(
                HoldingCompanyDTO.builder()
                    .name("TEST_HC")
                    .address(HoldingCompanyAddressDTO.builder()
                        .city("TEST_HC_CITY")
                        .line1("TEST_HC_L1")
                        .postcode("TEST_HC_POSTCODE")
                        .build())
                    .build()
            )
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));

        mockMvc.perform(
            MockMvcRequestBuilders
                .post(CONTROLLER_PATH + "/" + accountId + "/legal-entity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(legalEntityDTO))
        ).andExpect(status().isNoContent());

        verify(installationAccountUpdateService, times(1)).updateAccountLegalEntity(accountId, legalEntityDTO);
        verify(countryService, times(1)).getReferenceData();
    }

    @Test
    void updateInstallationName() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        AccountUpdateInstallationNameDTO installationNameDTO = AccountUpdateInstallationNameDTO.builder()
            .installationName("newInstallationName").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(installationNameDTO)))
            .andExpect(status().isNoContent());

        verify(installationAccountUpdateService, times(1))
            .updateInstallationName(accountId, installationNameDTO.getInstallationName());
    }

    @Test
    void updateInstallationName_forbidden() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        AccountUpdateInstallationNameDTO installationNameDTO = AccountUpdateInstallationNameDTO.builder()
            .installationName("newInstallationName").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateInstallationName", String.valueOf(accountId), null, null);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(installationNameDTO)))
            .andExpect(status().isForbidden());

        verify(installationAccountUpdateService, never())
            .updateAccountSiteName(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void updateFreeAllocationStatus() throws Exception {
        InstallationAccountUpdateController controller = new InstallationAccountUpdateController(installationAccountUpdateService);
        setupRoleBasedAuthentication(controller);
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        Long accountId = 1L;
        AccountUpdateFaStatusDTO updateFaStatusDTO = AccountUpdateFaStatusDTO.builder()
            .faStatus(true)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .put(CONTROLLER_PATH + "/" + accountId + "/free-allocation-status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateFaStatusDTO)))
            .andExpect(status().isNoContent());

        verify(installationAccountUpdateService, times(1))
            .updateFaStatus(accountId, updateFaStatusDTO);
    }

    @Test
    void updateFreeAllocationStatus_forbidden() throws Exception {
        InstallationAccountUpdateController controller = new InstallationAccountUpdateController(installationAccountUpdateService);
        setupRoleBasedAuthentication(controller);
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();
        long accountId = 1L;
        AccountUpdateFaStatusDTO updateFaStatusDTO = AccountUpdateFaStatusDTO.builder()
            .faStatus(true)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(user, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(
                MockMvcRequestBuilders
                    .put(CONTROLLER_PATH + "/" + accountId + "/free-allocation-status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateFaStatusDTO)))
            .andExpect(status().isForbidden());

        verify(installationAccountUpdateService, never())
            .updateFaStatus(Mockito.anyLong(), Mockito.any(AccountUpdateFaStatusDTO.class));
    }

    private LocalValidatorFactoryBean mockValidatorFactoryBean() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        MockServletContext servletContext = new MockServletContext();
        GenericWebApplicationContext context = new GenericWebApplicationContext(servletContext);
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();

        beanFactory.registerSingleton(CountryValidator.class.getCanonicalName(), new CountryValidator(countryService));

        context.refresh();
        validatorFactoryBean.setApplicationContext(context);
        TestConstrainValidatorFactory constraintValidatorFactory = new TestConstrainValidatorFactory(context);
        validatorFactoryBean.setConstraintValidatorFactory(constraintValidatorFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    private void setupRoleBasedAuthentication(InstallationAccountUpdateController controller) {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (InstallationAccountUpdateController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(new StringToAccountTypeEnumConverter());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
            .build();

        objectMapper = new ObjectMapper();
    }
}
