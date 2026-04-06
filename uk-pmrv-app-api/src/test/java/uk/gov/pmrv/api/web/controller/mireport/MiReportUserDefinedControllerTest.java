package uk.gov.pmrv.api.web.controller.mireport;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemParamsTypesProvider;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemResultTypesProvider;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResult;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedService;
import uk.gov.netz.api.mireport.userdefined.custom.CustomMiReportQuery;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

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
    private MiReportUserDefinedService miReportQueryService;

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

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .setConversionService(conversionService)
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
		when(miReportQueryService.generateCustomReport(CompetentAuthorityEnum.ENGLAND, query)).thenReturn(result);

		mockMvc.perform(MockMvcRequestBuilders.post(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/generate-custom")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(query)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.columnNames[0]").value("col1"));


		verify(appSecurityComponent, times(1)).getAuthenticatedUser();
		verify(miReportQueryService, times(1)).generateCustomReport(appUser.getCompetentAuthority(), query);
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

}
