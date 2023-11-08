package uk.gov.pmrv.api.workflow.request.core.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
class RequestRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestRepository requestRepository;
    
    @Autowired
	private EntityManager entityManager;
    
    @Test
    void findByAccountIdAndStatusAndTypeNotNotification() {
        Long accountId = 1L;
        createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.COMPLETED, LocalDateTime.now());
        Request request = createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, null);

        flushAndClear();
        
        List<Request> result = requestRepository.findByAccountIdAndStatusAndTypeNotNotification(accountId, RequestStatus.IN_PROGRESS);
        
        assertThat(result).containsExactlyInAnyOrder(request);
    }

    @Test
    void findAllByAccountId() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;

        Request acc1Request1 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED, LocalDateTime.now());
        Request acc1Request2 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId2, CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, null);

        flushAndClear();

        List<Request> retrievedRequests = requestRepository
            .findAllByAccountIdAndTypeIsNot(accountId1, RequestType.SYSTEM_MESSAGE_NOTIFICATION);

        assertThat(retrievedRequests).hasSize(2);
        assertThat(retrievedRequests)
            .extracting(Request::getId)
            .containsExactly(acc1Request1.getId(), acc1Request2.getId());
    }

    @Test
    void findAllByAccountIdInAndStatusIn() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Long accountId3 = 3L;

        Request acc1Request = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED, LocalDateTime.now());
        createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.APPROVED, LocalDateTime.now());
        Request acc2Request = createRequest(accountId2, CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId3, CompetentAuthorityEnum.ENGLAND, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, null);

        flushAndClear();

        List<Request> retrievedRequests = requestRepository
            .findAllByAccountIdInAndTypeIsNot(Set.of(accountId1, accountId2), RequestType.SYSTEM_MESSAGE_NOTIFICATION);

        assertThat(retrievedRequests).hasSize(2);
        assertThat(retrievedRequests)
            .extracting(Request::getId)
            .containsExactly(acc1Request.getId(), acc2Request.getId());
    }
    
    @Test
    void findByAccountIdAndTypeAndStatus() {
        Long accountId1 = 1L;

        Request request1_1 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, LocalDateTime.now());
        Request request1_2 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, LocalDateTime.now());
        createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED, LocalDateTime.now());
        createRequest(2L, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, LocalDateTime.now());

        flushAndClear();

		List<Request> result = requestRepository.findByAccountIdAndTypeAndStatus(accountId1, RequestType.PERMIT_VARIATION,
            RequestStatus.APPROVED, Sort.by(Sort.Direction.ASC, "creationDate"));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Request::getId).containsExactly(request1_1.getId(), request1_2.getId());
    }

    @Test
    void findByAccountIdAndTypeAndStatusAndEndDate() {
        Long accountId1 = 1L;

        Request request1_1 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, LocalDateTime.now());
        Request request1_2 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, LocalDateTime.now().plusSeconds(1));
        Request request1_3 = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, LocalDateTime.now().minusYears(1));
        createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED, LocalDateTime.now());
        createRequest(2L, CompetentAuthorityEnum.ENGLAND, RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, LocalDateTime.now());

        flushAndClear();

        List<Request> result = requestRepository.findByAccountIdAndTypeAndStatusOrderByEndDateDesc(
            accountId1, RequestType.PERMIT_VARIATION, RequestStatus.APPROVED);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Request::getId)
            .containsExactly(request1_2.getId(), request1_1.getId(), request1_3.getId());
    }
    
    @Test
    void existsByTypeAndStatusAndCompetentAuthority_exists() {
    	RequestType type = RequestType.PERMIT_BATCH_REISSUE;
    	RequestStatus status= RequestStatus.IN_PROGRESS;
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	
    	createRequest(1L, competentAuthority, type, status, LocalDateTime.now());
    	
        flushAndClear();
        
        boolean result = requestRepository.existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void existsByTypeAndStatusAndCompetentAuthority_not_exist() {
    	RequestType type = RequestType.PERMIT_BATCH_REISSUE;
    	RequestStatus status= RequestStatus.IN_PROGRESS;
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	
    	createRequest(1L, competentAuthority, type, RequestStatus.APPROVED, LocalDateTime.now());
        createRequest(2L, competentAuthority,  RequestType.AER, status, LocalDateTime.now());
        createRequest(3L, CompetentAuthorityEnum.OPRED, type, status, LocalDateTime.now());
        
        flushAndClear();
        
        boolean result = requestRepository.existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void existByRequestTypeAndStatusAndAccountIdAndMetadataYear_not_exist() {
    	Long accountId = 1L;
    	RequestType requestType = RequestType.DRE;
    	RequestStatus status = RequestStatus.IN_PROGRESS;
    	Year year = Year.of(2023);
    	
    	createRequest(2L, CompetentAuthorityEnum.ENGLAND, requestType, status, LocalDateTime.now(), null, DreRequestMetadata.builder().type(RequestMetadataType.DRE).year(year).build());
    	createRequest(accountId, CompetentAuthorityEnum.ENGLAND, requestType, status, LocalDateTime.now(), null, DreRequestMetadata.builder().type(RequestMetadataType.DRE).year(Year.of(2020)).build());
    	createRequest(accountId, CompetentAuthorityEnum.ENGLAND, requestType, RequestStatus.COMPLETED, LocalDateTime.now(), null, DreRequestMetadata.builder()
    			.type(RequestMetadataType.DRE)
    			.year(year).build());
    	
    	flushAndClear();
    	
    	boolean result = requestRepository.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(requestType.name(), status.name(), accountId, year.getValue());
    	
    	assertThat(result).isEqualTo(false);
    }
    
    @Test
    void existByRequestTypeAndStatusAndAccountIdAndMetadataYear_exist() {
    	Long accountId = 1L;
    	RequestType requestType = RequestType.DRE;
    	RequestStatus status = RequestStatus.IN_PROGRESS;
    	Year year = Year.of(2023);
    	
    	createRequest(accountId, CompetentAuthorityEnum.ENGLAND, requestType, status, LocalDateTime.now(), null, DreRequestMetadata.builder()
    			.type(RequestMetadataType.DRE)
    			.year(year).build());
    	
    	flushAndClear();
    	
    	boolean result = requestRepository.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(requestType.name(), status.name(), accountId, year.getValue());
    	
    	assertThat(result).isEqualTo(true);
    }

    @Test
    void findAllByAccountIdAndTypeInAndMetadataYear() {
        Long accountId = 1L;
        Year year = Year.of(2023);

        createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.AVIATION_AER_UKETS, RequestStatus.IN_PROGRESS,
            LocalDateTime.now(), null,
            AviationAerRequestMetadata.builder().type(RequestMetadataType.AVIATION_AER).year(year).build());

        createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.AVIATION_DRE_UKETS, RequestStatus.COMPLETED,
            LocalDateTime.now(), null,
            AviationDreRequestMetadata.builder().type(RequestMetadataType.AVIATION_DRE).year(Year.of(2022)).build());

        Request dreRequest2023_1 = createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.AVIATION_DRE_UKETS, RequestStatus.IN_PROGRESS,
            LocalDateTime.now(), null,
            AviationDreRequestMetadata.builder().type(RequestMetadataType.AVIATION_DRE).year(year).build());

        Request dreRequest2023_2 = createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.AVIATION_DRE_UKETS, RequestStatus.COMPLETED,
            LocalDateTime.now(), null,
            AviationDreRequestMetadata.builder().type(RequestMetadataType.AVIATION_DRE).year(year).build());

        flushAndClear();

        List<Request> result = requestRepository.findAllByAccountIdAndTypeInAndMetadataYear(accountId, List.of(RequestType.AVIATION_DRE_UKETS.name()), year.getValue());

        assertThat(result).containsExactlyInAnyOrder(dreRequest2023_1, dreRequest2023_2);
    }
    
	private Request createRequest(Long accountId, CompetentAuthorityEnum competentAuthority, RequestType type,
			RequestStatus status, LocalDateTime endDate) {
		return createRequest(accountId, competentAuthority, type, status, endDate, null);
	}
    
	private Request createRequest(Long accountId, CompetentAuthorityEnum competentAuthority, RequestType type,
			RequestStatus status, LocalDateTime endDate, Long verificationBodyId) {
		return createRequest(accountId, competentAuthority, type, status, endDate, verificationBodyId, null);
	}
    
	private Request createRequest(Long accountId, CompetentAuthorityEnum competentAuthority, RequestType type,
			RequestStatus status, LocalDateTime endDate, Long verificationBodyId, RequestMetadata metadata) {
        Request request = 
                Request.builder()
                    .id(RandomStringUtils.random(5))
                    .accountId(accountId)
                    .type(type)
                    .status(status)
                    .competentAuthority(competentAuthority)
                    .verificationBodyId(verificationBodyId)
                    .endDate(endDate)
                    .metadata(metadata)
                    .build();
        entityManager.persist(request);
        return request;
    }
    
    private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}

}
