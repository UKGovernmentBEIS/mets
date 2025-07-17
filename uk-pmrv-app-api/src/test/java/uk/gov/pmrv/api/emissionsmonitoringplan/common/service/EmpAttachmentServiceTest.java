package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.netz.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.netz.api.token.FileToken;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpAttachmentServiceTest {
    @InjectMocks
    private EmpAttachmentService service;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private FileAttachmentTokenService fileAttachmentTokenService;

    @Test
    void generateGetFileAttachmentToken() {
        String empId = "1";
        UUID attachmentUuid = UUID.randomUUID();

        EmissionsMonitoringPlanUkEtsContainer emissionsMonitoringPlanContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .empAttachments(Map.of(
                        attachmentUuid, "file1"
                ))
                .build();

        FileToken fileToken = FileToken.builder().token("token").build();


        when(emissionsMonitoringPlanQueryService.getEmpContainerById(empId)).thenReturn(emissionsMonitoringPlanContainer);
        when(fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid.toString())).thenReturn(
                fileToken);

        //invoke
        FileToken result = service.generateGetFileAttachmentToken(empId, attachmentUuid);

        assertThat(result).isEqualTo(fileToken);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmpContainerById(empId);
        verify(fileAttachmentTokenService, times(1)).generateGetFileAttachmentToken(attachmentUuid.toString());
    }

    @Test
    void getFileAttachment_uuid_not_found_in_emp() {
        String empId = "1";
        UUID attachmentUuid = UUID.randomUUID();

        EmissionsMonitoringPlanUkEtsContainer emissionsMonitoringPlanContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
                .empAttachments(Map.of(
                        UUID.randomUUID(), "file1"
                ))
                .build();

        when(emissionsMonitoringPlanQueryService.getEmpContainerById(empId)).thenReturn(emissionsMonitoringPlanContainer);

        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> {
            service.generateGetFileAttachmentToken(empId, attachmentUuid);
        });
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(emissionsMonitoringPlanQueryService, times(1)).getEmpContainerById(empId);
        verifyNoInteractions(fileAttachmentTokenService);
    }

}