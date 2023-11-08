package uk.gov.pmrv.api.workflow.request.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, RequestDetailsRepository.class})
class RequestDetailsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestDetailsRepository repo;

    @Autowired
    private EntityManager entityManager;
    
    @Test
    void findRequestDetailsBySearchCriteria_with_category_criteria_only() {
        Long accountId = 1L;
        Request request1 = createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        Request request2 = createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(accountId, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(accountId, RequestType.PERMIT_REISSUE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        Request request3 = createRequest(accountId, RequestType.PERMIT_REISSUE, RequestStatus.COMPLETED, CompetentAuthorityEnum.ENGLAND);

        flushAndClear();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .accountId(accountId)
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
                .category(RequestHistoryCategory.PERMIT)
                .build();
        
        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);
        
        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request1.getId(), request1.getType(), request1.getStatus(), request1.getCreationDate(), null);
        RequestDetailsDTO expectedWorkflowResult2 = new RequestDetailsDTO(request2.getId(), request2.getType(), request2.getStatus(), request2.getCreationDate(), null);
        RequestDetailsDTO expectedWorkflowResult3 = new RequestDetailsDTO(request3.getId(), request3.getType(), request3.getStatus(), request3.getCreationDate(), null);
        
        assertThat(results).isNotNull();
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
        		expectedWorkflowResult3, expectedWorkflowResult2, expectedWorkflowResult1
                ));
    }
    
    @Test
    void findRequestDetailsBySearchCriteria_with_category_and_status_criteria_only() {
        Long accountId = 1L;
        Request request1 = createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthorityEnum.ENGLAND);
        createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(accountId, RequestType.PERMIT_REISSUE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(accountId, RequestType.PERMIT_REISSUE, RequestStatus.COMPLETED, CompetentAuthorityEnum.ENGLAND);

        flushAndClear();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .accountId(accountId)
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
                .category(RequestHistoryCategory.PERMIT)
                .requestStatuses(Set.of(RequestStatus.IN_PROGRESS))
                .build();
        
        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);
        
        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request1.getId(), request1.getType(), request1.getStatus(), request1.getCreationDate(), null);
        
        assertThat(results).isNotNull();
        assertThat(results.getTotal()).isEqualTo(1L);
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
                expectedWorkflowResult1
                ));
    }
    
    @Test
    void findRequestDetailsBySearchCriteria_filter_with_category_and_request_types_criteria_only() {
        Long accountId = 1L;
        Request request1 = createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthorityEnum.ENGLAND);
        createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);

        flushAndClear();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .accountId(accountId)
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
                .category(RequestHistoryCategory.PERMIT)
                .requestTypes(Set.of(RequestType.INSTALLATION_ACCOUNT_OPENING))
                .build();
        
        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);
        
        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request1.getId(), request1.getType(), request1.getStatus(), request1.getCreationDate(), null);
        
        assertThat(results).isNotNull();
        assertThat(results.getTotal()).isEqualTo(1L);
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
                expectedWorkflowResult1
                ));
    }
    
    @Test
    void findRequestDetailsBySearchCriteria_for_competent_authority() {
        Request request1 = createRequest(1L, RequestType.PERMIT_BATCH_REISSUE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        Request request2 = createRequest(2L, RequestType.PERMIT_BATCH_REISSUE, RequestStatus.COMPLETED, CompetentAuthorityEnum.ENGLAND);
        createRequest(3L, RequestType.PERMIT_BATCH_REISSUE, RequestStatus.COMPLETED, CompetentAuthorityEnum.NORTHERN_IRELAND);
        createRequest(4L, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(5L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);

        flushAndClear();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
                .category(RequestHistoryCategory.CA)
                .requestTypes(Set.of(RequestType.PERMIT_BATCH_REISSUE))
                .build();
        
        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);
        
        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request1.getId(), request1.getType(), request1.getStatus(), request1.getCreationDate(), null);
        RequestDetailsDTO expectedWorkflowResult2 = new RequestDetailsDTO(request2.getId(), request2.getType(), request2.getStatus(), request2.getCreationDate(), null);
        
        assertThat(results).isNotNull();
        assertThat(results.getTotal()).isEqualTo(2L);
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
                expectedWorkflowResult2, expectedWorkflowResult1
                ));
    }

    @Test
    void findRequestDetailsById() {
        Request request = createRequest(1L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND);
        createRequest(1L, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthorityEnum.ENGLAND);
        flushAndClear();

        Optional<RequestDetailsDTO> actualOpt = repo.findRequestDetailsById(request.getId());

        assertThat(actualOpt).isNotEmpty();
        RequestDetailsDTO actual = actualOpt.get();
        assertThat(actual.getId()).isEqualTo(request.getId());
        assertThat(actual.getRequestType()).isEqualTo(request.getType());
        assertThat(actual.getRequestStatus()).isEqualTo(request.getStatus());
        assertThat(actual.getCreationDate()).isEqualTo(request.getCreationDate().toLocalDate());
    }
    
    @Test
    void findRequestDetailsById_not_found() {
        createRequest(1L, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthorityEnum.ENGLAND);
        flushAndClear();

        Optional<RequestDetailsDTO> actualOpt = repo.findRequestDetailsById("invalid_request_id");

        assertThat(actualOpt).isEmpty();
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthorityEnum ca) {
        return createRequest(accountId, type, status, ca, null);
    }
    
    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthorityEnum ca, RequestMetadata metaData) {
        Request request =
            Request.builder()
                    .id(RandomStringUtils.random(5))
                    .competentAuthority(ca)
                    .type(type)
                    .status(status)
                    .accountId(accountId)
                    .metadata(metaData)
                    .build();

        entityManager.persist(request);

        return request;
    }
    
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}