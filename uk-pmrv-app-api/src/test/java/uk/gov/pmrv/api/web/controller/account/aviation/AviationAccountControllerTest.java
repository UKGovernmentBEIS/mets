package uk.gov.pmrv.api.web.controller.account.aviation;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResults;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResultsInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountCreationService;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.VERIFIER;

@ExtendWith(MockitoExtension.class)
class AviationAccountControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/aviation/accounts";

    @InjectMocks
    private AviationAccountController controller;

    @Mock
    private AviationAccountCreationService aviationAccountCreationService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (AviationAccountController) aopProxy.getProxy();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void createAviationAccount() throws Exception {
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.of(2022, 12, 11);
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.SCOTLAND).build()))
            .build();
        AviationAccountCreationDTO accountCreationDTO = AviationAccountCreationDTO.builder()
            .name(accountName)
            .crcoCode(crcoCode)
            .emissionTradingScheme(emissionTradingScheme)
            .commencementDate(commencementDate)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        mockMvc.perform(
                MockMvcRequestBuilders.post(CONTROLLER_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountCreationDTO)))
            .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(aviationAccountCreationService, times(1)).createAccount(accountCreationDTO, appUser);
    }

    @Test
    void createAviationAccount_forbidden() throws Exception {
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(OPERATOR)
            .build();
        AviationAccountCreationDTO accountCreationDTO = AviationAccountCreationDTO.builder()
            .name("accountName")
            .crcoCode("crcoCode")
            .emissionTradingScheme(EmissionTradingScheme.CORSIA)
            .commencementDate(LocalDate.of(2022, 12, 11))
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[] {REGULATOR});

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountCreationDTO)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAccountCreationService);
    }

    @Test
    void isExistingAccountName() throws Exception {
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.NORTHERN_IRELAND;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .authorities(List.of(AppAuthority.builder().competentAuthority(competentAuthority).build()))
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(aviationAccountQueryService.isExistingAccountName(accountName, competentAuthority, emissionTradingScheme, null))
            .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/name")
                .param("name", accountName)
                .param("scheme", String.valueOf(emissionTradingScheme))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(true)));

        verify(aviationAccountQueryService, times(1))
            .isExistingAccountName(accountName, competentAuthority, emissionTradingScheme, null);
    }

    @Test
    void isExistingAccountNameWithAccountId() throws Exception {
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.NORTHERN_IRELAND;
        Long accountId = 1L;
        AppUser appUser = AppUser.builder()
                .userId("authUserId")
                .authorities(List.of(AppAuthority.builder().competentAuthority(competentAuthority).build()))
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(aviationAccountQueryService.isExistingAccountName(accountName, competentAuthority, emissionTradingScheme, accountId))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/name")
                        .param("name", accountName)
                        .param("scheme", String.valueOf(emissionTradingScheme))
                        .param("accountId", String.valueOf(accountId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(true)));

        verify(aviationAccountQueryService, times(1))
                .isExistingAccountName(accountName, competentAuthority, emissionTradingScheme, accountId);
    }

    @Test
    void isExistingAccountName_forbidden() throws Exception {
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;

        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(OPERATOR)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[] {REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/name")
                .param("name", accountName)
                .param("scheme", String.valueOf(emissionTradingScheme))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAccountQueryService);
    }

    @Test
    void isExistingCrcoCode() throws Exception {
        String crcoCode = "crcoCode";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.NORTHERN_IRELAND;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .authorities(List.of(AppAuthority.builder().competentAuthority(competentAuthority).build()))
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(aviationAccountQueryService.isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme, null))
            .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/crco-code")
                .param("code", crcoCode)
                .param("scheme", String.valueOf(emissionTradingScheme))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(true)));

        verify(aviationAccountQueryService, times(1))
            .isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme, null);
    }

    @Test
    void isExistingCrcoCodeWithAccountId() throws Exception {
        String crcoCode = "crcoCode";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.NORTHERN_IRELAND;
        Long accountId = 1L;
        AppUser appUser = AppUser.builder()
                .userId("authUserId")
                .authorities(List.of(AppAuthority.builder().competentAuthority(competentAuthority).build()))
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(aviationAccountQueryService.isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme, accountId))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/crco-code")
                        .param("code", crcoCode)
                        .param("scheme", String.valueOf(emissionTradingScheme))
                        .param("accountId", String.valueOf(accountId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(true)));

        verify(aviationAccountQueryService, times(1))
                .isExistingCrcoCode(crcoCode, competentAuthority, emissionTradingScheme, accountId);
    }

    @Test
    void isExistingCrcoCode_forbidden() throws Exception {
        String crcoCode = "crcoCode";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;

        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(VERIFIER)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[] {REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/crco-code")
                .param("code", crcoCode)
                .param("scheme", String.valueOf(emissionTradingScheme))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAccountQueryService);
    }

    @Test
    void getCurrentUserAccounts() throws Exception {
        final AppUser user = AppUser.builder().userId("userId").build();
        final AccountSearchCriteria criteria = AccountSearchCriteria.builder()
                .term("key")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();

        final List<AviationAccountSearchResultsInfoDTO> accounts =
                List.of(
                        new AviationAccountSearchResultsInfoDTO(1L, "account1", "EM00009", AviationAccountStatus.LIVE.name()),
                        new AviationAccountSearchResultsInfoDTO(2L, "account2", "EM00010", AviationAccountStatus.LIVE.name()));
        final AviationAccountSearchResults results = AviationAccountSearchResults.builder().accounts(accounts).total(2L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(aviationAccountQueryService.getAviationAccountsByUserAndSearchCriteria(user, criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH)
                        .param("term", criteria.getTerm())
                        .param("page", String.valueOf(criteria.getPaging().getPageNumber()))
                        .param("size", String.valueOf(criteria.getPaging().getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0].id").value(1L))
                .andExpect(jsonPath("$.accounts[0].name").value("account1"))
                .andExpect(jsonPath("$.accounts[0].emitterId").value("EM00009"))
                .andExpect(jsonPath("$.accounts[1].id").value(2L))
                .andExpect(jsonPath("$.accounts[1].name").value("account2"))
                .andExpect(jsonPath("$.accounts[1].emitterId").value("EM00010"))
        ;

        verify(aviationAccountQueryService, times(1)).getAviationAccountsByUserAndSearchCriteria(user, criteria);
    }

    @Test
    void getCurrentUserAccounts_forbidden() throws Exception {
        final AppUser user = AppUser.builder().userId("userId").build();
        final AccountSearchCriteria criteria = AccountSearchCriteria.builder()
                .term("key")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user, new String[]{OPERATOR, REGULATOR, VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH)
                        .param("term", criteria.getTerm())
                        .param("page", String.valueOf(criteria.getPaging().getPageNumber()))
                        .param("size", String.valueOf(criteria.getPaging().getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAccountQueryService);

    }
}