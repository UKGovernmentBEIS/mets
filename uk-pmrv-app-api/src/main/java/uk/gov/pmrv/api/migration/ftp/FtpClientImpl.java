package uk.gov.pmrv.api.migration.ftp;

import java.io.InputStream;
import java.security.KeyPair;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.client.SftpClient.DirEntry;
import org.apache.sshd.sftp.client.fs.SftpClientDirectoryScanner;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Log4j2
class FtpClientImpl implements FtpClient {
    private final FtpProperties ftpProperties;
    private SshClient sshClient;
    private ClientSession session;
    private SftpClient sftpClient;

    @Override
    public byte[] fetchFile(String file) throws FtpException {
        try (FtpClientImpl client = this) {
            client.connect();
            return getFileBytes(file);
        } catch (Exception e) {
            throw new FtpException("Fetching file from FTP server failed. Reason: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] fetchFileBatch(String file) throws FtpException {
        try {
            if(sshClient == null || sshClient.isClosed()) {
                connect();
            }

            return getFileBytes(file);

        } catch (Exception e) {
            throw new FtpException("Fetching file from FTP server failed. Reason: " + e.getMessage(), e);
        }

    }

    @Override
    public void healthCheck() {
        try(FtpClientImpl client = this) {
            client.connect();
            log.info("FTP server is healthy");
        } catch (IOException e) {
            log.error("Cannot connect to FTP server. Error: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        if (sftpClient != null) {
            sftpClient.close();
        }
        if (session != null) {
            session.close();
        }
        if (sshClient != null) {
            sshClient.stop();
        }
    }

    @Override
    public List<String> getFilePaths(String directory) throws FtpException {
        List<String> filePaths = new ArrayList<>();
        try {
            connect();
            SftpClientDirectoryScanner scanner = new SftpClientDirectoryScanner(directory);
            scanner.setIncludes("*");
            Collection<SftpClientDirectoryScanner.ScanDirEntry> scanEntries = scanner.scan(sftpClient).stream().filter(entry -> !entry.getFilename().startsWith(".")).toList();
            for (SftpClientDirectoryScanner.ScanDirEntry entry : scanEntries) {
                if (entry.getAttributes().isRegularFile()) {
                    filePaths.add(entry.getRelativePath());
                }
            }
            return filePaths;

        } catch (IOException e) {
            throw new FtpException("Fetching file from FTP server failed. Reason: " + e.getMessage(), e);
        }
    }

    private void connect() throws IOException {
        // close client
        close();

        // initialize ssh client
        sshClient = SshClient.setUpDefaultClient();
        sshClient.start();

        // connect via ssh to server and initialize session
        this.session = sshClient.connect(ftpProperties.getUsername(), ftpProperties.getUrl(), ftpProperties.getPort())
            .verify(Duration.ofSeconds(10))
            .getSession();

        // set key
        Iterator<KeyPair> keyPairIterator = new FileKeyPairProvider(ftpProperties.getKeyPath().getFile().toPath()).loadKeys(null).iterator();
        session.addPublicKeyIdentity(KeyIdentityProvider.exhaustCurrentIdentities(keyPairIterator));
        session.auth().verify(Duration.ofSeconds(5));

        // initialize sftp client
        this.sftpClient = SftpClientFactory.instance().createSftpClient(session);
    }


    private byte[] getFileBytes(String file) throws IOException {
        try (InputStream inputStream = sftpClient.read(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        }
    }



}
