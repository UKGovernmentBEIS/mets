package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculationpfc.cellanodetypes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EtsCellAndAnodeType {
    private String etsAccountId;
    private String cellType;
    private String anodeType;
}
