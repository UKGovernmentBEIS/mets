package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.RequestTaskVisit;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemOperatorRepository.class})
class ItemOperatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemOperatorRepository cut;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItems_assigned_to_me() {
        Long account1 = -1L;
        Long account1_2 = -2L;

        String user = "user";

        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
				Map.of(account1, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE), 
						account1_2, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Request request1 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 1L, LocalDateTime.now());
        RequestTask requestTask1 =
            createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());
        createOpenedItem(requestTask1.getId(), user);

        Request request2 = createRequest(account1_2, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, 2L, LocalDateTime.now());
        RequestTask requestTask2 =
            createRequestTask(user, request2, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER, "t2", request2.getCreationDate());

        
        Request request3 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 3L, LocalDateTime.now());
        createRequestTask("anotherUser", request3, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t3", request3.getCreationDate());

        Request request4 = createRequest(account1, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, 4L, LocalDateTime.now());
        createRequestTask("anotherUser", request4, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER, "t4", request4.getCreationDate());
        
        Request request5 = createRequest(account1, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, 5L, LocalDateTime.now());
        createRequestTask(user, request5, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t5", request5.getCreationDate());

        Request request6 = createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 6L, LocalDateTime.now());
        createRequestTask(user, request6, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t6", request6.getCreationDate());

        Request request7 = createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 7L, LocalDateTime.now());
        createRequestTask(user, request7, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t7", request7.getCreationDate());

        Request request8 = createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 8L, LocalDateTime.now());
        createRequestTask(user, request8, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t8", request8.getCreationDate());
        
        createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t9", request1.getCreationDate());

        ItemPage itemPage = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes,PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());

        
        Item item2 = itemPage.getItems().get(0);
        assertThat(item2.getRequestId()).isEqualTo(request2.getId());
        assertEquals(item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item2.getRequestId(), request2.getId());
        assertEquals(item2.getRequestType(), request2.getType());
        assertEquals(item2.getTaskId(), requestTask2.getId());
        assertEquals(item2.getTaskType(), requestTask2.getType());
        assertEquals(item2.getTaskAssigneeId(), requestTask2.getAssignee());
        assertEquals(item2.getTaskDueDate(), requestTask2.getDueDate());
        assertEquals(item2.getAccountId(), account1_2);
        assertTrue(item2.isNew());
        
        Item item1 = itemPage.getItems().get(1);
        assertThat(item1.getRequestId()).isEqualTo(request1.getId());
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account1);
        assertFalse(item1.isNew());
    }
    
    @Test
    void findItems_assigned_to_others() {
        Long account1 = -1L;
        Long account1_2 = -2L;

        String user = "user";

        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
				Map.of(account1, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE), 
						account1_2, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Request request1 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 1L, LocalDateTime.now());
        createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());

        Request request2 = createRequest(account1_2, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, 2L, LocalDateTime.now());
        createRequestTask("another_user", request2, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER, "t2", request2.getCreationDate());
        
        Request request3 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 3L, LocalDateTime.now());
        RequestTask requestTask3 = createRequestTask("anotherUser", request3, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t3", request3.getCreationDate());
        
        Request request4 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 4L, LocalDateTime.now());
        createRequestTask("anotherUser", request4, RequestTaskType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE, "t4", request4.getCreationDate());
        
        Request request5 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 5L, LocalDateTime.now());
        createRequestTask("anotherUser", request5, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t5", request5.getCreationDate());
        
        Request request6 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 6L, LocalDateTime.now());
        createRequestTask("anotherUser", request6, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t6", request5.getCreationDate());
        
        createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t7", request1.getCreationDate());

        ItemPage itemPage = cut.findItems(user, ItemAssignmentType.OTHERS, scopedRequestTaskTypes,PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        
        Item item = itemPage.getItems().get(0);
        assertThat(item.getRequestId()).isEqualTo(request3.getId());
        assertEquals(item.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask3.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item.getRequestId(), request3.getId());
        assertEquals(item.getRequestType(), request3.getType());
        assertEquals(item.getTaskId(), requestTask3.getId());
        assertEquals(item.getTaskType(), requestTask3.getType());
        assertEquals(item.getTaskAssigneeId(), requestTask3.getAssignee());
        assertEquals(item.getTaskDueDate(), requestTask3.getDueDate());
        assertEquals(item.getAccountId(), account1_2);
    }
    
    @Test
    void findItems_unassigned() {
        Long account1 = -1L;
        Long account1_2 = -2L;

        String user = "user";

        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
				Map.of(account1, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE), 
						account1_2, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Request request1 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 1L, LocalDateTime.now());
        RequestTask requestTask1 = createRequestTask(null, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());
        
        createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t2", request1.getCreationDate());

        Request request2 = createRequest(account1_2, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, 2L, LocalDateTime.now());
        createRequestTask(user, request2, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER, "t3", request2.getCreationDate());
        
        Request request3 = createRequest(account1_2, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 3L, LocalDateTime.now());
        createRequestTask("anotherUser", request3, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t4", request3.getCreationDate());

        Request request4 = createRequest(account1, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, 4L, LocalDateTime.now());
        createRequestTask("anotherUser", request4, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER, "t5", request4.getCreationDate());
        
        Request request5 = createRequest(account1, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, 5L, LocalDateTime.now());
        createRequestTask(user, request5, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t6", request5.getCreationDate());

        Request request6 = createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 6L, LocalDateTime.now());
        createRequestTask(user, request6, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t7", request6.getCreationDate());

        Request request7 = createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 7L, LocalDateTime.now());
        createRequestTask(user, request7, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t8", request7.getCreationDate());

        Request request8 = createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 8L, LocalDateTime.now());
        createRequestTask(user, request8, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t9", request8.getCreationDate());
        
        createRequestTask(null, request1, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t10", request1.getCreationDate());
        
        ItemPage itemPage = cut.findItems(user, ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes,PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        Item item1 = itemPage.getItems().get(0);
        assertThat(item1.getRequestId()).isEqualTo(request1.getId());
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account1);
    }
    
    @Test
    void findItems_assigned_to_me_large_account_map() {
        Long account1  = 1L;
        String user = "user";
        
		Map<Long, Set<RequestTaskType>> accountScopedMap = new HashMap<>();

		for (int i = 1; i < 1000; i++) {
			accountScopedMap.put(Long.valueOf(i), Set.of(RequestTaskType.ACCOUNT_USERS_SETUP,
					RequestTaskType.AER_AMEND_APPLICATION_VERIFICATION_SUBMIT));
		}
        
        Request request1 = createRequest(account1, RequestType.AER, RequestStatus.IN_PROGRESS, 1L, LocalDateTime.now());
        RequestTask requestTask1 =
            createRequestTask(user, request1, RequestTaskType.AER_AMEND_APPLICATION_VERIFICATION_SUBMIT, "t1", request1.getCreationDate());
        createOpenedItem(requestTask1.getId(), user);


        ItemPage itemPage = cut.findItems(user, ItemAssignmentType.ME, accountScopedMap, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        
        Item item1 = itemPage.getItems().get(0);
        assertThat(item1.getRequestId()).isEqualTo(request1.getId());
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account1);
        assertFalse(item1.isNew());
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, Long vbId, LocalDateTime creationDate) {
        Request request = Request.builder()
                .id(RandomStringUtils.insecure().next(5))
                .competentAuthority(ENGLAND)
                .type(type)
                .status(status)
                .accountId(accountId)
                .verificationBodyId(vbId)
                .creationDate(creationDate)
                .build();

        entityManager.persist(request);

        return request;
    }

    private RequestTask createRequestTask(String assignee, Request request, RequestTaskType taskType,
            String processTaskId, LocalDateTime startDate) {
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .type(taskType)
                .assignee(assignee)
                .startDate(LocalDateTime.now())
                .dueDate(LocalDate.now().plusMonths(1L))
                .build();

        entityManager.persist(requestTask);
        requestTask.setStartDate(startDate);

        return requestTask;
    }

    private void createOpenedItem(Long taskId, String userId) {
        RequestTaskVisit requestTaskVisit = RequestTaskVisit.builder()
                .taskId(taskId)
                .userId(userId).build();

        entityManager.persist(requestTaskVisit);
    }
}
