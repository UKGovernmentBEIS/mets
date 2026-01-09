package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ReissueChangesDetailsDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ReissueCompletedRequestActionPayload.class, new ReissueChangesDetailsDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void deserialize_changesDetailsNotNull_permit () throws IOException {

        String json = """
                {"document": {"name": "UK-S-IN-00177 v33.pdf", "uuid": "0d81ee33-04e7-4b5b-8451-e308f96e9005"}, "signatory": "2aa31dad-82ec-49c0-8040-0275648f10e7", "submitter": "RegulatorSC1 RegulatorSC1", "payloadType": "PERMIT_REISSUE_COMPLETED_PAYLOAD", "signatoryName": "RegulatorSC1 RegulatorSC1", "changesDetails": {"changes": ["test"], "changesSummary": "asd"}, "officialNotice": {"name": "Batch_variation_notice.pdf", "uuid": "5e52b471-ed8c-4b32-b6cd-c183a93aa2bf"}}
                """;

        ReissueCompletedRequestActionPayload result = objectMapper.readValue(json, ReissueCompletedRequestActionPayload.class);

        assertThat(result.getDocument().getName()).isEqualTo("UK-S-IN-00177 v33.pdf");
        assertThat(result.getDocument().getUuid()).isEqualTo("0d81ee33-04e7-4b5b-8451-e308f96e9005");
        assertThat(result.getSignatory()).isEqualTo("2aa31dad-82ec-49c0-8040-0275648f10e7");
        assertThat(result.getSubmitter()).isEqualTo("RegulatorSC1 RegulatorSC1");
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_REISSUE_COMPLETED_PAYLOAD);
        assertThat(result.getSignatoryName()).isEqualTo("RegulatorSC1 RegulatorSC1");
        assertThat(result.getChangesDetails()).isInstanceOf(PermitBatchReissueChangesDetails.class);
        assertThat(((PermitBatchReissueChangesDetails)result.getChangesDetails()).getChanges()).containsExactly( "test");
        assertThat(((PermitBatchReissueChangesDetails)result.getChangesDetails()).getChangesSummary()).isEqualTo( "asd");
        assertThat(result.getOfficialNotice().getName()).isEqualTo("Batch_variation_notice.pdf");
        assertThat(result.getOfficialNotice().getUuid()).isEqualTo("5e52b471-ed8c-4b32-b6cd-c183a93aa2bf");

    }

    @Test
    void deserialize_changesDetailsNull_permit () throws IOException {

        String json = """
                {"document": {"name": "UK-S-IN-00177 v33.pdf", "uuid": "0d81ee33-04e7-4b5b-8451-e308f96e9005"}, "signatory": "2aa31dad-82ec-49c0-8040-0275648f10e7", "submitter": "RegulatorSC1 RegulatorSC1", "payloadType": "PERMIT_REISSUE_COMPLETED_PAYLOAD", "signatoryName": "RegulatorSC1 RegulatorSC1",  "officialNotice": {"name": "Batch_variation_notice.pdf", "uuid": "5e52b471-ed8c-4b32-b6cd-c183a93aa2bf"}}
                """;

        ReissueCompletedRequestActionPayload result = objectMapper.readValue(json, ReissueCompletedRequestActionPayload.class);

        assertThat(result.getDocument().getName()).isEqualTo("UK-S-IN-00177 v33.pdf");
        assertThat(result.getDocument().getUuid()).isEqualTo("0d81ee33-04e7-4b5b-8451-e308f96e9005");
        assertThat(result.getSignatory()).isEqualTo("2aa31dad-82ec-49c0-8040-0275648f10e7");
        assertThat(result.getSubmitter()).isEqualTo("RegulatorSC1 RegulatorSC1");
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_REISSUE_COMPLETED_PAYLOAD);
        assertThat(result.getSignatoryName()).isEqualTo("RegulatorSC1 RegulatorSC1");
        assertThat(result.getChangesDetails()).isNull();
        assertThat(result.getOfficialNotice().getName()).isEqualTo("Batch_variation_notice.pdf");
        assertThat(result.getOfficialNotice().getUuid()).isEqualTo("5e52b471-ed8c-4b32-b6cd-c183a93aa2bf");

    }

    @Test
    void deserialize_changesDetailsNotNull_emp () throws IOException {

        String json = """
                {"document": {"name": "UK-S-IN-00177 v33.pdf", "uuid": "0d81ee33-04e7-4b5b-8451-e308f96e9005"}, "signatory": "2aa31dad-82ec-49c0-8040-0275648f10e7", "submitter": "RegulatorSC1 RegulatorSC1", "payloadType": "EMP_REISSUE_COMPLETED_PAYLOAD", "signatoryName": "RegulatorSC1 RegulatorSC1", "changesDetails": {"changes": ["test"]}, "officialNotice": {"name": "Batch_variation_notice.pdf", "uuid": "5e52b471-ed8c-4b32-b6cd-c183a93aa2bf"}}
                """;

        ReissueCompletedRequestActionPayload result = objectMapper.readValue(json, ReissueCompletedRequestActionPayload.class);

        assertThat(result.getDocument().getName()).isEqualTo("UK-S-IN-00177 v33.pdf");
        assertThat(result.getDocument().getUuid()).isEqualTo("0d81ee33-04e7-4b5b-8451-e308f96e9005");
        assertThat(result.getSignatory()).isEqualTo("2aa31dad-82ec-49c0-8040-0275648f10e7");
        assertThat(result.getSubmitter()).isEqualTo("RegulatorSC1 RegulatorSC1");
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.EMP_REISSUE_COMPLETED_PAYLOAD);
        assertThat(result.getSignatoryName()).isEqualTo("RegulatorSC1 RegulatorSC1");
        assertThat(result.getChangesDetails()).isInstanceOf(EmpBatchReissueChangesDetails.class);
        assertThat(((EmpBatchReissueChangesDetails)result.getChangesDetails()).getChanges()).containsExactly( "test");
        assertThat(result.getOfficialNotice().getName()).isEqualTo("Batch_variation_notice.pdf");
        assertThat(result.getOfficialNotice().getUuid()).isEqualTo("5e52b471-ed8c-4b32-b6cd-c183a93aa2bf");

    }

    @Test
    void deserialize_changesDetailsNull_emp () throws IOException {

        String json = """
                {"document": {"name": "UK-S-IN-00177 v33.pdf", "uuid": "0d81ee33-04e7-4b5b-8451-e308f96e9005"}, "signatory": "2aa31dad-82ec-49c0-8040-0275648f10e7", "submitter": "RegulatorSC1 RegulatorSC1", "payloadType": "EMP_REISSUE_COMPLETED_PAYLOAD", "signatoryName": "RegulatorSC1 RegulatorSC1", "officialNotice": {"name": "Batch_variation_notice.pdf", "uuid": "5e52b471-ed8c-4b32-b6cd-c183a93aa2bf"}}
                """;

        ReissueCompletedRequestActionPayload result = objectMapper.readValue(json, ReissueCompletedRequestActionPayload.class);

        assertThat(result.getDocument().getName()).isEqualTo("UK-S-IN-00177 v33.pdf");
        assertThat(result.getDocument().getUuid()).isEqualTo("0d81ee33-04e7-4b5b-8451-e308f96e9005");
        assertThat(result.getSignatory()).isEqualTo("2aa31dad-82ec-49c0-8040-0275648f10e7");
        assertThat(result.getSubmitter()).isEqualTo("RegulatorSC1 RegulatorSC1");
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.EMP_REISSUE_COMPLETED_PAYLOAD);
        assertThat(result.getSignatoryName()).isEqualTo("RegulatorSC1 RegulatorSC1");
        assertThat(result.getChangesDetails()).isNull();
        assertThat(result.getOfficialNotice().getName()).isEqualTo("Batch_variation_notice.pdf");
        assertThat(result.getOfficialNotice().getUuid()).isEqualTo("5e52b471-ed8c-4b32-b6cd-c183a93aa2bf");

    }
}
