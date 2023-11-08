package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@ExtendWith(MockitoExtension.class)
class RequestTaskTimeManagementServiceTest {

    @InjectMocks
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Test
    void setDueDateToTasks() {
        String requestId = "1";
        RequestExpirationType requestExpirationType = RequestExpirationType.APPLICATION_REVIEW;
        LocalDate dueDate = LocalDate.now();
        RequestTask permitSubmitTask1 = RequestTask.builder()
        	.processTaskId("1")
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();
        RequestTask permitSubmitTask2 = RequestTask.builder()
            	.processTaskId("1")
                .type(RequestTaskType.PERMIT_ISSUANCE_MAKE_PAYMENT)
                .build();

        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(List.of(permitSubmitTask1, permitSubmitTask2));

        requestTaskTimeManagementService
            .setDueDateToTasks(requestId, requestExpirationType, dueDate);

        assertThat(permitSubmitTask1.getDueDate()).isNotNull();
        assertThat(permitSubmitTask2.getDueDate()).isNull();
        verify(requestTaskRepository, times(1)).findByRequestId(requestId);
    }

    @Test
    void pauseTasks() {
        String requestId = "1";
        RequestExpirationType requestExpirationType = RequestExpirationType.APPLICATION_REVIEW;
        RequestTask permitSubmitTask = RequestTask.builder()
        		.processTaskId("1")
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();

        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(List.of(permitSubmitTask));

        requestTaskTimeManagementService
            .pauseTasks(requestId, requestExpirationType);

        assertNotNull(permitSubmitTask.getPauseDate());
    }

    @Test
    void unpauseTasksAndUpdateDueDate() {
        String requestId = "1";
        RequestExpirationType requestExpirationType = RequestExpirationType.APPLICATION_REVIEW;
        LocalDate dueDate = LocalDate.now();
        RequestTask permitSubmitTask = RequestTask.builder().processTaskId("1")
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .pauseDate(LocalDate.now())
            .build();

        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(List.of(permitSubmitTask));

        requestTaskTimeManagementService
            .unpauseTasksAndUpdateDueDate(requestId, requestExpirationType, dueDate);

        assertNull(permitSubmitTask.getPauseDate());
        assertEquals(dueDate, permitSubmitTask.getDueDate());
    }
}