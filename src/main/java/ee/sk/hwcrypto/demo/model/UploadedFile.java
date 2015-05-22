package ee.sk.hwcrypto.demo.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kalver on 15.05.2015.
 */

public class UploadedFile {

    private String fileName;
    private String mimeType;
    private String path;

    public UploadedFile(MultipartFile multipartFile) throws IOException {
        this.fileName = multipartFile.getOriginalFilename();
        this.mimeType = multipartFile.getContentType();
        //TODO assign correct path to save temp file
        this.path = "/usr/local/mgine/digidoc/temp/" + fileName;
        try{
            FileOutputStream fos = new FileOutputStream(path,false);
            fos.write(multipartFile.getBytes());
            fos.close();
        }catch (IOException e){
            System.out.println("IOException writing to disk " + e);
        }
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
