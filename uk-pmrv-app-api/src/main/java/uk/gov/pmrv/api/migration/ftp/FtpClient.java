package uk.gov.pmrv.api.migration.ftp;

import java.util.List;

interface FtpClient extends AutoCloseable {
    
    /**
     * Fetch file
     * @param file the file path
     * @return bytes of the file
     * @throws Exception 
     */
    byte[] fetchFile(String file) throws FtpException;
    
    /**
     * Fetch file in batch mode (leaves the session and channel open)
     * @param file the file path
     * @return bytes of the file
     * @throws Exception
     */
    byte[] fetchFileBatch(String file) throws FtpException;
    
    void healthCheck();

    List<String> getFilePaths(String directory) throws FtpException;
}
