package ee.sk.hwcrypto.demo.model;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Created by kalver on 15.05.2015.
 */

public class UploadedFile {

    private byte[] bytes;
    private String fileName;
    private String mimeType;

    public UploadedFile(MultipartFile multipartFile) throws IOException {
        this.fileName = multipartFile.getOriginalFilename();
        this.mimeType = multipartFile.getContentType();
        this.bytes = multipartFile.getBytes();
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
