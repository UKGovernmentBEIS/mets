package uk.gov.pmrv.api.workflow.request.core.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchByAccountCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

class RequestSearchCriteriaMapperTest {

	private RequestSearchCriteriaMapper mapper;
    
    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RequestSearchCriteriaMapper.class);
    }
    
    @Test
    void toRequestSearchCriteria() {
    	RequestSearchByAccountCriteria requestSearchByAccountCriteria = RequestSearchByAccountCriteria.builder()
    			.accountId(1L)
    			.category(RequestHistoryCategory.PERMIT)
    			.paging(PagingRequest.builder().pageNumber(1L).pageSize(10L).build())
    			.requestStatuses(Set.of(RequestStatus.COMPLETED))
    			.requestTypes(Set.of(RequestType.PERMIT_BATCH_REISSUE))
    			.build();
    	
    	RequestSearchCriteria result = mapper.toRequestSearchCriteria(requestSearchByAccountCriteria);
    	
    	assertThat(result).isEqualTo(RequestSearchCriteria.builder()
    			.accountId(1L)
    			.category(RequestHistoryCategory.PERMIT)
    			.paging(PagingRequest.builder().pageNumber(1L).pageSize(10L).build())
    			.requestStatuses(Set.of(RequestStatus.COMPLETED))
    			.requestTypes(Set.of(RequestType.PERMIT_BATCH_REISSUE))
    			.build());
    }
}
