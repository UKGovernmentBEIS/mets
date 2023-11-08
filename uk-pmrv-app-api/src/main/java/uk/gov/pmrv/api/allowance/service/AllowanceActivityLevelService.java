package uk.gov.pmrv.api.allowance.service;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.AllowanceActivityLevelEntity;
import uk.gov.pmrv.api.allowance.mapper.AllowanceMapper;
import uk.gov.pmrv.api.allowance.repository.AllowanceActivityLevelRepository;
import uk.gov.pmrv.api.allowance.validation.AllowanceActivityLevelValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllowanceActivityLevelService {

    private final AllowanceActivityLevelRepository allowanceActivityLevelRepository;
    private final AllowanceActivityLevelValidator allowanceActivityLevelValidator;
    private static final AllowanceMapper ALLOWANCE_MAPPER = Mappers.getMapper(AllowanceMapper.class);

    @Transactional
    public void submitActivityLevels(List<ActivityLevel> activityLevels, Long accountId) {
        // Validate
        allowanceActivityLevelValidator.validate(activityLevels);

        // Submit
        List<AllowanceActivityLevelEntity> entities = ALLOWANCE_MAPPER.toActivityLevelEntities(activityLevels, accountId);
        allowanceActivityLevelRepository.saveAll(entities);
    }
}
