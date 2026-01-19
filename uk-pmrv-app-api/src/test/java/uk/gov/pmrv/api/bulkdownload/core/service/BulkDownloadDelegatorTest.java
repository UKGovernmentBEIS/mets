package uk.gov.pmrv.api.bulkdownload.core.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;

public class BulkDownloadDelegatorTest {

    private BulkDownloadDelegator bulkDownloadDelegator;
    private BulkDownloadService service1;
    private BulkDownloadService service2;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        service1 = mock(BulkDownloadService.class);
        service2 = mock(BulkDownloadService.class);
        // Inject mocks into the service
        bulkDownloadDelegator = new BulkDownloadDelegator(Arrays.asList(service1, service2));
        appUser = mock(AppUser.class);
    }

    @Test
    void canBulkDownload_trueIfAnyServiceAllows() {
        when(service1.canBulkDownload(appUser)).thenReturn(false);
        when(service2.canBulkDownload(appUser)).thenReturn(true);

        assertTrue(bulkDownloadDelegator.canBulkDownload(appUser));
    }

    @Test
    void canBulkDownload_falseIfNoneAllow() {
        when(service1.canBulkDownload(appUser)).thenReturn(false);
        when(service2.canBulkDownload(appUser)).thenReturn(false);

        assertFalse(bulkDownloadDelegator.canBulkDownload(appUser));
    }

    @Test
    void getAvailableWorkflows_returnsWorkflowNames() {
        when(service1.isWorkflowAvailable(appUser)).thenReturn(true);
        when(service2.isWorkflowAvailable(appUser)).thenReturn(false);
        when(service1.getWorkflow()).thenReturn(AccountFileAttachmentWorkflow.ALR.name());
        when(service2.getWorkflow()).thenReturn(AccountFileAttachmentWorkflow.DOAL.name());

        List<String> result = bulkDownloadDelegator.getAvailableWorkflows(appUser);

        assertEquals(Collections.singletonList("ALR"), result);
    }

    @Test
    void getAvailablePeriods_returnsPeriodsForMatchingWorkflow() {
        when(service1.getWorkflow()).thenReturn(AccountFileAttachmentWorkflow.ALR.name());
        when(service1.getAvailablePeriods(appUser)).thenReturn(List.of("2023", "2024"));
        when(service2.getWorkflow()).thenReturn(AccountFileAttachmentWorkflow.DOAL.name());
        when(service2.getAvailablePeriods(appUser)).thenReturn(List.of("wrong-1", "wrong-2"));

        List<String> result = bulkDownloadDelegator.getAvailablePeriods("ALR", appUser);

        assertEquals(List.of("2023", "2024"), result);
    }

    @Test
    void getAvailablePeriods_throwsIfNoMatchingWorkflow() {
        when(service1.getWorkflow()).thenReturn(AccountFileAttachmentWorkflow.DOAL.name());
        when(service2.getWorkflow()).thenReturn(AccountFileAttachmentWorkflow.DOAL.name());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> bulkDownloadDelegator.getAvailablePeriods("ALR", appUser));
        assertTrue(ex.getMessage().contains("No BulkDownloadDetailsService found for workflow: ALR"));
    }
}