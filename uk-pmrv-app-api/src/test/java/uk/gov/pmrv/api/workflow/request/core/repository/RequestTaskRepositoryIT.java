package uk.gov.pmrv.api.workflow.request.core.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class RequestTaskRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestTaskRepository repository;
    
    @Autowired
	private EntityManager entityManager;
    
    @Test
    void findByRequestTypeAndAssignee() {
    	final String assignee = "assignee";
		final String anotherAsignee = "another_assignee";
    	Request requestInstallationAccountOpening = createRequest(CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING);
    	RequestTask requestTaskInstallationAccountOpeningAssignee = createRequestTask(requestInstallationAccountOpening, assignee, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
    	
    	Request requestInstallationAccountOpeningAnotherAssignee = createRequest(CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING);
        // requestTaskInstallationAccountOpeningAnotherAssignee
    	createRequestTask(requestInstallationAccountOpeningAnotherAssignee, anotherAsignee, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
    	
    	Request requestPermitIssuance = createRequest(CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_ISSUANCE);
    	// requestTaskPermitIssuanceAssignee
    	createRequestTask(requestPermitIssuance, assignee, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT);
    	
    	flushAndClear();
    	
    	//invoke
    	List<RequestTask> tasksFound = repository.findByRequestTypeAndAssignee(RequestType.INSTALLATION_ACCOUNT_OPENING, assignee);
    	
    	//assert
    	assertThat(tasksFound).hasSize(1);
    	assertThat(tasksFound.get(0).getId()).isEqualTo(requestTaskInstallationAccountOpeningAssignee.getId());
    }

	@Test
	void findByRequestTypeAndAssigneeAndRequestAccountId() {
		final String assignee = "assignee";
		final RequestType requestType = RequestType.SYSTEM_MESSAGE_NOTIFICATION;
		final Long accountId = 1L;

		Request request1 = createRequest(accountId, CompetentAuthorityEnum.ENGLAND, requestType);
		RequestTask requestTask1 = createRequestTask(request1, assignee, ACCOUNT_USERS_SETUP);

		Request request2 = createRequest(accountId, CompetentAuthorityEnum.ENGLAND, INSTALLATION_ACCOUNT_OPENING);
		createRequestTask(request2, assignee, INSTALLATION_ACCOUNT_OPENING_ARCHIVE);

		Request request3 = createRequest(2L, CompetentAuthorityEnum.ENGLAND, requestType);
		createRequestTask(request3, assignee, ACCOUNT_USERS_SETUP);

		Request request4 = createRequest(1L, CompetentAuthorityEnum.ENGLAND, requestType);
		createRequestTask(request4, "other_assignee", ACCOUNT_USERS_SETUP);
		
		flushAndClear();

		//invoke
		List<RequestTask> tasksFound = repository.findByRequestTypeAndAssigneeAndRequestAccountId(requestType, assignee, accountId);

		//assert
		assertThat(tasksFound).hasSize(1);
		assertThat(tasksFound.get(0).getId()).isEqualTo(requestTask1.getId());
	}
	
	@Test
    void findByAssignee() {
        final String assignee = "assignee";
		final String anotherAsignee = "another_assignee";

        Request requestOpen1 = createRequest(CompetentAuthorityEnum.ENGLAND, INSTALLATION_ACCOUNT_OPENING);
        RequestTask requestTask1 = createRequestTask(requestOpen1, assignee, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
        
		Request requestOpen2 = createRequest(CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING);
		createRequestTask(requestOpen2, anotherAsignee, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);

		flushAndClear();
        
        //invoke
        List<RequestTask> tasksFound = repository.findByAssignee(assignee);
        
        //assert
        assertThat(tasksFound).containsOnly(requestTask1);
    }

    @Test
    void findByAssigneeAndRequestAccountId() {
        String assignee1 = "assignee1";
		String assignee2 = "assignee2";

		Long accountId1 = 1L;
		Long accountId2 = 2L;

        Request request1 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, INSTALLATION_ACCOUNT_OPENING);
		RequestTask requestTask1 = createRequestTask(request1, assignee1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);

		Request request2 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING);
		createRequestTask(request2, assignee2, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);

		Request request3 = createRequest(accountId2, CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING);
		createRequestTask(request3, assignee1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);


		flushAndClear();

		List<RequestTask> requestTasksFound =
            repository.findByAssigneeAndRequestAccountId(assignee1, accountId1);

        assertThat(requestTasksFound).containsOnly(requestTask1);
    }

    @Test
    void findByRequestId() {
        String user1 = "user1";
        String user2 = "user2";

        Request request1 = createRequest(CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_ISSUANCE);
        RequestTask request1Task1 = createRequestTask(request1, user1, PERMIT_ISSUANCE_APPLICATION_REVIEW);
        RequestTask request1Task2 = createRequestTask(request1, user2, PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW);

        Request request2 = createRequest(CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_ISSUANCE);
        createRequestTask(request2, user1, PERMIT_ISSUANCE_APPLICATION_REVIEW);

        flushAndClear();

        List<RequestTask> requestTasksFound = repository.findByRequestId(request1.getId());

        assertThat(requestTasksFound).containsExactly(request1Task1, request1Task2);
    }
    
    private Request createRequest(
    		CompetentAuthorityEnum ca,
    		RequestType type) {
        return createRequest(null, ca, type);
    }

	private Request createRequest(
		Long accountId,
		CompetentAuthorityEnum ca,
		RequestType type) {
		Request request =
			Request.builder()
				.id(RandomStringUtils.insecure().next(5))
				.type(type)
				.status(RequestStatus.IN_PROGRESS)
				.competentAuthority(ca)
				.accountId(accountId)
				.build();
		entityManager.persist(request);
		return request;
	}
	
	private RequestTask createRequestTask(
    		Request request, 
    		String assignee, 
    		RequestTaskType type) {
    	RequestTask requestTask = 
    			RequestTask.builder()
    				.request(request)
    				.assignee(assignee)
    				.processTaskId(String.valueOf(UUID.randomUUID()))
    				.type(type)
    				.startDate(LocalDateTime.now())
    				.build();
    	entityManager.persist(requestTask);
        return requestTask;
    }
    
    private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}

}