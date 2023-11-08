package uk.gov.pmrv.api.files.documents.service;

import java.util.UUID;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.utils.MimeTypeUtils;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.domain.FileDocument;
import uk.gov.pmrv.api.files.documents.repository.FileDocumentRepository;
import uk.gov.pmrv.api.files.documents.transform.FileDocumentMapper;

@Service
@RequiredArgsConstructor
public class FileDocumentService {

    private final FileDocumentRepository fileDocumentRepository;
    private static final FileDocumentMapper fileDocumentMapper = Mappers.getMapper(FileDocumentMapper.class);
    
    @Transactional(readOnly = true)
    public FileDTO getFileDTO(String uuid) {
        return fileDocumentMapper.toFileDTO(fileDocumentRepository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, uuid)));
    }
    
    @Transactional(readOnly = true)
    public FileInfoDTO getFileInfoDTO(String uuid) {
        return fileDocumentMapper.toFileInfoDTO(fileDocumentRepository.findByUuid(uuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, uuid)));
    }
    
    @Transactional
    public FileInfoDTO createFileDocumentWithUuid(byte[] fileContent, String fileName, String uuid) {
       return createFileDocument(fileContent, fileName, uuid);
    }
    
    @Transactional
    public FileInfoDTO createFileDocument(byte[] fileContent, String fileName) {
        return createFileDocument(fileContent, fileName, UUID.randomUUID().toString());
    }
    
    private FileInfoDTO createFileDocument(byte[] fileContent, String fileName, String uuid) {
        FileDocument fileDocument = FileDocument.builder()
                .fileName(fileName)
                .fileContent(fileContent)
                .fileType(MimeTypeUtils.detect(fileContent, fileName))
                .fileSize(fileContent.length)
                .uuid(uuid)
                .status(FileStatus.SUBMITTED)
                .createdBy("system")
                .build();
        fileDocumentRepository.save(fileDocument);
        return FileInfoDTO.builder()
                .name(fileDocument.getFileName())
                .uuid(fileDocument.getUuid())
                .build();
    }
}
