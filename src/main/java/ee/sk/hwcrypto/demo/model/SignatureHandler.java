package ee.sk.hwcrypto.demo.model;

import ee.sk.digidoc.DataFile;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * Created by kalver on 8.05.2015.
 */
@Service
public class SignatureHandler {

    private static final Logger log = LoggerFactory.getLogger(SignatureHandler.class);

    @Autowired
    FileManager fileManager;

    //TODO session get/set signature
    private Signature sig;

    public String prepareContract(String den) throws DigiDocException, CertificateException {
        Security.addProvider(new BouncyCastleProvider());
        X509Certificate cert = parseCertificate(den);
        System.out.println("got cert " + cert.getSubjectDN().getName());
        SignedDoc sdoc = new SignedDoc(SignedDoc.FORMAT_BDOC, SignedDoc.BDOC_VERSION_2_1);
        sdoc.setProfile(SignedDoc.BDOC_PROFILE_TM);
        System.out.println("creating sdoc " + sdoc.getMimeType());
        //TODO somekind of better solution to add files. Did not find a way to add byte[]
        sdoc.addDataFile(uploadedFileToDisk(fileManager.getUploadedFile().getBytes()),
                "text/plain", DataFile.CONTENT_BINARY);
        System.out.println("DATAFILE ADDED " + BouncyCastleProvider.PROVIDER_NAME);
        sig = sdoc.prepareSignature(cert, null, null);
        System.out.println("creating sdoc " + sdoc.getFile());
        sig.setProfile(SignedDoc.BDOC_PROFILE_TM);
        byte[] sidigest = sig.calculateSignedInfoDigest();
        System.out.println("PREPARED");
        return SignedDoc.bin2hex(sidigest);
    }

    public void signDocument(String signatureInHex) throws DigiDocException {
        sig.setSignatureValue(SignedDoc.hex2bin(signatureInHex));
        SignedDoc signedDoc = sig.getSignedDoc();
        System.out.println("SIGNATURE " + sig);
        System.out.println("doc " + signedDoc.getFile());
        System.out.println("filemanager " + fileManager);
        fileManager.setSignedFile(signedDoc.getFile().getBytes());
    }

    private File uploadedFileToDisk(byte[] myByteArray){
        File file = new File(this.getClass().getResource("/temp").getFile());
        try{
            FileOutputStream fos = new FileOutputStream(file.getPath(),false);
            fos.write(myByteArray);
            fos.close();
        }catch (IOException e){
            log.error("IOException writing to disk " + e);
        }
        System.out.println("file " + file.getTotalSpace());
        return file;
    }

    private X509Certificate parseCertificate(String den) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        byte[] bytes = Base64.getDecoder().decode(den);
        return (X509Certificate)cf.generateCertificate(
                new ByteArrayInputStream(bytes));
    }

}
