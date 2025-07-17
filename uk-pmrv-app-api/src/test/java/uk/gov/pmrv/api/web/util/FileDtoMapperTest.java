package uk.gov.pmrv.api.web.util;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FileDtoMapperTest {

    private FileDtoMapper mapper = Mappers.getMapper(FileDtoMapper.class);

    @Test
    void toFileDTO() throws IOException {
        String  content = "content";
        MultipartFile multipartFile = new MockMultipartFile("name", "originalname.txt", "type", content.getBytes());
        
        FileDTO fileDTO = mapper.toFileDTO(multipartFile);
        
        assertThat(fileDTO.getFileContent()).isEqualTo(content.getBytes());
        assertThat(fileDTO.getFileName()).isEqualTo("originalname.txt");
        assertThat(fileDTO.getFileSize()).isEqualTo(content.getBytes().length);
        assertThat(fileDTO.getFileType()).isEqualTo("text/plain");
    }
}
