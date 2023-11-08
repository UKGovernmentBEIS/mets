package uk.gov.pmrv.api.migration.workflow.permitbatchreissue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class MigrationPermitBatchReissueSequenceService extends MigrationBaseService {
	
	private static final String SQL = "SELECT ca.fldName ca, max(bo.fldDisplayID) latestReissueId \r\n"
			+ "  FROM tblBatchOperation bo \r\n"
			+ "  join tlkpBatchOperationType bot on bot.fldBatchOperationTypeID = bo.fldBatchOperationTypeID \r\n"
			+ "  join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = bo.fldCompetentAuthorityID \r\n"
			+ " where bot.fldDisplayName = 'Batch Reissue' \r\n"
			+ " group by ca.fldName, bot.fldDisplayName";
	
	private final RequestSequenceRepository requestSequenceRepository;
	private final JdbcTemplate migrationJdbcTemplate;

	@Override
	public List<String> migrate(String ids) {
		List<String> prevalidateErrors = preValidate();
		if(!prevalidateErrors.isEmpty()) {
			return prevalidateErrors;
		}
		
		final List<PermitBatchReissueSequenceVO> batchReissueSequences = migrationJdbcTemplate.query(SQL, new PermitBatchReissueSequenceVOMapper());
		batchReissueSequences.forEach(this::doMigrateRequestSequence);
		return Collections.emptyList();
	}
	
	/**
	 * @return the error list if exist
	 */
	private List<String> preValidate() {
		List<String> errors = new ArrayList<>();
		for (CompetentAuthorityEnum ca : CompetentAuthorityEnum.values()) {
			if(requestSequenceRepository.findByBusinessIdentifierAndType(ca.name(), RequestType.PERMIT_BATCH_REISSUE).isPresent()) {
				errors.add(String.format("For CA %s batch reissues already exist in PMRV db", ca.name()));
			}
		}
		return errors;
	}
	
	private void doMigrateRequestSequence(PermitBatchReissueSequenceVO vo) {
		RequestSequence requestSequence = new RequestSequence(vo.getCa().name(), RequestType.PERMIT_BATCH_REISSUE);
		for (int i = 0; i < vo.getLatestReissueId(); i++) {
			requestSequence.incrementSequenceAndGet();
		}
		requestSequenceRepository.save(requestSequence);
	}

	@Override
	public String getResource() {
		return "permit-batch-reissue-sequence";
	}

}
