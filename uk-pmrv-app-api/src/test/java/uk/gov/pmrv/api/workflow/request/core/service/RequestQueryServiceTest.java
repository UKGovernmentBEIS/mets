package uk.gov.pmrv.api.workflow.request.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_TRANSFER_A;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_TRANSFER_B;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestDetailsRepository;

@ExtendWith(MockitoExtension.class)
class RequestQueryServiceTest {

    @InjectMocks
    private RequestQueryService service;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestDetailsRepository requestDetailsRepository;
    
    @Test
    void findInProgressRequestsByAccount() {
        Long accountId = 1L;
        Request request = Request.builder().id("1").status(RequestStatus.IN_PROGRESS).build();

        when(requestRepository.findByAccountIdAndStatusAndTypeNotNotification(accountId, RequestStatus.IN_PROGRESS))
                .thenReturn(List.of(request));
        
        List<Request> result = service.findInProgressRequestsByAccount(accountId);
        
        assertThat(result).containsExactlyInAnyOrder(request);
        verify(requestRepository, times(1)).findByAccountIdAndStatusAndTypeNotNotification(accountId, RequestStatus.IN_PROGRESS);
    }

    @Test
    void existsRequestById() {
        String requestId = "requestId";

        when(requestRepository.existsById(requestId)).thenReturn(true);

        boolean result = service.existsRequestById(requestId);

        assertThat(result).isTrue();
        verify(requestRepository, times(1)).existsById(requestId);
    }
    
    @Test
    void existByRequestTypeAndRequestStatusAndCompetentAuthority() {
    	RequestType type = RequestType.PERMIT_BATCH_REISSUE;
    	RequestStatus status = RequestStatus.IN_PROGRESS;
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        when(requestRepository.existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority)).thenReturn(true);

        boolean result = service.existByRequestTypeAndRequestStatusAndCompetentAuthority(type, status, competentAuthority);

        assertThat(result).isTrue();
        verify(requestRepository, times(1)).existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority);
    }
    
    @Test
    void existByRequestTypeAndStatusAndAccountIdAndMetadataYear() {
    	RequestType type = RequestType.DRE;
    	RequestStatus status = RequestStatus.IN_PROGRESS;
    	Long accountId = 1L;
    	Year year = Year.of(2023);
    	
    	when(requestRepository.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(type.name(), status.name(), accountId, year.getValue()))
    		.thenReturn(true);
    	
    	boolean result = service.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(type, status, accountId, year);
    	
    	assertThat(result).isTrue();
    	verify(requestRepository, times(1)).existByRequestTypeAndStatusAndAccountIdAndMetadataYear(type.name(), status.name(), accountId, year.getValue());
    }

    @Test
    void findRequestDetailsBySearchCriteria() {
        Long accountId = 1L;
        final String requestId = "1";
        RequestSearchCriteria criteria = RequestSearchCriteria.builder().resourceId(String.valueOf(accountId)).resourceType(ResourceType.ACCOUNT)
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build()).build();

        RequestDetailsDTO workflowResult1 = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);
        RequestDetailsDTO workflowResult2 = new RequestDetailsDTO(requestId, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        RequestDetailsSearchResults expectedResults = RequestDetailsSearchResults.builder()
                .requestDetails(List.of(workflowResult1, workflowResult2))
                .total(10L)
                .build();

        when(requestDetailsRepository.findRequestDetailsBySearchCriteria(criteria)).thenReturn(expectedResults);

        RequestDetailsSearchResults actualResults = service.findRequestDetailsBySearchCriteria(criteria);

        assertThat(actualResults).isEqualTo(expectedResults);
        verify(requestDetailsRepository, times(1)).findRequestDetailsBySearchCriteria(criteria);
    }

    @Test
    void findRequestDetailsById() {
        final String requestId = "1";
        RequestDetailsDTO expected = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        when(requestDetailsRepository.findRequestDetailsById(requestId)).thenReturn(Optional.of(expected));

        RequestDetailsDTO actual = service.findRequestDetailsById(requestId);

        assertThat(actual).isEqualTo(expected);
        verify(requestDetailsRepository, times(1)).findRequestDetailsById(requestId);
    }
    
    @Test
    void findRequestDetailsById_not_found() {
        final String requestId = "1";

        when(requestDetailsRepository.findRequestDetailsById(requestId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () -> service.findRequestDetailsById(requestId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(requestDetailsRepository, times(1)).findRequestDetailsById(requestId);
    }

    @Test
    void findLinkedRequests() {

        final String linkedRequestId = "linkedRequestId";
        final String linkedProcessInstanceId = "linkedProcessInstanceId";

        final List<Request> requests = List.of(
            Request.builder()
                .type(PERMIT_TRANSFER_A)
                .payload(PermitTransferARequestPayload.builder().relatedRequestId(linkedRequestId).build())
                .status(RequestStatus.IN_PROGRESS)
                .build()
        );

        final Request linkedRequest = Request.builder()
            .id(linkedRequestId)
            .processInstanceId(linkedProcessInstanceId)
            .type(PERMIT_TRANSFER_B)
            .status(RequestStatus.IN_PROGRESS)
            .build();

        when(requestRepository.findByIdInAndStatus(Set.of(linkedRequestId), RequestStatus.IN_PROGRESS)).thenReturn(List.of(linkedRequest));

        final List<Request> relatedRequests = service.getRelatedRequests(requests);

        assertThat(relatedRequests).containsExactly(linkedRequest);
    }
}
