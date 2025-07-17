package uk.gov.pmrv.api.notification.template.service;

public interface DocumentGeneratorClientService {

	public byte[] generateDocument(byte[] source, String fileNameToGenerate) throws Exception;
	
}
