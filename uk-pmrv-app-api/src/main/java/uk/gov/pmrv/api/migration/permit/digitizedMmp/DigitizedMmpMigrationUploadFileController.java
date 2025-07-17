package uk.gov.pmrv.api.migration.permit.digitizedMmp;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationEntity;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.repository.MmpFilesMigrationRepository;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping(path = "/v1.0/migration-mmp")
@RequiredArgsConstructor
public class DigitizedMmpMigrationUploadFileController {

    private final MmpFilesMigrationRepository mmpFilesMigrationRepository;

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadMmp(@RequestPart("id") @Parameter String id, @RequestPart("type") @Parameter MmpFileType fileType,
                                            @RequestPart("file") @Parameter MultipartFile file) {
        Long accountId = -1l;
        try {
            accountId = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Failure: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Optional<MmpFilesMigrationEntity> mmpFilesOptional =
            mmpFilesMigrationRepository.findByAccountIdAndMmpFileType(accountId, fileType);
        if (mmpFilesOptional.isEmpty()) {
            return new ResponseEntity<>("Cannot find entry for mmp migration with account id '" + id + "'",
                HttpStatus.BAD_REQUEST);
        }
        MmpFilesMigrationEntity mmpFilesMigrationEntity = mmpFilesOptional.get();

        try {
            mmpFilesMigrationEntity.setFileContent(file.getBytes());
        } catch (IOException e) {
            return new ResponseEntity<>("Failure: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        mmpFilesMigrationRepository.save(mmpFilesMigrationEntity);
        return new ResponseEntity<>("File for account with id '" + id + "' successfully updated!", HttpStatus.OK);
    }

}
