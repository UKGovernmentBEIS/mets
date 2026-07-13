package uk.gov.pmrv.api.web.controller.mireport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemParamsTypesProvider;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemResultTypesProvider;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedDTO;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedGeneratorDelegator;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResult;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResults;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedService;
import uk.gov.netz.api.mireport.userdefined.category.MiReportUserDefinedCategoryDTO;
import uk.gov.netz.api.mireport.userdefined.category.MiReportUserDefinedCategoryService;
import uk.gov.netz.api.mireport.userdefined.custom.CustomMiReportQuery;
import uk.gov.netz.api.mireport.userdefined.custom.ValidSqlQueryValidator;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MiReportUserDefinedControllerTest {

    private static final String MI_REPORT_QUERY_BASE_CONTROLLER_PATH = "/v1.0/mireports/user-defined";

    private MockMvc mockMvc;

    @InjectMocks
    private MiReportUserDefinedController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private MiReportUserDefinedService miReportUserDefinedService;

    @Mock
    private MiReportUserDefinedCategoryService miReportUserDefinedCategoryService;

    @Mock
    private MiReportUserDefinedGeneratorDelegator miReportUserDefinedGeneratorDelegator;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerSubtypes(new MiReportSystemParamsTypesProvider().getTypes().toArray(NamedType[]::new));
        objectMapper.registerSubtypes(new MiReportSystemResultTypesProvider().getTypes().toArray(NamedType[]::new));

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedRoleAspect
                authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (MiReportUserDefinedController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setConstraintValidatorFactory(new ConstraintValidatorFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == ValidSqlQueryValidator.class) {
                    return (T) new ValidSqlQueryValidator(miReportUserDefinedGeneratorDelegator);
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {
            }
        });
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .setConversionService(conversionService)
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

	@Test
	void generateCustom() throws Exception {
		AppUser appUser = buildMockAuthenticatedUser();
		CustomMiReportQuery query = CustomMiReportQuery.builder().sqlQuery("sql").build();

		MiReportUserDefinedResult result = MiReportUserDefinedResult.builder()
				.columnNames(List.of("col1"))
				.results(List.of(Map.of(
						"entry1", "val1"
						)))
				.build();
		
		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
		when(miReportUserDefinedService.generateCustomReport(CompetentAuthorityEnum.ENGLAND, query)).thenReturn(result);

		mockMvc.perform(MockMvcRequestBuilders.post(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/generate-custom")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(query)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.columnNames[0]").value("col1"));


		verify(appSecurityComponent, times(1)).getAuthenticatedUser();
		verify(miReportUserDefinedService, times(1)).generateCustomReport(appUser.getCompetentAuthority(), query);
	}

    @Test
    void createCustomReport() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        MiReportUserDefinedDTO miReportUserDefinedDTO = MiReportUserDefinedDTO.builder()
                .reportName("My report")
                .categories(new HashSet<>())
                .queryDefinition("select * from account")
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(miReportUserDefinedDTO)))
                .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(miReportUserDefinedService, times(1))
                .create(appUser.getUserId(), appUser.getCompetentAuthority(), miReportUserDefinedDTO);
    }

    @Test
    void createCustomReport_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        MiReportUserDefinedDTO miReportUserDefinedDTO = MiReportUserDefinedDTO.builder()
                .reportName("My report")
                .categories(new HashSet<>())
                .queryDefinition("test")
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.post(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(miReportUserDefinedDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(miReportUserDefinedService);
    }

    @Test
    void getCategories() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        List<MiReportUserDefinedCategoryDTO> categories = List.of(
                MiReportUserDefinedCategoryDTO.builder().id(1L).name("Financial").build(),
                MiReportUserDefinedCategoryDTO.builder().id(2L).name("Compliance").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportUserDefinedCategoryService.findAllEnabled()).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(categories.size()))
                .andExpect(jsonPath("$.[0].name").value("Financial"))
                .andExpect(jsonPath("$.[1].name").value("Compliance"));

        verify(miReportUserDefinedCategoryService, times(1)).findAllEnabled();
    }

    @Test
    void getCategories_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(miReportUserDefinedCategoryService);
    }

    private AppUser buildMockAuthenticatedUser() {
        return AppUser.builder()
                .authorities(
                        Arrays.asList(
                                AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()
                        )
                )
                .roleType(RoleTypeConstants.REGULATOR)
                .userId("USER_ID")
                .build();
    }

    @Test
    void getReports() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        int page = 0;
        int pageSize = 5;
        MiReportUserDefinedResults results = org.mockito.Mockito.mock(MiReportUserDefinedResults.class);

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportUserDefinedService.findAllByCA(appUser.getCompetentAuthority(), page, pageSize,null,null)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/reports")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(pageSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(miReportUserDefinedService, times(1))
                .findAllByCA(appUser.getCompetentAuthority(), page, pageSize,null,null);
    }

    @Test
    void getReports_with_filters() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        int page = 0;
        int pageSize = 5;
        MiReportUserDefinedResults results = org.mockito.Mockito.mock(MiReportUserDefinedResults.class);

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportUserDefinedService.findAllByCA(appUser.getCompetentAuthority(), page, pageSize,1L,"test")).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/reports")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(pageSize))
                        .param("categoryId", "1")
                        .param("term", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(miReportUserDefinedService, times(1))
                .findAllByCA(appUser.getCompetentAuthority(), page, pageSize,1L,"test");
    }

    @Test
    void getReports_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/reports")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(miReportUserDefinedService);
    }

    @Test
    void getReports_missing_page_param() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/reports")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(miReportUserDefinedService);
    }

    @Test
    void getReport() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        Long id = 1L;
        MiReportUserDefinedDTO dto = MiReportUserDefinedDTO.builder()
                .reportName("My report")
                .categories(new HashSet<>())
                .queryDefinition("test")
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportUserDefinedService.findById(id)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .param("id", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportName").value("My report"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(miReportUserDefinedService, times(1)).findById(id);
    }

    @Test
    void getReport_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(miReportUserDefinedService);
    }

    @Test
    void getReport_missing_id_param() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(miReportUserDefinedService);
    }

    @Test
    void deleteReport() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        Long id = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders.delete(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .param("id", String.valueOf(id))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(miReportUserDefinedService, times(1)).delete(id);
    }

    @Test
    void deleteReport_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.delete(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(miReportUserDefinedService);
    }

    @Test
    void delete_missing_id_param() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(miReportUserDefinedService);
    }


}
