package uk.gov.pmrv.api.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class MimeTypeUtilsTest {

	@Test
	void detect() throws Exception {
		Path file;
		String name;
		String result;
		
		name = "95.xls";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/vnd.ms-excel");
		
		name = "97-2003.doc";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/msword");
		
		name = "97-2003.ppt";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/vnd.ms-powerpoint");
		
		name = "97-2003.xls";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/vnd.ms-excel");
		
		name = "2003-2010.vsd";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/vnd.visio");
		
		name = "Book.xlsx";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		name = "Document.docx";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		
		name = "Drawing.vsdx";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/vnd.ms-visio.drawing");
		
		name = "Image.bmp";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("image/bmp");
		
		name = "Image.dib";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("image/bmp");
		
		name = "Image.jpeg";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("image/jpeg");
		
		name = "Image.jpg";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("image/jpeg");
		
		name = "Image.png";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("image/png");
		
		name = "Image.tif";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("image/tiff");
		
		name = "Image.tiff";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("image/tiff");
		
		name = "Presentation.pptx";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/vnd.openxmlformats-officedocument.presentationml.presentation");
		
		name = "sample.pdf";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("application/pdf");
		
		name = "Text-ansi.txt";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("text/plain");
		
		name = "Text-comma.csv";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("text/csv");
		
		name = "Text-semicolon.csv";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("text/csv");
		
		name = "Text-utf8bom.txt";
		file = Paths.get("src", "test", "resources", "files", "mimetypes", name);
		result = MimeTypeUtils.detect(Files.readAllBytes(file), name);
		assertThat(result).isEqualTo("text/plain");
	}
}
