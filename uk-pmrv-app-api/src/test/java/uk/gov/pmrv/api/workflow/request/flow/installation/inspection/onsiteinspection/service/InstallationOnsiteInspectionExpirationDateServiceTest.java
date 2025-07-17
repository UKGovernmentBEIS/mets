package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionExpirationDateServiceTest {

    @InjectMocks
    private InstallationOnsiteInspectionExpirationDateService service;

    @Test
    void getRequestTaskType(){
        assertThat(service.getRequestTaskType()).isEqualTo( RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT);
    }
}
