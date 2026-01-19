package uk.gov.pmrv.api.bulkdownload.core.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Service
@RequiredArgsConstructor
public class BulkDownloadDelegator {

    private final List<BulkDownloadService> bulkDownloadServices;

    public boolean canBulkDownload(AppUser appUser) {
        return bulkDownloadServices.stream()
                .anyMatch(service -> service.canBulkDownload(appUser));
    }

    public List<String> getAvailableWorkflows(AppUser appUser) {
        return bulkDownloadServices.stream()
            .filter(service -> service.isWorkflowAvailable(appUser))
            .map(BulkDownloadService::getWorkflow)
            .collect(Collectors.toList());
    }

    public List<String> getAvailablePeriods(String workflow, AppUser appUser) {
        return bulkDownloadServices.stream()
            .filter(service -> service.getWorkflow().equals(workflow))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No BulkDownloadDetailsService found for workflow: " + workflow))
            .getAvailablePeriods(appUser);
    }

}
