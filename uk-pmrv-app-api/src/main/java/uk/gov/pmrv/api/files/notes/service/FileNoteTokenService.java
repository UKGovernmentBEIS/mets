package uk.gov.pmrv.api.files.notes.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.transform.FileMapper;
import uk.gov.pmrv.api.files.notes.repository.FileNoteRepository;
import uk.gov.pmrv.api.token.FileToken;
import uk.gov.pmrv.api.token.UserFileTokenService;

@Service
@RequiredArgsConstructor
public class FileNoteTokenService {

    private final FileNoteRepository fileNoteRepository;
    private final UserFileTokenService userFileTokenService;
    private static final FileMapper fileMapper = Mappers.getMapper(FileMapper.class);
    
    public FileToken generateGetAccountFileNoteToken(final Long accountId, final UUID fileUuid) {

        final boolean exists = fileNoteRepository.existsByAccountIdAndUuid(accountId, fileUuid.toString());
        if (!exists) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return userFileTokenService.generateGetFileToken(fileUuid.toString());
    }
    
    public FileToken generateGetRequestFileNoteToken(final String requestId, final UUID fileUuid) {

        final boolean exists = fileNoteRepository.existsByRequestIdAndUuid(requestId, fileUuid.toString());
        if (!exists) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return userFileTokenService.generateGetFileToken(fileUuid.toString());
    }
    
    @Transactional(readOnly = true)
    public FileDTO getFileDTOByToken(String getFileToken) {
        
        final String fileUuid = userFileTokenService.resolveGetFileUuid(getFileToken);
        return fileNoteRepository.findByUuid(fileUuid)
                .map(fileMapper::toFileDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
