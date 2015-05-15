package ee.sk.hwcrypto.demo.model;

import org.springframework.stereotype.Service;

/**
 * Created by kalver on 15.05.2015.
 */
@Service
public class FileManager {

    private UploadedFile uploadedFile;
    private byte[] signedFile;

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public byte[] getSignedFile() {
        return signedFile;
    }

    public void setSignedFile(byte[] signedFile) {
        this.signedFile = signedFile;
    }
}
