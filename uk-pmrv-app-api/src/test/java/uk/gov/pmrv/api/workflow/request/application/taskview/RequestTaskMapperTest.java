package uk.gov.pmrv.api.workflow.request.application.taskview;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

class RequestTaskMapperTest {
	
	private RequestTaskMapper mapper;
	
	@BeforeEach
	public void init() {
		mapper = Mappers.getMapper(RequestTaskMapper.class);
	}
	
	@Test
	void toTaskDTO_no_assignee_user_assignable() {
		final String requestId = "1";
	    Long requestTaskId = 2L;
	    LocalDate dueDate = LocalDate.now().plusDays(10);
	    Request request = createRequest(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING);
		RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
		RequestTask requestTask = createRequestTask(requestTaskId, null, requestTaskType, request, dueDate, null);
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, null);
		
		//assert
		assertThat(result.getAssigneeFullName()).isNull();
		assertThat(result.getAssigneeUserId()).isNull();
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isTrue();
		assertThat(result.getDaysRemaining())
				.isEqualTo(DateUtils.getTaskExpirationDaysRemaining(LocalDate.now(), dueDate));
    }
	
	@Test
	void toTaskDTO_no_assignee_user_assignable_with_pause_Date() {
		final String requestId = "1";
	    Long requestTaskId = 2L;
	    LocalDate dueDate = LocalDate.now().plusDays(10);
	    LocalDate pauseDate = LocalDate.now().plusDays(8);
	    Request request = createRequest(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING);
		RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
		RequestTask requestTask = createRequestTask(requestTaskId, null, requestTaskType, request, dueDate, pauseDate);
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, null);
		
		//assert
		assertThat(result.getAssigneeFullName()).isNull();
		assertThat(result.getAssigneeUserId()).isNull();
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isTrue();
		assertThat(result.getDaysRemaining())
			.isEqualTo(DateUtils.getTaskExpirationDaysRemaining(pauseDate, dueDate));
    }
	
	@Test
	void toTaskDTO_no_assignee_user_not_assignable() {
		final String requestId = "1";
        Long requestTaskId = 2L;
        Request request = createRequest(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING);
		RequestTaskType requestTaskType = RequestTaskType.ACCOUNT_USERS_SETUP;
		LocalDate dueDate = LocalDate.now();
		RequestTask requestTask = createRequestTask(requestTaskId, null, requestTaskType, request, dueDate, null);
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, null);
		
		//assert
		assertThat(result.getAssigneeFullName()).isNull();
		assertThat(result.getAssigneeUserId()).isNull();
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isFalse();
		assertThat(result.getDaysRemaining())
		.isEqualTo(DateUtils.getTaskExpirationDaysRemaining(LocalDate.now(), dueDate));
	}
	
	@Test
	void toTaskDTO_with_assignee_user() {
		final String requestId = "1";
        Long requestTaskId = 2L;
        String task_assignee = "task_assignee";
        Request request = createRequest(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING);
		RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
		LocalDate dueDate = LocalDate.now();
		RequestTask requestTask = createRequestTask(requestTaskId, task_assignee, requestTaskType, request, dueDate, null);
		final String fn = "fn";
		final String ln = "ln";
		UserDTO assigneeUser = OperatorUserDTO.builder()
							.firstName(fn)
							.lastName(ln)
							.build();
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, assigneeUser);
		
		//assert
		assertThat(result.getAssigneeFullName()).isEqualTo(fn + " " + ln);
		assertThat(result.getAssigneeUserId()).isEqualTo(task_assignee);
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isTrue();
	}

	private Request createRequest(String requestId, RequestType requestType) {
	    return Request.builder()
            .id(requestId)
            .type(requestType)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(RequestStatus.IN_PROGRESS)
            .accountId(1L)
            .build();
    }
	
    private RequestTask createRequestTask(Long requestTaskId, String assignee, RequestTaskType requestTaskType,
            Request request, LocalDate dueDate, LocalDate pauseDate) {
		return RequestTask.builder()
	            .id(requestTaskId)
	            .request(request)
	            .type(requestTaskType)
	            .assignee(assignee)
	            .dueDate(dueDate)
	            .pauseDate(pauseDate)
	            .build();
    }
}
