package uk.gov.pmrv.api.common.utils;

import java.io.IOException;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class MimeTypeUtils {

	public String detect(byte[] content, String fileName) {
		final Detector detector = new DefaultDetector();
        final Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);

		try {
			return detector.detect(TikaInputStream.get(content), metadata).toString();
		} catch (IOException e) {
			log.error(String.format("Error occurred when detecting content type of file: %s",  fileName), e);
			return null;
		}
	}
}
