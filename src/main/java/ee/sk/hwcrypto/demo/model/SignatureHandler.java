package ee.sk.hwcrypto.demo.model;

import ee.sk.digidoc.DataFile;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
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
        SignedDoc sdoc = new SignedDoc(SignedDoc.FORMAT_BDOC, SignedDoc.BDOC_VERSION_2_1);
        sdoc.setProfile(SignedDoc.BDOC_PROFILE_TM);
        fileManager.setSignedFileType(".bdoc");
        sdoc.addDataFile(new File(fileManager.getUploadedFile().getPath()),
                fileManager.getUploadedFile().getMimeType(), DataFile.CONTENT_BINARY);
        sig = sdoc.prepareSignature(cert, null, null);
        sig.setProfile(SignedDoc.BDOC_PROFILE_TM);
        byte[] sidigest = sig.calculateSignedInfoDigest();
        return SignedDoc.bin2hex(sidigest);
    }

    public void signDocument(String signatureInHex) throws DigiDocException {
        sig.setSignatureValue(SignedDoc.hex2bin(signatureInHex));
        SignedDoc signedDoc = sig.getSignedDoc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signedDoc.writeToStream(baos);
        fileManager.setSignedFile(baos.toByteArray());
    }

    private X509Certificate parseCertificate(String den) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        byte[] bytes = Base64.decode(den);
        return (X509Certificate)cf.generateCertificate(
                new ByteArrayInputStream(bytes));
    }

}
