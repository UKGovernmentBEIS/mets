package uk.gov.pmrv.api.web.controller.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyAddressDTO;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.service.CountryService;
import uk.gov.netz.api.referencedata.service.CountryValidator;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.controller.utils.TestConstrainValidatorFactory;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestCreateActionProcessDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionResourceTypeDelegator;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionResourceTypeHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningSubmitApplicationCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountSubmitter;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    private static final String BASE_PATH = "/v1.0/requests";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestController requestController;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private RequestCreateActionResourceTypeDelegator requestCreateActionResourceTypeDelegator;

    @Mock
    private RequestCreateActionResourceTypeHandler handler;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RequestQueryService requestQueryService;

    private ObjectMapper mapper;
    
    @Mock
    private CountryService countryService;
    
    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(requestController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        requestController = (RequestController) aopProxy.getProxy();
        
        LocalValidatorFactoryBean validatorFactoryBean = mockValidatorFactoryBean();
        
        mockMvc = MockMvcBuilders.standaloneSetup(requestController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setValidator(validatorFactoryBean)
                .build();
    }

    @Test
    void processRequestCreateAction() throws Exception {
        AppUser appUser = AppUser.builder().userId("id").build();
        InstallationAccountOpeningSubmitApplicationCreateActionPayload payload = InstallationAccountOpeningSubmitApplicationCreateActionPayload.builder()
            .payloadType(RequestCreateActionPayloadType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD)
            .accountPayload(InstallationAccountPayload.builder()
                    .accountType(AccountType.INSTALLATION)
                    .applicationType(ApplicationType.NEW_PERMIT)
                    .name("name")
                    .submitter(InstallationAccountSubmitter.builder().name("name").email("test@test.gr").build())
                    .siteName("site name")
                    .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                    .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                    .commencementDate(LocalDate.of(2020,8,6))
                    .location(LocationOnShoreDTO.builder()
                            .type(LocationType.ONSHORE)
                            .gridReference("NN166718")
                            .address(AddressDTO.builder().city("city").country("GR").line1("lin1").postcode("14").build()).build())
                    .legalEntity(LegalEntityDTO.builder()
                        .type(LegalEntityType.PARTNERSHIP)
                        .name("name")
                        .referenceNumber("09546038")
                        .address(AddressDTO.builder().city("city").country("GR").line1("lin1").postcode("14").build())
                        .holdingCompany(HoldingCompanyDTO.builder()
                            .name("Holding Limited")
                            .registrationNumber("123456")
                            .address(HoldingCompanyAddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .postcode("14")
                                .build())
                            .build())
                        .build())
                    .build())
            .build();
        RequestCreateActionProcessDTO requestCreateActionProcessDTO = RequestCreateActionProcessDTO.builder()
                .requestCreateActionType(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION)
                .requestCreateActionPayload(payload)
                .build();

        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(requestCreateActionResourceTypeDelegator.getResourceTypeHandler(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION)).thenReturn(handler);

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_PATH)
                .content(mapper.writeValueAsString(requestCreateActionProcessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestCreateActionResourceTypeDelegator, times(1)).getResourceTypeHandler(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION);
        verify(handler, times(1)).process(null, RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION, payload, appUser);
        verify(countryService, times(2)).getReferenceData();
    }
    
    @Test
    void processRequestCreateAction_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("id").build();
        InstallationAccountOpeningSubmitApplicationCreateActionPayload payload = InstallationAccountOpeningSubmitApplicationCreateActionPayload.builder()
            .payloadType(RequestCreateActionPayloadType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD)
            .accountPayload(InstallationAccountPayload.builder()
                    .accountType(AccountType.INSTALLATION)
                    .applicationType(ApplicationType.NEW_PERMIT)
                    .name("name")
                    .submitter(InstallationAccountSubmitter.builder().name("name").email("test@test.gr").build())
                    .siteName("site name")
                    .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                    .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                    .commencementDate(LocalDate.of(2020,8,6))
                    .location(LocationOnShoreDTO.builder()
                            .type(LocationType.ONSHORE)
                            .gridReference("NN166718")
                            .address(AddressDTO.builder().city("city").country("GR").line1("lin1").postcode("14").build()).build())
                    .legalEntity(LegalEntityDTO.builder()
                        .type(LegalEntityType.PARTNERSHIP)
                        .name("name")
                        .referenceNumber("09546038")
                        .address(AddressDTO.builder().city("city").country("GR").line1("lin1").postcode("14").build())
                        .build())
                    .build())
            .build();
        RequestCreateActionProcessDTO requestCreateActionProcessDTO = RequestCreateActionProcessDTO.builder()
                .requestCreateActionType(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION)
                .requestCreateActionPayload(payload)
                .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(countryService.getReferenceData()).thenReturn(List.of(Country.builder().code("GR").build()));
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "processRequestCreateAction", null, null, RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_PATH)
                .content(mapper.writeValueAsString(requestCreateActionProcessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(countryService, times(2)).getReferenceData();
        verifyNoInteractions(requestCreateActionResourceTypeDelegator, handler);
    }

    @Test
    void getRequestDetailsById() throws Exception {
        final String requestId = "1";
        AerRequestMetadata metadata = AerRequestMetadata.builder()
                .type(RequestMetadataType.AER).emissions(BigDecimal.valueOf(10000)).build();
        RequestDetailsDTO workflowInfo = new RequestDetailsDTO(requestId, RequestType.AER,
                RequestStatus.IN_PROGRESS, LocalDateTime.now(), metadata);

        when(requestQueryService.findRequestDetailsById(requestId)).thenReturn(workflowInfo);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workflowInfo.getId()))
                .andExpect(jsonPath("$.requestType").value(workflowInfo.getRequestType().name()))
                .andExpect(jsonPath("$.requestStatus").value(workflowInfo.getRequestStatus().name()))
                .andExpect(jsonPath("$.requestMetadata.type").value(metadata.getType().name()))
                .andExpect(jsonPath("$.requestMetadata.emissions").value(metadata.getEmissions()));

        verify(requestQueryService, times(1)).findRequestDetailsById(requestId);
    }

    @Test
    void getRequestDetailsById_forbidden() throws Exception {
        final String requestId = "1";
        AppUser appUser = AppUser.builder().userId("id").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getRequestDetailsById", requestId, null, null);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, never()).findRequestDetailsById(anyString());
    }

    @Test
    void getRequestDetailsByResource() throws Exception {
        Long accountId = 1L;
        final String requestId = "1";
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
        		.resourceId(String.valueOf(accountId))
				.resourceType(ResourceType.ACCOUNT)
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.category(RequestHistoryCategory.PERMIT).build();
        
        RequestDetailsDTO workflowResult1 = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);
        RequestDetailsDTO workflowResult2 = new RequestDetailsDTO(requestId, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        RequestDetailsSearchResults results = RequestDetailsSearchResults.builder()
                .requestDetails(List.of(workflowResult1, workflowResult2))
                .total(10L)
                .build();

        when(requestQueryService.findRequestDetailsBySearchCriteria(criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/workflows")
                .content(mapper.writeValueAsString(criteria))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(results.getTotal()))
                .andExpect(jsonPath("$.requestDetails[0].id").value(workflowResult1.getId()))
                .andExpect(jsonPath("$.requestDetails[1].id").value(workflowResult2.getId()))
        ;

        verify(requestQueryService, times(1)).findRequestDetailsBySearchCriteria(criteria);
    }

    @Test
    void getRequestDetailsByResource_forbidden() throws Exception {
        Long accountId = 1L;
        AppUser user = AppUser.builder().userId("user").build();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
        		.resourceId(String.valueOf(accountId))
				.resourceType(ResourceType.ACCOUNT)
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.category(RequestHistoryCategory.PERMIT).build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getRequestDetailsByResource", String.valueOf(accountId), ResourceType.ACCOUNT, null);

        mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_PATH + "/workflows")
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, never()).findRequestDetailsBySearchCriteria(any());
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
}
