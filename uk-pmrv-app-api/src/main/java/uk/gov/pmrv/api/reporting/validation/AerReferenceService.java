package uk.gov.pmrv.api.reporting.validation;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerViolation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class AerReferenceService {

    public enum Rule {

        SOURCE_STREAM_EXISTS,
        EMISSION_SOURCES_EXIST,
        EMISSION_POINTS_EXIST
        ;

        private static final Map<AerReferenceService.Rule, AerViolation.AerViolationMessage> MAP =
            ImmutableMap.<AerReferenceService.Rule, AerViolation.AerViolationMessage>builder()
                .put(SOURCE_STREAM_EXISTS, AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM)
                .put(EMISSION_SOURCES_EXIST, AerViolation.AerViolationMessage.INVALID_EMISSION_SOURCE)
                .put(EMISSION_POINTS_EXIST, AerViolation.AerViolationMessage.INVALID_EMISSION_POINT)
                .build();

        public static AerViolation.AerViolationMessage getViolationMessage(final AerReferenceService.Rule rule) {
            return MAP.get(rule);
        }
    }

    public Optional<Pair<AerViolation.AerViolationMessage, List<String>>> validateExistenceInAer(
        final Collection<String> referencesInAer,
        final Collection<String> referencesInSection,
        final AerReferenceService.Rule rule) {

        final List<String> diff = new ArrayList<>(referencesInSection);
        diff.removeAll(referencesInAer);
        diff.remove(null);
        if (!diff.isEmpty()) {
            final AerViolation.AerViolationMessage message = AerReferenceService.Rule.getViolationMessage(rule);
            return Optional.of(Pair.of(message, diff));
        }
        return Optional.empty();
    }
}
