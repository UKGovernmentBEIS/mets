package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmpAttachmentService {

    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    private final FileAttachmentTokenService fileAttachmentTokenService;

    public FileToken generateGetFileAttachmentToken(String empId, UUID attachmentUuid) {
        EmissionsMonitoringPlanContainer empContainer = emissionsMonitoringPlanQueryService.getEmpContainerById(empId);

        //validate
        if (!empContainer.getEmpAttachments().containsKey(attachmentUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, attachmentUuid);
        }

        return fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid.toString());
    }

}
