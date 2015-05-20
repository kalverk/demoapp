package ee.sk.hwcrypto.demo.model;

import ee.sk.digidoc.DataFile;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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
        fileManager.setSignedFileType(".bdoc");
        System.out.println("creating sdoc " + sdoc.getMimeType());
        sdoc.addDataFile(new File(fileManager.getUploadedFile().getPath()),
                fileManager.getUploadedFile().getMimeType(), DataFile.CONTENT_BINARY);
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signedDoc.writeToStream(baos);
        fileManager.setSignedFile(baos.toByteArray());
    }

    private X509Certificate parseCertificate(String den) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        byte[] bytes = Base64.decodeBase64(den);
        return (X509Certificate)cf.generateCertificate(
                new ByteArrayInputStream(bytes));
    }

}
