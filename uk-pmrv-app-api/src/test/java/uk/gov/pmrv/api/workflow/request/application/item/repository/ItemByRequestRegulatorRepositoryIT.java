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
@Import({ObjectMapper.class, ItemByRequestRegulatorRepository.class})
class ItemByRequestRegulatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemByRequestRegulatorRepository cut;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItemsByRequestId() {
        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT, RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_AMENDS));

        Request request1 = createRequest(1L, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 = createRequestTask("oper1", request1, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t1", LocalDateTime.now());
        RequestTask requestTask2 = createRequestTask("oper1", request1, RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_AMENDS, "t2", LocalDateTime.now());
        
        createRequestTask("oper1", request1, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW, "t3", LocalDateTime.now());
        
        Request request2 = createRequest(2L, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.SCOTLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper1", request2, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t4", LocalDateTime.now());
        
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
        assertEquals(item2.getAccountId(), 1L);
        
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
        assertEquals(item1.getAccountId(), 1L);
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthorityEnum ca, LocalDateTime creationDate) {
        Request request =
                Request.builder()
                        .id(RandomStringUtils.random(5))
                        .competentAuthority(ca)
                        .type(type)
                        .status(status)
                        .accountId(accountId)
                        .creationDate(creationDate)
                        .build();

        entityManager.persist(request);

        return request;
    }

    private RequestTask createRequestTask(String assignee, Request request, RequestTaskType taskType,
                                          String processTaskId, LocalDateTime startDate) {
        RequestTask requestTask =
                RequestTask.builder()
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
