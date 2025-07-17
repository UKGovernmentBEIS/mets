package uk.gov.pmrv.api.web.controller.workflow;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.note.NotePayload;
import uk.gov.netz.api.common.note.NoteRequest;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileUuidDTO;
import uk.gov.netz.api.files.notes.service.FileNoteService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteDto;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteResponse;
import uk.gov.pmrv.api.workflow.request.core.service.RequestNoteService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestNoteControllerTest {

    private static final String REQUEST_NOTE_CONTROLLER_PATH = "/v1.0/request-notes";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestNoteController controller;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private RequestNoteService requestNoteService;

    @Mock
    private FileNoteService fileNoteService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (RequestNoteController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getRequestNotes() throws Exception {
        
        final String requestId = "requestId";
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        final RequestNoteResponse response = RequestNoteResponse.builder()
            .totalItems(10L)
            .requestNotes(
                List.of(
                    RequestNoteDto.builder().requestId(requestId).payload(NotePayload.builder().note("note 1").build()).build(),
                    RequestNoteDto.builder().requestId(requestId).payload(NotePayload.builder().note("note 2").build()).build()
                )
            )
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(requestNoteService.getRequestNotesByRequestId(requestId, 0, 2)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(REQUEST_NOTE_CONTROLLER_PATH + "?requestId=requestId&page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.requestNotes[0].requestId").value("requestId"))
            .andExpect(jsonPath("$.requestNotes[0].payload.note").value("note 1"))
            .andExpect(jsonPath("$.requestNotes[1].requestId").value("requestId"))
            .andExpect(jsonPath("$.requestNotes[1].payload.note").value("note 2"));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestNoteService, times(1)).getRequestNotesByRequestId(requestId, 0, 2);
    }

    @Test
    void getRequestNoteById() throws Exception {

        final long noteId = 1L;
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        final RequestNoteDto
            requestNoteDto = RequestNoteDto.builder().requestId("reqId").payload(NotePayload.builder().note("the note").build()).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(requestNoteService.getNote(noteId)).thenReturn(requestNoteDto);

        mockMvc.perform(MockMvcRequestBuilders.get(REQUEST_NOTE_CONTROLLER_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.requestId").value("reqId"))
            .andExpect(jsonPath("$.payload.note").value("the note"));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestNoteService, times(1)).getNote(noteId);
    }

    @Test
    void createRequestNote() throws Exception {

        final AppUser user = AppUser.builder()
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        final RequestNoteRequest requestNoteRequest = RequestNoteRequest.builder()
            .requestId("reqId")
            .note("the note")
            .files(Set.of(UUID.randomUUID()))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(REQUEST_NOTE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestNoteRequest)))
            .andExpect(status().isNoContent());

        verify(requestNoteService, times(1)).createNote(user, requestNoteRequest);
    }

    @Test
    void createRequestNote_bad_request() throws Exception {

        final AppUser user = AppUser.builder()
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        final RequestNoteRequest requestNoteRequest = RequestNoteRequest.builder()
            .requestId("reqId")
            .note(null)
            .files(Set.of(UUID.randomUUID()))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(REQUEST_NOTE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestNoteRequest)))
            .andExpect(status().isBadRequest());

        verify(requestNoteService, never()).createNote(any(), any());
    }

    @Test
    void uploadRequestNoteFile() throws Exception {

        final AppUser authUser = AppUser.builder().userId("id").build();
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
        final String requestId = "reqId";

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        when(fileNoteService.uploadRequestFile(authUser.getUserId(), fileDTO, requestId))
            .thenReturn(FileUuidDTO.builder().uuid(noteUuid.toString()).build());

        mockMvc.perform(
                MockMvcRequestBuilders
                    .multipart(REQUEST_NOTE_CONTROLLER_PATH + "/upload/request/" + requestId)
                    .file(noteFile)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid").value(noteUuid.toString()));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
    }

    @Test
    void uploadRequestNoteFile_forbidden() throws Exception {

        final AppUser authUser = AppUser.builder().userId("id").build();

        final MockMultipartFile noteFile = new MockMultipartFile("file", "filename.txt", "text/plain", "content".getBytes());

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(authUser, "uploadRequestNoteFile", "reqId", null, null);

        mockMvc.perform(
                MockMvcRequestBuilders.multipart(REQUEST_NOTE_CONTROLLER_PATH + "/upload/request/" + "reqId")
                    .file(noteFile)
            )
            .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(fileNoteService);
    }

    @Test
    void updateRequestNote() throws Exception {

        final AppUser user = AppUser.builder()
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        final NoteRequest noteRequest = NoteRequest.builder()
            .note("the note")
            .files(Set.of(UUID.randomUUID()))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(REQUEST_NOTE_CONTROLLER_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(noteRequest)))
            .andExpect(status().isNoContent());

        verify(requestNoteService, times(1)).updateNote(1L, noteRequest, user);
    }

    @Test
    void updateRequestNote_forbidden() throws Exception {

        final AppUser user = AppUser.builder()
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        final NoteRequest noteRequest = NoteRequest.builder()
            .note("the note")
            .files(Set.of(UUID.randomUUID()))
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateRequestNote", "1", null, null);

        mockMvc.perform(MockMvcRequestBuilders.put(REQUEST_NOTE_CONTROLLER_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(noteRequest)))
            .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(requestNoteService);
    }

    @Test
    void deleteRequestNote() throws Exception {

        final AppUser user = AppUser.builder()
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.delete(REQUEST_NOTE_CONTROLLER_PATH + "/1"))
            .andExpect(status().isNoContent());

        verify(requestNoteService, times(1)).deleteNote(1L);
    }
    
    @Test
    void generateGetFileNoteToken() throws Exception {

        final String requestId = "requestId";
        final UUID documentUuid = UUID.randomUUID();
        final FileToken expectedToken = FileToken.builder().token("token").build();

        when(requestNoteService.generateGetFileNoteToken(requestId, documentUuid)).thenReturn(expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                .get(REQUEST_NOTE_CONTROLLER_PATH + "/" + requestId + "/files")
                .param("uuid", documentUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(requestNoteService, times(1)).generateGetFileNoteToken(requestId, documentUuid);
    }
}
