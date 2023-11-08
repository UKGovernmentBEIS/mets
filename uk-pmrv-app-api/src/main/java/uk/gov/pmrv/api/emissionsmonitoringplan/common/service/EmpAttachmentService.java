package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.pmrv.api.token.FileToken;

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
