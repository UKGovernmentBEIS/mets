package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTokenService;
import uk.gov.pmrv.api.token.FileToken;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpDocumentServiceTest {


    @InjectMocks
    private EmpDocumentService service;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private FileDocumentTokenService fileDocumentTokenService;

    @Test
    void generateGetFileDocumentToken() {

        final String empId = "1";
        final UUID documentUuid = UUID.randomUUID();

        final FileToken fileToken = FileToken.builder().token("token").build();
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();

        when(fileDocumentTokenService.generateGetFileDocumentToken(documentUuid.toString())).thenReturn(fileToken);
        when(emissionsMonitoringPlanQueryService.getEmpContainerByIdAndFileDocumentUuid(empId, documentUuid.toString())).thenReturn(empContainer);

        final FileToken result = service.generateGetFileDocumentToken(empId, documentUuid);

        assertThat(result).isEqualTo(fileToken);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmpContainerByIdAndFileDocumentUuid(empId, documentUuid.toString());
        verify(fileDocumentTokenService, times(1)).generateGetFileDocumentToken(documentUuid.toString());
    }

    @Test
    void generateGetFileDocumentToken_uuid_not_found_in_emp() {

        final String empId = "1";
        final UUID documentUuid = UUID.randomUUID();

        when(emissionsMonitoringPlanQueryService.getEmpContainerByIdAndFileDocumentUuid(empId, documentUuid.toString())).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        final BusinessException be = assertThrows(BusinessException.class, () -> service.generateGetFileDocumentToken(empId, documentUuid));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(emissionsMonitoringPlanQueryService, times(1)).getEmpContainerByIdAndFileDocumentUuid(empId, documentUuid.toString());
        verifyNoInteractions(fileDocumentTokenService);
    }

}