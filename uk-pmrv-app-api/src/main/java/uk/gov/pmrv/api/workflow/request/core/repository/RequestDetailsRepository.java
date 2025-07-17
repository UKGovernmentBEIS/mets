package uk.gov.pmrv.api.workflow.request.core.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.QRequest;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RequestDetailsRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    public RequestDetailsSearchResults findRequestDetailsBySearchCriteria(RequestSearchCriteria criteria) {
        QRequest request = QRequest.request;

        JPAQuery<RequestDetailsDTO> query = new JPAQuery<>(entityManager);

		Set<RequestType> requestTypes = ObjectUtils.isEmpty(criteria.getRequestTypes())
				? RequestType.getRequestTypesByCategory(criteria.getCategory())
				: criteria.getRequestTypes().stream()
						.filter(type -> criteria.getCategory() == type.getCategory())
						.collect(Collectors.toSet());
		
		Set<RequestType> requestTypesFilteredByResourceType = requestTypes.stream()
				.filter(requestType -> requestType.getResourceType().equals(criteria.getResourceType()))
				.collect(Collectors.toSet());

        BooleanBuilder whereClause = new BooleanBuilder();
        
        if(ResourceType.ACCOUNT.equals(criteria.getResourceType())) {
        	whereClause.and(request.accountId.eq(Long.valueOf(criteria.getResourceId())));
        } else if (ResourceType.CA.equals(criteria.getResourceType())) {
			whereClause.and(request.competentAuthority.eq(CompetentAuthorityEnum.valueOf(criteria.getResourceId())));
        }
        
        whereClause.and(request.type.in(requestTypesFilteredByResourceType));
        
        if(!criteria.getRequestStatuses().isEmpty()) {
            whereClause.and(request.status.in(criteria.getRequestStatuses()));
        }
        
        // handle not displayed in progress requests
		if (criteria.getRequestStatuses().isEmpty()
				|| criteria.getRequestStatuses().contains(RequestStatus.IN_PROGRESS)) {
			whereClause.andAnyOf(
					criteria.getRequestStatuses().isEmpty() ? request.status.ne(RequestStatus.IN_PROGRESS)
							: request.status.in(criteria.getRequestStatuses().stream()
									.filter(status -> status != RequestStatus.IN_PROGRESS).collect(Collectors.toSet())),
					request.type.notIn(RequestType.getNotDisplayedInProgressRequestTypes()));
		}
		
        JPAQuery<RequestDetailsDTO> jpaQuery = query.select(
                Projections.constructor(RequestDetailsDTO.class,
                        request.id, 
                        request.type, 
                        request.status,
                        request.creationDate,
                        request.metadata))
                .from(request)
                .where(whereClause)
                .orderBy(request.creationDate.desc())
                .offset(criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
                .limit(criteria.getPaging().getPageSize());

        return RequestDetailsSearchResults.builder()
                .requestDetails(jpaQuery.fetch())
                .total(jpaQuery.fetchCount())
                .build();
    }

    public Optional<RequestDetailsDTO> findRequestDetailsById(String requestId) {
        QRequest request = QRequest.request;

        JPAQuery<RequestDetailsDTO> query = new JPAQuery<>(entityManager);

        JPAQuery<RequestDetailsDTO> jpaQuery = query.select(
                Projections.constructor(RequestDetailsDTO.class,
                        request.id,
                        request.type,
                        request.status,
                        request.creationDate,
                        request.metadata))
                .from(request)
                .where(request.id.eq(requestId));

        return Optional.ofNullable(jpaQuery.fetchFirst());
    }
}
