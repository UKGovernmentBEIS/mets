package uk.gov.pmrv.api.aviationreporting.ukets.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSafDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSafPurchase;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsTest {

    @Test
    void getAerSectionAttachmentIds() {
        final UUID additionalDocumentUUId = UUID.randomUUID();
        final UUID certificateFileUUId = UUID.randomUUID();
        final UUID noDoubleCountingDeclarationFileUUId = UUID.randomUUID();
        final UUID evidenceFile1UUID = UUID.randomUUID();
        final UUID evidenceFile2UUID = UUID.randomUUID();
        final UUID evidenceFile3UUID = UUID.randomUUID();
        final UUID evidenceFile4UUID = UUID.randomUUID();

        final AviationAerUkEts aer = AviationAerUkEts.builder()
                .additionalDocuments(EmpAdditionalDocuments.builder()
                        .exist(true)
                        .documents(Set.of(additionalDocumentUUId))
                        .build())
                .operatorDetails(EmpOperatorDetails.builder()
                        .airOperatingCertificate(AirOperatingCertificate.builder()
                                .certificateFiles(Set.of(certificateFileUUId))
                                .build())
                        .build())
                .saf(AviationAerSaf.builder()
                        .safDetails(AviationAerSafDetails.builder()
                                .noDoubleCountingDeclarationFile(noDoubleCountingDeclarationFileUUId)
                                .purchases(List.of(AviationAerSafPurchase.builder()
                                                .evidenceFiles(Set.of(evidenceFile1UUID, evidenceFile2UUID))
                                                .build(),
                                        AviationAerSafPurchase.builder()
                                                .evidenceFiles(Set.of(evidenceFile3UUID, evidenceFile4UUID))
                                                .build()))
                                .build())
                        .build())
                .build();

        assertThat(aer.getAerSectionAttachmentIds()).containsOnly(additionalDocumentUUId, certificateFileUUId, noDoubleCountingDeclarationFileUUId,
                evidenceFile1UUID, evidenceFile2UUID, evidenceFile3UUID, evidenceFile4UUID);
    }
}
