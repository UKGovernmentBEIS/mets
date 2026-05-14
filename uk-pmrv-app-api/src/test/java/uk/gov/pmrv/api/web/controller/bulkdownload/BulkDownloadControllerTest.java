package uk.gov.pmrv.api.web.controller.bulkdownload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.bulkdownload.core.service.BulkDownloadDelegator;
import uk.gov.pmrv.api.bulkdownload.core.domain.dto.BulkDownloadResponse;
import uk.gov.pmrv.api.bulkdownload.core.service.BulkDownloadGenerateFileService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class BulkDownloadControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/bulk-download";

    private MockMvc mockMvc;

    @InjectMocks
    private BulkDownloadController controller;

    @Mock
    private BulkDownloadDelegator bulkDownloadDelegator;

    @Mock
    private BulkDownloadGenerateFileService bulkDownloadGenerateFileService;

    @Mock
    private Validator validator;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void hasAccessBulkDownload_returnsTrue() throws Exception {
        when(bulkDownloadDelegator.canBulkDownload(any()))
            .thenReturn(true);

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders.get(CONTROLLER_PATH + "/access")
                ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("true");
    }

    @Test
    void getAvailableWorkflows_returnsResults() throws Exception {
        List<String> results = List.of(AccountFileAttachmentWorkflow.ALR.name());

        when(bulkDownloadDelegator.getAvailableWorkflows(any()))
                .thenReturn(results);

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders.get(CONTROLLER_PATH + "/workflows")
                ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("\"ALR\"");

        verify(bulkDownloadDelegator, times(1)).getAvailableWorkflows(any());
    }

    @Test
    void getAvailablePeriods_returnsResults() throws Exception {
        List<String> results = List.of("2024", "2025");

        when(bulkDownloadDelegator.getAvailablePeriods(any(), any()))
            .thenReturn(results);

        MvcResult result =
            mockMvc.perform(
                MockMvcRequestBuilders.get(CONTROLLER_PATH + "/workflows/ALR/periods")
            ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("\"2024\"", "\"2025\"");

        verify(bulkDownloadDelegator, times(1)).getAvailablePeriods(any(), any());
    }

    @Test
    void generateBulkDownloadExportToken_returnsFileToken() throws Exception {
        FileToken token =
                FileToken.builder()
                        .token("jwt-token")
                        .tokenExpirationMinutes(60L)
                        .build();

        when(bulkDownloadGenerateFileService
                .generateBulkDownloadAttachmentToken(any(), any(), any()))
                .thenReturn(token);

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CONTROLLER_PATH + "/workflows/ALR/periods/2024")
                ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("jwt-token");
    }

    @Test
    void bulkDownloadExport_returnsZipStreamWithHeaders() throws Exception {
        StreamingResponseBody body =
                outputStream -> outputStream.write("ZIP".getBytes());

        BulkDownloadResponse response =
                BulkDownloadResponse.builder()
                        .filename("2024 ALR EA.zip")
                        .body(body)
                        .build();

        when(bulkDownloadGenerateFileService
                .extractBulkDownloadResponseFromToken("token"))
                .thenReturn(response);

        MvcResult result =
                mockMvc.perform(
                        MockMvcRequestBuilders.get(CONTROLLER_PATH + "/file/token")
                ).andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();

        assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(httpResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"2024 ALR EA.zip\"");
        assertThat(httpResponse.getContentType())
                .isEqualTo("application/zip");

        verify(bulkDownloadGenerateFileService, times(1))
                .extractBulkDownloadResponseFromToken("token");
    }
}