package uk.gov.pmrv.api.emissionsmonitoringplan.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.documents.service.FileDocumentTokenService;
import uk.gov.netz.api.token.FileToken;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpDocumentService {

    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    private final FileDocumentTokenService fileDocumentTokenService;

    public FileToken generateGetFileDocumentToken(final String empId,
                                                  final UUID documentUuid) {

        emissionsMonitoringPlanQueryService.getEmpContainerByIdAndFileDocumentUuid(empId, documentUuid.toString());
        return fileDocumentTokenService.generateGetFileDocumentToken(documentUuid.toString());
    }
}
