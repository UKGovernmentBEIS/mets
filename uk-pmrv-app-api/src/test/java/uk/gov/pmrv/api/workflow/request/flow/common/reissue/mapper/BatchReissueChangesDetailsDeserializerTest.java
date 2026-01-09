package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class BatchReissueChangesDetailsDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BatchReissueCompletedRequestActionPayload.class, new BatchReissueChangesDetailsDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void deserialize_permit_with_changesDetails_and_filters() throws IOException {
        String json = """
            {
                "submitter": "User1",
                "signatory": "Sign1",
                "signatoryName": "Sign Name",
                "report": {"name": "report.pdf", "uuid": "uuid-1"},
                "payloadType": "PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD",
                "numberOfAccounts": 5,
                "changesDetails": {"changes": ["change1"], "changesSummary": "summary"},
                "filters": {"accountStatuses": ["LIVE"]}
            }
        """;

        BatchReissueCompletedRequestActionPayload result = objectMapper.readValue(json, BatchReissueCompletedRequestActionPayload.class);

        assertThat(result.getSubmitter()).isEqualTo("User1");
        assertThat(result.getSignatory()).isEqualTo("Sign1");
        assertThat(result.getSignatoryName()).isEqualTo("Sign Name");
        assertThat(result.getReport()).extracting(FileInfoDTO::getName).isEqualTo("report.pdf");
        assertThat(result.getReport()).extracting(FileInfoDTO::getUuid).isEqualTo("uuid-1");
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD);
        assertThat(result.getNumberOfAccounts()).isEqualTo(5);
        assertThat(result.getChangesDetails()).isInstanceOf(PermitBatchReissueChangesDetails.class);
        assertThat(((PermitBatchReissueChangesDetails)result.getChangesDetails()).getChanges()).containsExactly("change1");
        assertThat(((PermitBatchReissueChangesDetails)result.getChangesDetails()).getChangesSummary()).isEqualTo("summary");
        assertThat(result.getFilters()).isInstanceOf(PermitBatchReissueFilters.class);
        assertThat(((PermitBatchReissueFilters)result.getFilters()).getAccountStatuses().size()).isEqualTo(1);
        assertThat(((PermitBatchReissueFilters)result.getFilters()).getAccountStatuses().contains(InstallationAccountStatus.LIVE)).isTrue();
    }

    @Test
    void deserialize_emp_with_changesDetails_and_filters() throws IOException {
        String json = """
            {
                "submitter": "User2",
                "signatory": "Sign2",
                "signatoryName": "Sign Name2",
                "report": {"name": "report2.pdf", "uuid": "uuid-2"},
                "payloadType": "EMP_BATCH_REISSUE_COMPLETED_PAYLOAD",
                "numberOfAccounts": 3,
                "changesDetails": {"changes": ["empChange"]},
                "filters": {"emissionTradingSchemes": ["UK_ETS_AVIATION", "CORSIA"]}
            }
        """;

        BatchReissueCompletedRequestActionPayload result = objectMapper.readValue(json, BatchReissueCompletedRequestActionPayload.class);

        assertThat(result.getSubmitter()).isEqualTo("User2");
        assertThat(result.getSignatory()).isEqualTo("Sign2");
        assertThat(result.getSignatoryName()).isEqualTo("Sign Name2");
        assertThat(result.getReport()).extracting(FileInfoDTO::getName).isEqualTo("report2.pdf");
        assertThat(result.getReport()).extracting(FileInfoDTO::getUuid).isEqualTo("uuid-2");
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.EMP_BATCH_REISSUE_COMPLETED_PAYLOAD);
        assertThat(result.getNumberOfAccounts()).isEqualTo(3);
        assertThat(result.getChangesDetails()).isInstanceOf(EmpBatchReissueChangesDetails.class);
        assertThat(((EmpBatchReissueChangesDetails)result.getChangesDetails()).getChanges()).containsExactly("empChange");
        assertThat(result.getFilters()).isInstanceOf(EmpBatchReissueFilters.class);
        assertThat(((EmpBatchReissueFilters)result.getFilters()).getEmissionTradingSchemes().size()).isEqualTo(2);
        assertThat(((EmpBatchReissueFilters)result.getFilters()).getEmissionTradingSchemes().contains(EmissionTradingScheme.UK_ETS_AVIATION)).isTrue();
        assertThat(((EmpBatchReissueFilters)result.getFilters()).getEmissionTradingSchemes().contains(EmissionTradingScheme.CORSIA)).isTrue();
    }

    @Test
    void deserialize_permit_without_changesDetails_and_filters() throws IOException {
        String json = """
            {
                "submitter": "User3",
                "signatory": "Sign3",
                "signatoryName": "Sign Name3",
                "report": {"name": "report3.pdf", "uuid": "uuid-3"},
                "payloadType": "PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD",
                "numberOfAccounts": 2
            }
        """;

        BatchReissueCompletedRequestActionPayload result = objectMapper.readValue(json, BatchReissueCompletedRequestActionPayload.class);

        assertThat(result.getSubmitter()).isEqualTo("User3");
        assertThat(result.getSignatory()).isEqualTo("Sign3");
        assertThat(result.getSignatoryName()).isEqualTo("Sign Name3");
        assertThat(result.getReport()).extracting(FileInfoDTO::getName).isEqualTo("report3.pdf");
        assertThat(result.getReport()).extracting(FileInfoDTO::getUuid).isEqualTo("uuid-3");
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD);
        assertThat(result.getNumberOfAccounts()).isEqualTo(2);
        assertThat(result.getChangesDetails()).isNull();
        assertThat(result.getFilters()).isNull();
    }

    @Test
    void deserialize_emp_without_changesDetails_and_filters() throws IOException {
        String json = """
            {
                "submitter": "User4",
                "signatory": "Sign4",
                "signatoryName": "Sign Name4",
                "report": {"name": "report4.pdf", "uuid": "uuid-4"},
                "payloadType": "EMP_BATCH_REISSUE_COMPLETED_PAYLOAD",
                "numberOfAccounts": 1
            }
        """;

        BatchReissueCompletedRequestActionPayload result = objectMapper.readValue(json, BatchReissueCompletedRequestActionPayload.class);

        assertThat(result.getSubmitter()).isEqualTo("User4");
        assertThat(result.getSignatory()).isEqualTo("Sign4");
        assertThat(result.getSignatoryName()).isEqualTo("Sign Name4");
        assertThat(result.getReport()).extracting(FileInfoDTO::getName).isEqualTo("report4.pdf");
        assertThat(result.getReport()).extracting(FileInfoDTO::getUuid).isEqualTo("uuid-4");
        assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.EMP_BATCH_REISSUE_COMPLETED_PAYLOAD);
        assertThat(result.getNumberOfAccounts()).isEqualTo(1);
        assertThat(result.getChangesDetails()).isNull();
        assertThat(result.getFilters()).isNull();
    }
}
