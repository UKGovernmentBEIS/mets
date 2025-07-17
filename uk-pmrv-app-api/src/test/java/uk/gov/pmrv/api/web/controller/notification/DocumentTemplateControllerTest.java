package uk.gov.pmrv.api.web.controller.notification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.account.transform.StringToAccountTypeEnumConverter;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateQueryService;
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateUpdateService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private DocumentTemplateController documentTemplateController;

    @Mock
    private DocumentTemplateQueryService documentTemplateQueryService;

    @Mock
    private DocumentTemplateUpdateService documentTemplateUpdateService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AuthorizedAspect authorizedAspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(documentTemplateController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        aspectJProxyFactory.addAspect(authorizedAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        documentTemplateController = (DocumentTemplateController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(new StringToAccountTypeEnumConverter());

        mockMvc = MockMvcBuilders.standaloneSetup(documentTemplateController)
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .setConversionService(conversionService)
            .build();
    }

    @Test
    void getCurrentUserDocumentTemplates() throws Exception {
        AccountType accountType = AccountType.AVIATION;
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        AppAuthority pmrvAuthority = AppAuthority.builder().competentAuthority(ca).build();
        AppUser appUser = AppUser.builder()
            .userId("userId")
            .roleType(RoleTypeConstants.REGULATOR)
            .authorities(List.of(pmrvAuthority))
            .build();
        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .competentAuthority(ca)
            .accountType(accountType)
            .term("term")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        List<TemplateInfoDTO> documentTemplates = List.of(
            new TemplateInfoDTO(1L, "template1", "Workflow Name", LocalDateTime.now()),
            new TemplateInfoDTO(2L, "template2", "Workflow Name", LocalDateTime.now())
        );
        TemplateSearchResults results = TemplateSearchResults.builder()
            .templates(documentTemplates)
            .total(2L)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(documentTemplateQueryService.getDocumentTemplatesBySearchCriteria(searchCriteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1.0/aviation/document-templates")
                .param("term", searchCriteria.getTerm())
                .param("page", String.valueOf(searchCriteria.getPaging().getPageNumber()))
                .param("size", String.valueOf(searchCriteria.getPaging().getPageSize()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.templates[0].id").value(1L))
                .andExpect(jsonPath("$.templates[0].name").value("template1"))
                .andExpect(jsonPath("$.templates[1].id").value(2L))
                .andExpect(jsonPath("$.templates[1].name").value("template2"));

        verify(documentTemplateQueryService, times(1)).getDocumentTemplatesBySearchCriteria(searchCriteria);
    }

    @Test
    void getCurrentUserDocumentTemplates_forbidden() throws Exception {
        AppUser appUser = AppUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();
        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .term("term")
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1.0/aviation/document-templates")
                .param("term", searchCriteria.getTerm())
                .param("page", String.valueOf(searchCriteria.getPaging().getPageNumber()))
                .param("size", String.valueOf(searchCriteria.getPaging().getPageSize()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(documentTemplateQueryService);
    }

    @Test
    void getDocumentTemplateById() throws Exception {
        Long documentTemplateId = 1L;
        String documentTemplateName = "document_template_name";

        DocumentTemplateDTO documentTemplateDTO = DocumentTemplateDTO.builder()
            .id(documentTemplateId)
            .name(documentTemplateName)
            .build();

        when(documentTemplateQueryService.getDocumentTemplateDTOById(documentTemplateId)).thenReturn(documentTemplateDTO);

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1.0/document-templates/" + documentTemplateId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(documentTemplateId))
            .andExpect(jsonPath("$.name").value(documentTemplateName));

        verify(documentTemplateQueryService, times(1)).getDocumentTemplateDTOById(documentTemplateId);
    }

    @Test
    void getDocumentTemplateById_forbidden() throws Exception {
        long documentTemplateId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "getDocumentTemplateById", Long.toString(documentTemplateId), null, null);

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1.0/document-templates/" + documentTemplateId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(documentTemplateQueryService);
    }

    @Test
    void updateDocumentTemplate() throws Exception {
        Long documentTemplateId = 1L;
        String userId = "userId";
        AppUser authUser = AppUser.builder().userId(userId).roleType(RoleTypeConstants.REGULATOR).build();
        String originalFilename = "filename.txt";
        String contentType = "text/plain";
        byte[] fileContent = "content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", originalFilename, contentType, fileContent);
        FileDTO fileDTO = FileDTO.builder()
            .fileName(originalFilename)
            .fileType(contentType)
            .fileContent(fileContent)
            .fileSize(file.getSize())
            .build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1.0/document-templates/" + documentTemplateId)
                .file(file)).andExpect(status().isNoContent());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(documentTemplateUpdateService, times(1)).updateDocumentTemplateFile(documentTemplateId, fileDTO, userId);
    }
    
    @Test
    void updateDocumentTemplate_forbidden() throws Exception {
        Long documentTemplateId = 1L;
        String userId = "userId";
        AppUser authUser = AppUser.builder().userId(userId).roleType(RoleTypeConstants.REGULATOR).build();
        String originalFilename = "filename.txt";
        String contentType = "text/plain";
        byte[] fileContent = "content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", originalFilename, contentType, fileContent);
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(appUserAuthorizationService).authorize(authUser,
                "updateDocumentTemplate", Long.toString(documentTemplateId), null, null);
        
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1.0/document-templates/" + documentTemplateId)
                .file(file)).andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(documentTemplateUpdateService);
    }
    
    @Test
    @DisplayName("Should throw BAD REQUEST (400) when no attachment is provided")
    void updateDocumentTemplate_noDocumentTemplateFileProvided() throws Exception {
        AppUser authUser = AppUser.builder().userId("userId").roleType(RoleTypeConstants.REGULATOR).build();
        long documentTemplateId = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1.0/document-templates/" + documentTemplateId))
            .andExpect(
                result -> Assertions.assertTrue(
                    result.getResolvedException() instanceof MissingServletRequestPartException))
            .andExpect(status().isBadRequest());
    }

}