/**
 * DSS Hwcrypto Demo
 *
 * Copyright (c) 2015 Estonian Information System Authority
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ee.sk.hwcrypto.demo.controller;

import ee.sk.hwcrypto.demo.model.*;
import ee.sk.hwcrypto.demo.model.UploadedFile;
import ee.sk.hwcrypto.demo.model.Result;
import ee.sk.hwcrypto.demo.util.SOAPMessageParser;
import ee.sk.utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.soap.*;
import java.io.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

@RestController
public class SigningController {

    private static final Logger log = LoggerFactory.getLogger(SigningController.class);

    @Autowired
    SOAPMessageParser soapMessageParser;

    @Autowired
    FileManager fileManager;

    @Autowired
    SignatureHandler signatureHandler;

    @Autowired
    MobileSign mobileSign;

    @RequestMapping(value="/upload", method= RequestMethod.POST)
    public Result handleUpload(@RequestParam MultipartFile file) {
        log.debug("Handling file upload for file " + file.getOriginalFilename());
        try {
            fileManager.setUploadedFile(new UploadedFile(file));
            return Result.resultOk();
        } catch (IOException e) {
            log.error("Error reading bytes from uploaded file " + file.getOriginalFilename(), e);
        }
        return Result.resultUploadingError();
    }

    @RequestMapping(value="/generateHash", method = RequestMethod.POST)
    public Digest generateHash(@RequestParam String cert) {
        log.debug("Generating hash from cert " + cert);
        //TODO initiate this when app is loaded
        configManagerInit();
        log.debug("config is inited ");
        Digest digest = new Digest();
        try {
            String data = signatureHandler.prepareContract(cert);
            System.out.println("signature is ready ");
            digest.setHex(data);
            digest.setResult(Result.OK);
        } catch (Exception e) {
            log.error("Error ", e);
            digest.setResult(Result.ERROR_GENERATING_HASH);
        }
        return digest;
    }

    @RequestMapping(value="/createContainer", method = RequestMethod.POST)
    public Result createContainer(@RequestParam String signatureInHex) {
        log.debug("Creating container for signature " + signatureInHex);
        try {
            signatureHandler.signDocument(signatureInHex);
            return Result.resultOk();
        } catch (Exception e) {
            log.error("Error Signing document", e);
        }
        return Result.resultSigningError();
    }

    @RequestMapping(value="/identify", method = RequestMethod.POST)
    public Digest identifyUser(@RequestParam String certificate) {
        Digest digest = new Digest();
        try {
            CertificateFactory cf =  CertificateFactory.getInstance("X.509");
            byte[] bytes = Base64.getDecoder().decode(certificate);
            InputStream stream = new ByteArrayInputStream(bytes);
            X509Certificate cert = (X509Certificate)cf.generateCertificate(stream);
            cert.checkValidity();
            //result = cert.getSubjectDN().getName();
            digest.setHex(cert.getSubjectDN().getName());
            digest.setResult(Result.OK);
            //TODO create session for user cert.getSubjectDN().getName()
            return digest;
        } catch (Exception e) {
            log.error("Error identify ", e);
            digest.setResult(Result.ERROR);
        }
        return digest;
    }

    @RequestMapping(value="/mobileauth", method = RequestMethod.POST)
    public Digest mobileAuth(@RequestParam String id,@RequestParam String phoneNumber){
        System.out.println("mobileauth");
        Digest digest = new Digest();
        try{
            //TODO assign correct parameters
            String nationality = "EE";
            String language = "EST";
            String service = "Testimine";
            String messageDisplayedOnPhone = "Beer tastes good!";
            String type = "asynchClientServer";
            //TODO can add some additional parameters if necessary, check documentation http://www.id.ee/public/SK-JDD-PRG-GUIDE.pdf
            MobileAuthenticate mobileAuthenticate = new MobileAuthenticate(id.trim(), nationality, phoneNumber.trim(), language, service, messageDisplayedOnPhone, "", type, "", "", "");
            String req = mobileAuthenticate.query();
            SOAPMessage result = SOAPQuery(req);
            String[] qResult = soapMessageParser.parseMobileAuthResponse(result);
            digest.setResult(Result.OK);
            digest.setHex(Arrays.toString(qResult));
            //TODO create session from SOAPMessage response
        }catch (SOAPException e){
            log.error("Error with SOAP " + e);
            digest.setResult(Result.ERROR);
        }
        return digest;
    }

    @RequestMapping(value="/mobilesign", method = RequestMethod.POST)
    public void startsession(@RequestParam String id,@RequestParam String phoneNumber){
        try{
            //Datafile limit 4 MB, if file is bigger send only hex
            System.out.println("mobilesign");
            //TODO initiate this when app is loaded
            configManagerInit();

            String startSessionQuery = mobileSign.startSession();
            SOAPMessage startSessionResponse = SOAPQuery(startSessionQuery);

            String[] responseParameters = soapMessageParser.parseStartSessionResponse(startSessionResponse);
            String status = responseParameters[0];
            String sessCode = responseParameters[1];
            //Session is started
            System.out.println("status " + status);
            System.out.println("sesscode " + sessCode);
            if(status.equalsIgnoreCase("OK")){
                System.out.println("startsession");
                String mobileSignQueryStatus = mobileSignQuery(sessCode,id,phoneNumber);
                if(mobileSignQueryStatus.equalsIgnoreCase("OK")){
                    System.out.println("Mobilesign is OK");
                    String statusInfo = getStatusInfo(sessCode);
                    if(statusInfo.equalsIgnoreCase("SIGNATURE")){
                        System.out.println("Signature has been given");
                        String[] document = getSignedDocument(sessCode);
                        String docStatus = document[0];
                        String doc = document[1];
                        if(docStatus.equalsIgnoreCase("OK")){
                            System.out.println("Downloading file");
                            String escaped = escapeHtml(doc);
                            byte[] decodedBytes = Base64.getDecoder().decode(escaped);
                            fileManager.setSignedFile(decodedBytes);
                        }
                    }
                }
            }

        }catch (Exception e){
            System.out.println("Error " + e);
        }

    }

    private String mobileSignQuery(String sessCode, String id, String phoneNo) throws SOAPException{
        //TODO can add some additional parameters if necessary, check documentation http://www.sk.ee/upload/files/DigiDocService_spec_est.pdf
        String mobileSignQuery = mobileSign.mobileSignQuery(sessCode, id, phoneNo, "", "Testimine", "", "EST", "", "", "", "", "", "", "asynchClientServer", "", "true", "true");
        SOAPMessage mobileSignSessionResponse = SOAPQuery(mobileSignQuery);
        String[] mobileSignParameters = soapMessageParser.parseMobileSignResponse(mobileSignSessionResponse);
        return mobileSignParameters[0];
    }

    private String getStatusInfo(String sessCode) throws SOAPException{
        String statusInfoQuery = mobileSign.getStatusInfo(sessCode, "true", "true");
        //This is filled when the signature is given
        SOAPMessage statusResponse = SOAPQuery(statusInfoQuery);

        //All codes that can come page 37 StatusCode
        //http://www.sk.ee/upload/files/DigiDocService_spec_est.pdf
        return soapMessageParser.parseGetStatusInfo(statusResponse);
    }

    private String[] getSignedDocument(String sessCode) throws SOAPException{
        String getSignedDocumentQuery = mobileSign.getSignedDoc(sessCode);
        SOAPMessage getSignedDocumentResponse = SOAPQuery(getSignedDocumentQuery);
        String[] result = soapMessageParser.parseGetSignedDoc(getSignedDocumentResponse);
        String docStatus = result[0];
        String doc = result[1];
        return new String[]{docStatus,doc};
    }

    private SOAPMessage SOAPQuery(String req){
        try{
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            String url = "http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl";

            MessageFactory messageFactory = MessageFactory.newInstance();
            InputStream is = new ByteArrayInputStream(req.getBytes());
            SOAPMessage soapMessage = messageFactory.createMessage(null, is);
            SOAPPart soapPart = soapMessage.getSOAPPart();

            String serverURI = "https://www.openxades.org:8443/DigiDocService/";

            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("", url);
            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", "");
            soapMessage.saveChanges();

            SOAPMessage soapResponse = soapConnection.call(soapMessage, serverURI);

            return soapResponse;
        }catch (Exception e) {
            System.out.println("SOAP Exception " + e);
        }
        return null;
    }

    private void configManagerInit(){
        ConfigManager.init("C:\\Users\\kalver\\IdeaProjects\\dss-hwcrypto-demo-master\\src\\main\\resources\\jdigidoc.cfg");
    }
}
