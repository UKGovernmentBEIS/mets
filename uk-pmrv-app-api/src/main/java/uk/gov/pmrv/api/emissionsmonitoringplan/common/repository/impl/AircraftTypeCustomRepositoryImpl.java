package uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.AircraftType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.AircraftTypeId;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.QAircraftType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchCriteria;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchResults;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.AircraftTypeCustomRepository;

@Repository
public class AircraftTypeCustomRepositoryImpl implements AircraftTypeCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AircraftTypeSearchResults findBySearchCriteria(AircraftTypeSearchCriteria searchCriteria) {
        QAircraftType aircraftType = QAircraftType.aircraftType;

        StringPath manufacturer = aircraftType.aircraftTypeId.manufacturer;
        StringPath model = aircraftType.aircraftTypeId.model;
        StringPath designatorType = aircraftType.aircraftTypeId.designatorType;

        JPAQuery<AircraftTypeDTO> query = new JPAQuery<>(entityManager);

        BooleanBuilder whereClause = buildWhereClauseFromSearchCriteria(aircraftType, searchCriteria);

        JPAQuery<AircraftTypeDTO> jpaQuery = query.select(
                Projections.constructor(AircraftTypeDTO.class,
                    manufacturer,
                    model,
                    designatorType))
            .from(aircraftType)
            .where(whereClause)
            .orderBy(manufacturer.asc(), model.asc(), designatorType.asc())
            .offset(searchCriteria.getPaging().getPageNumber() * searchCriteria.getPaging().getPageSize())
            .limit(searchCriteria.getPaging().getPageSize());

        return AircraftTypeSearchResults.builder()
            .aircraftTypes(jpaQuery.fetch())
            .total(jpaQuery.fetchCount())
            .build();
    }

    @Override
    public List<String> findInvalidDesignatorCodes(List<String> designatorCodes) {
        if (designatorCodes == null || designatorCodes.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<AircraftType> from = criteriaQuery.from(AircraftType.class);

        criteriaQuery.select(from.get("aircraftTypeId").get("designatorType"));
        criteriaQuery.where(from.get("aircraftTypeId").get("designatorType").in(designatorCodes));

        List<String> foundCodes = entityManager.createQuery(criteriaQuery).getResultList();

        return designatorCodes.stream()
            .filter(code -> !foundCodes.contains(code))
            .collect(Collectors.toList());
    }

    private BooleanBuilder buildWhereClauseFromSearchCriteria(QAircraftType aircraftType, AircraftTypeSearchCriteria searchCriteria) {
        BooleanBuilder whereClause = new BooleanBuilder();

        String term = searchCriteria.getTerm();

        if(!ObjectUtils.isEmpty(term)) {
            String searchTerm = "%" + term.toLowerCase() + "%";

            whereClause.and(aircraftType.aircraftTypeId.manufacturer.likeIgnoreCase(searchTerm)
                .or(aircraftType.aircraftTypeId.model.likeIgnoreCase(searchTerm))
                .or(aircraftType.aircraftTypeId.designatorType.likeIgnoreCase(searchTerm))
            );
        }

        if(!searchCriteria.getExcludedAircraftTypes().isEmpty()) {
            List<AircraftTypeId> excludedAircraftTypes = searchCriteria.getExcludedAircraftTypes().stream()
                .map(dto -> new AircraftTypeId(dto.getManufacturer(), dto.getModel(), dto.getDesignatorType()))
                .collect(Collectors.toList());

            whereClause.and((aircraftType.aircraftTypeId.notIn(excludedAircraftTypes)));
        }

        return whereClause;
    }
}
