package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemByRequestOperatorRepository.class})
class ItemByRequestOperatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemByRequestOperatorRepository cut;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItemsByRequestId() {
        Long account1 = -1L;
        String user = "user";

        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
				Map.of(account1, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW)
						);
        
        Request request1 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 1L, LocalDateTime.now());
        RequestTask requestTask1 =
            createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", LocalDateTime.now());
        RequestTask requestTask2 =
            createRequestTask(user, request1, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", LocalDateTime.now());

        createRequestTask(user, request1, RequestTaskType.ACCOUNT_USERS_SETUP, "t3", LocalDateTime.now());

        
        Request request2 = createRequest(account1, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, 2L, LocalDateTime.now());
        createRequestTask("anotherUser", request2, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t4", LocalDateTime.now());

        ItemPage itemPage = cut.findItemsByRequestId(scopedRequestTaskTypes, request1.getId());

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());

        
        Item item2 = itemPage.getItems().get(0);
        assertThat(item2.getRequestId()).isEqualTo(request1.getId());
        assertEquals(item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item2.getRequestId(), request1.getId());
        assertEquals(item2.getRequestType(), request1.getType());
        assertEquals(item2.getTaskId(), requestTask2.getId());
        assertEquals(item2.getTaskType(), requestTask2.getType());
        assertEquals(item2.getTaskAssigneeId(), requestTask2.getAssignee());
        assertEquals(item2.getTaskDueDate(), requestTask2.getDueDate());
        assertEquals(item2.getAccountId(), account1);
        
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
    }
    

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, Long vbId, LocalDateTime creationDate) {
        Request request = Request.builder()
                .id(RandomStringUtils.random(5))
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
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

}
