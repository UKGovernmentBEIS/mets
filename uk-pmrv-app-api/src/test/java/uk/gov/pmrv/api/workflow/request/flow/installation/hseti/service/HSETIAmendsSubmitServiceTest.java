package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationAmendsSubmitRequestTaskPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HSETIAmendsSubmitServiceTest {


    @InjectMocks
    private HSETIAmendsSubmitService service;

    @Mock
    private HSETISubmitService submitService;

    @Test
    void saveAmends() {
        HSETIApplicationAmendsSaveRequestTaskActionPayload taskActionPayload =
                HSETIApplicationAmendsSaveRequestTaskActionPayload
                        .builder()
                        .hsetiSectionsCompleted(Map.of("Test", true))
                        .requestedChangesCompleted(true)
                        .hseti(HSETI
                                .builder()
                                .notes("asdas")
                                .build())
                        .build();

        HSETIApplicationAmendsSubmitRequestTaskPayload taskPayload =
                HSETIApplicationAmendsSubmitRequestTaskPayload
                        .builder()
                         .hsetiSectionsCompleted(Map.of("Test1", true))
                        .requestedChangesCompleted(false)
                        .hseti(HSETI
                                .builder()
                                .notes("lkjoihj")
                                .build())
                        .build();

        RequestTask task = RequestTask.builder().payload(taskPayload).build();

        service.saveAmends(taskActionPayload, task);

        assertThat(((HSETIApplicationAmendsSubmitRequestTaskPayload) task.getPayload()).getHseti())
                .isEqualTo(taskActionPayload.getHseti());

        assertThat(((HSETIApplicationAmendsSubmitRequestTaskPayload) task.getPayload()).getHsetiSectionsCompleted())
                .containsExactlyEntriesOf(taskActionPayload.getHsetiSectionsCompleted());

        assertThat(((HSETIApplicationAmendsSubmitRequestTaskPayload) task.getPayload()).getRequestedChangesCompleted())
                .isTrue();

    }

    @Test
    void submitToRegulator() {

        AppUser user = AppUser.builder().userId("testuser").build();

        HSETIApplicationAmendsSubmitRequestTaskActionPayload taskActionPayload =
                HSETIApplicationAmendsSubmitRequestTaskActionPayload
                        .builder()
                        .hsetiSectionsCompleted(Map.of("Test", true))
                        .build();

        HSETIApplicationAmendsSubmitRequestTaskPayload taskPayload =
                HSETIApplicationAmendsSubmitRequestTaskPayload
                        .builder()
                         .hsetiSectionsCompleted(Map.of("Test1", true))
                        .requestedChangesCompleted(false)
                        .hseti(HSETI
                                .builder()
                                .notes("lkjoihj")
                                .build())
                        .build();

        RequestTask task = RequestTask.builder().payload(taskPayload).build();

        service.submitToRegulator(taskActionPayload, task, user);


        verify(submitService, times(1)).submitToRegulator(task, user);

        assertThat(((HSETIApplicationAmendsSubmitRequestTaskPayload) task.getPayload()).getHsetiSectionsCompleted())
                .containsExactlyEntriesOf(taskActionPayload.getHsetiSectionsCompleted());

    }
}
