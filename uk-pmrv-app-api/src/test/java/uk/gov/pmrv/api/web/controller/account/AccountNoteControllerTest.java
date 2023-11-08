package uk.gov.pmrv.api.web.controller.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteDto;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteRequest;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteResponse;
import uk.gov.pmrv.api.account.service.AccountNoteService;
import uk.gov.pmrv.api.account.transform.StringToAccountTypeEnumConverter;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.note.NotePayload;
import uk.gov.pmrv.api.common.note.NoteRequest;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileUuidDTO;
import uk.gov.pmrv.api.files.notes.service.FileNoteService;
import uk.gov.pmrv.api.token.FileToken;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@ExtendWith(MockitoExtension.class)
class AccountNoteControllerTest {

    private static final String ACCOUNT_NOTE_CONTROLLER_PATH = "/v1.0/account-notes";

    private MockMvc mockMvc;

    @InjectMocks
    private AccountNoteController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private AccountNoteService accountNoteService;

    @Mock
    private FileNoteService fileNoteService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (AccountNoteController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(new StringToAccountTypeEnumConverter());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
                .build();
    }

    @Test
    void getAccountNotes() throws Exception {
        
        final long accountId = 1L;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR).build();

        final AccountNoteResponse response = AccountNoteResponse.builder()
            .totalItems(10L)
            .accountNotes(
                List.of(
                    AccountNoteDto.builder().accountId(accountId).payload(NotePayload.builder().note("note 1").build()).build(),
                    AccountNoteDto.builder().accountId(accountId).payload(NotePayload.builder().note("note 2").build()).build()
                )
            )
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(accountNoteService.getAccountNotesByAccountId(accountId, 0, 2)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(ACCOUNT_NOTE_CONTROLLER_PATH + "?accountId=1&page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.accountNotes[0].accountId").value(1L))
            .andExpect(jsonPath("$.accountNotes[0].payload.note").value("note 1"))
            .andExpect(jsonPath("$.accountNotes[1].accountId").value(1L))
            .andExpect(jsonPath("$.accountNotes[1].payload.note").value("note 2"));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(accountNoteService, times(1)).getAccountNotesByAccountId(accountId, 0, 2);
    }

    @Test
    void getAccountNoteById() throws Exception {

        final long noteId = 1L;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        
        final AccountNoteDto accountNoteDto = AccountNoteDto.builder().accountId(2L).payload(NotePayload.builder().note("the note").build()).build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(accountNoteService.getNote(noteId)).thenReturn(accountNoteDto);

        mockMvc.perform(MockMvcRequestBuilders.get(ACCOUNT_NOTE_CONTROLLER_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.accountId").value(2L))
            .andExpect(jsonPath("$.payload.note").value("the note"));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(accountNoteService, times(1)).getNote(noteId);
    }

    @Test
    void getAccountNotes_forbidden() throws Exception {

        final PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getNotesByAccountId", Long.toString(1L));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(ACCOUNT_NOTE_CONTROLLER_PATH + "?accountId=1&page=0&size=2")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(accountNoteService, never()).getAccountNotesByAccountId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void createAccountNote() throws Exception {
        
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        final AccountNoteRequest accountNoteRequest = AccountNoteRequest.builder()
            .accountId(1L)
            .note("the note")
            .files(Set.of(UUID.randomUUID()))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(ACCOUNT_NOTE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(accountNoteRequest)))
            .andExpect(status().isNoContent());

        verify(accountNoteService, times(1)).createNote(user, accountNoteRequest);
    }

    @Test
    void createAccountNote_bad_request() throws Exception {
        
        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        final AccountNoteRequest accountNoteRequest = AccountNoteRequest.builder()
            .accountId(1L)
            .note(null)
            .files(Set.of(UUID.randomUUID()))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(ACCOUNT_NOTE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(accountNoteRequest)))
            .andExpect(status().isBadRequest());

        verify(accountNoteService, never()).createNote(any(), any());
    }

    @Test
    void uploadAccountNoteFile() throws Exception {
        
        final PmrvUser authUser = PmrvUser.builder().userId("id").build();
        final String noteName = "file";
        final String noteOriginalFileName = "filename.txt";
        final String noteContentType = "text/plain";
        final byte[] noteContent = "content".getBytes();
        
        final MockMultipartFile
            noteFile = new MockMultipartFile(noteName, noteOriginalFileName, noteContentType, noteContent);
        final FileDTO fileDTO = FileDTO.builder()
            .fileName(noteOriginalFileName)
            .fileType(noteContentType)
            .fileContent(noteContent)
            .fileSize(noteFile.getSize())
            .build();
        final UUID noteUuid = UUID.randomUUID();
        final Long accountId = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        when(fileNoteService.uploadAccountFile(authUser, fileDTO, accountId))
            .thenReturn(FileUuidDTO.builder().uuid(noteUuid.toString()).build());

        mockMvc.perform(
                MockMvcRequestBuilders
                    .multipart(ACCOUNT_NOTE_CONTROLLER_PATH + "/upload/account/" + accountId)
                    .file(noteFile)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid").value(noteUuid.toString()));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
    }

    @Test
    void uploadAccountNoteFile_forbidden() throws Exception {

        final PmrvUser authUser = PmrvUser.builder().userId("id").build();

        final MockMultipartFile noteFile = new MockMultipartFile("file", "filename.txt", "text/plain", "content".getBytes());
       
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(authUser, "uploadAccountNoteFile", "1");

        mockMvc.perform(
                MockMvcRequestBuilders.multipart(ACCOUNT_NOTE_CONTROLLER_PATH + "/upload/account/" + 1)
                    .file(noteFile)
            )
            .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(fileNoteService);
    }

    @Test
    void updateAccountNote() throws Exception {

        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        final NoteRequest noteRequest = NoteRequest.builder()
            .note("the note")
            .files(Set.of(UUID.randomUUID()))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(ACCOUNT_NOTE_CONTROLLER_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(noteRequest)))
            .andExpect(status().isNoContent());

        verify(accountNoteService, times(1)).updateNote(1L, noteRequest, user);
    }

    @Test
    void updateAccountNote_forbidden() throws Exception {

        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        final NoteRequest noteRequest = NoteRequest.builder()
            .note("the note")
            .files(Set.of(UUID.randomUUID()))
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "updateAccountNote", "1");

        mockMvc.perform(MockMvcRequestBuilders.put(ACCOUNT_NOTE_CONTROLLER_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(noteRequest)))
            .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(accountNoteService);
    }

    @Test
    void deleteAccountNote() throws Exception {

        final PmrvUser user = PmrvUser.builder()
            .roleType(RoleType.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.delete(ACCOUNT_NOTE_CONTROLLER_PATH + "/1"))
            .andExpect(status().isNoContent());

        verify(accountNoteService, times(1)).deleteNote(1L);
    }

    @Test
    void generateGetPermitDocumentToken() throws Exception {
        
        final Long noteId = 1L;
        final UUID documentUuid = UUID.randomUUID();
        final FileToken expectedToken = FileToken.builder().token("token").build();

        when(accountNoteService.generateGetFileNoteToken(noteId, documentUuid)).thenReturn(expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                .get(ACCOUNT_NOTE_CONTROLLER_PATH + "/" + noteId + "/files")
                .param("uuid", documentUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(accountNoteService, times(1)).generateGetFileNoteToken(noteId, documentUuid);
    }
}
