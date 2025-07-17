package uk.gov.pmrv.api.migration;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class MigrationConstants {

    public final String MIGRATION_PROCESS_USER = "migration_process";
    
    public final List<String> ALLOWED_FILE_TYPES = 
        List.of(".doc", ".docx", ".jpg", ".jpeg", ".pdf", ".png", ".ppt", ".pptx", ".tif", ".txt", ".vsd", ".vsdx", ".xls", ".xlsx");
}
