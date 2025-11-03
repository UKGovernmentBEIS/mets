package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;

import java.io.IOException;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;

public class BatchReissueChangesDetailsDeserializer extends StdDeserializer<BatchReissueCompletedRequestActionPayload> {

     public BatchReissueChangesDetailsDeserializer() {
        super(BatchReissueCompletedRequestActionPayload.class);
     }

    @Override
    public BatchReissueCompletedRequestActionPayload deserialize(JsonParser p, DeserializationContext c) throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode node = mapper.readTree(p);

        BatchReissueCompletedRequestActionPayload payload = BatchReissueCompletedRequestActionPayload
                .builder()
                .submitter(node.hasNonNull("submitter")? node.get("submitter").asText() : null)
                .signatory(node.hasNonNull("signatory")? node.get("signatory").asText() : null)
                .signatoryName(node.hasNonNull("signatoryName")? node.get("signatoryName").asText() : null)
                .report(node.hasNonNull("report")? mapper.treeToValue(node.get("report"), FileInfoDTO.class) : null)
                .payloadType(node.hasNonNull("payloadType")? mapper.treeToValue(node.get("payloadType"), RequestActionPayloadType.class) : null)
                .build();

        JsonNode numberOfAccounts = node.get("numberOfAccounts");

        if (numberOfAccounts != null) {
            payload.setNumberOfAccounts(node.get("numberOfAccounts").asInt());
        }

        JsonNode changesDetails = node.get("changesDetails");

        if (changesDetails != null) {
            BatchReissueChangesDetails details = switch (payload.getPayloadType()) {
                case RequestActionPayloadType.PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD,
                     RequestActionPayloadType.PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD ->
                    mapper.treeToValue(changesDetails, PermitBatchReissueChangesDetails.class);
                case RequestActionPayloadType.EMP_BATCH_REISSUE_SUBMITTED_PAYLOAD,
                     RequestActionPayloadType.EMP_BATCH_REISSUE_COMPLETED_PAYLOAD ->
                    mapper.treeToValue(changesDetails, EmpBatchReissueChangesDetails.class);
                default -> null;
            };
            payload.setChangesDetails(details);
        } else {
            payload.setChangesDetails(null);
        }

        JsonNode filters = node.get("filters");
        if (filters != null) {
            BatchReissueFilters batchReissueFilters = switch (payload.getPayloadType()) {
                case RequestActionPayloadType.PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD,
                     RequestActionPayloadType.PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD ->
                    mapper.treeToValue(filters, PermitBatchReissueFilters.class);
                case RequestActionPayloadType.EMP_BATCH_REISSUE_SUBMITTED_PAYLOAD,
                     RequestActionPayloadType.EMP_BATCH_REISSUE_COMPLETED_PAYLOAD ->
                    mapper.treeToValue(filters, EmpBatchReissueFilters.class);
                default -> null;
            };
            payload.setFilters(batchReissueFilters);
        } else {
            payload.setFilters(null);
        }

        return payload;
    }

}
