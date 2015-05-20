package ee.sk.hwcrypto.demo.model;

import ee.sk.digidoc.DataFile;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kalver on 13.05.2015.
 */
@Service
public class MobileSign {

    private static final Logger log = LoggerFactory.getLogger(SignatureHandler.class);

    @Autowired
    FileManager fileManager;

    public String startSession() throws DigiDocException{
        //Create BDOC to sign
        SignedDoc sdoc = new SignedDoc(SignedDoc.FORMAT_BDOC, SignedDoc.BDOC_VERSION_2_1);
        fileManager.setSignedFileType(".bdoc");
        sdoc.setProfile(SignedDoc.BDOC_PROFILE_TM);
        //TODO somekind of better solution to add files. Did not find a way to add byte[]
        sdoc.addDataFile(new File(fileManager.getUploadedFile().getPath()),
                fileManager.getUploadedFile().getMimeType(), DataFile.CONTENT_BINARY);
        //Encode BDOC to base64
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sdoc.writeToStream(outputStream);
        byte[] encodedBytes = Base64.encodeBase64(outputStream.toByteArray());
        String base64File = new String(encodedBytes);

        //Hold the session so we can add signature later
        String holdSession = "true";

        //get start session query
        return startSessionQuery(base64File, holdSession);
    }

    public String getSignedDoc(String sessionCode){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:d=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:mss=\"http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl\" xmlns:SOAP=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:MIME=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:DIME=\"http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/\" xmlns:WSDL=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"><SOAP-ENV:Body><mns:GetSignedDoc xmlns:mns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><Sesscode xsi:type=\"xsd:int\">"+sessionCode+"</Sesscode></mns:GetSignedDoc></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

    public String getStatusInfo(String sessionCode, String returnDocInfo, String waitSignature){
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:d=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:mss=\"http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl\" xmlns:SOAP=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:MIME=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:DIME=\"http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/\" xmlns:WSDL=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<SOAP-ENV:Body><mns:GetStatusInfo xmlns:mns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<Sesscode xsi:type=\"xsd:int\">"+sessionCode+"</Sesscode>" +
                "<ReturnDocInfo xsi:type=\"xsd:boolean\">"+returnDocInfo+"</ReturnDocInfo>" +
                "<WaitSignature xsi:type=\"xsd:boolean\">"+waitSignature+"</WaitSignature>" +
                "</mns:GetStatusInfo></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

    public String mobileSignQuery(String sessCode, String signerIdCode, String signedPhoneNo, String signersCountry ,String serviceName, String additionalDataToBeDisplayed, String language, String role, String city, String  stateOrProvince, String postalCode, String countryName, String signingProfile ,String messagingMode, String asyncConfiguration, String returnDocInfo, String returnDocData){
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:d=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:mss=\"http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl\" xmlns:SOAP=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:MIME=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:DIME=\"http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/\" xmlns:WSDL=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<SOAP-ENV:Body>" +
                "<mns:MobileSign xmlns:mns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<Sesscode xsi:type=\"xsd:int\">"+sessCode+"</Sesscode>" +
                "<SignerIDCode xsi:type=\"xsd:string\">"+signerIdCode+"</SignerIDCode>" +
                "<SignersCountry xsi:type=\"xsd:string\">"+signersCountry+"</SignersCountry>" +
                "<SignerPhoneNo xsi:type=\"xsd:string\">"+signedPhoneNo+"</SignerPhoneNo>" +
                "<ServiceName xsi:type=\"xsd:string\">"+serviceName+"</ServiceName>" +
                "<AdditionalDataToBeDisplayed xsi:type=\"xsd:string\">"+additionalDataToBeDisplayed+"</AdditionalDataToBeDisplayed>" +
                "<Language xsi:type=\"xsd:string\">"+language+"</Language>" +
                "<Role xsi:type=\"xsd:string\">"+role+"</Role>" +
                "<City xsi:type=\"xsd:string\">"+city+"</City>" +
                "<StateOrProvince xsi:type=\"xsd:string\">"+stateOrProvince+"</StateOrProvince>" +
                "<PostalCode xsi:type=\"xsd:string\">"+postalCode+"</PostalCode>" +
                "<CountryName xsi:type=\"xsd:string\">"+countryName+"</CountryName>" +
                "<SigningProfile xsi:type=\"xsd:string\">"+signingProfile+"</SigningProfile>" +
                "<MessagingMode xsi:type=\"xsd:string\">"+messagingMode+"</MessagingMode>" +
                "<AsyncConfiguration xsi:type=\"xsd:int\">"+asyncConfiguration+"</AsyncConfiguration>" +
                "<ReturnDocInfo xsi:type=\"xsd:boolean\">"+returnDocInfo+"</ReturnDocInfo>" +
                "<ReturnDocData xsi:type=\"xsd:boolean\">"+returnDocData+"</ReturnDocData>" +
                "</mns:MobileSign></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

    private String startSessionQuery(String base64File, String holdSession){
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:d=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" xmlns:mss=\"http://www.sk.ee:8098/MSSP_GW/MSSP_GW.wsdl\" xmlns:SOAP=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:MIME=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:DIME=\"http://schemas.xmlsoap.org/ws/2002/04/dime/wsdl/\" xmlns:WSDL=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<SOAP-ENV:Body>" +
                "<mns:StartSession xmlns:mns=\"http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<SigningProfile xsi:type=\"xsd:string\"></SigningProfile>" +
                "<SigDocXML xsi:type=\"xsd:string\">"+base64File+"</SigDocXML>" +
                "<bHoldSession xsi:type=\"xsd:boolean\">"+holdSession+"</bHoldSession>" +
                "<datafile xsi:type=\"d:DataFileData\">" +
                "<Id SOAP-ENC:position=\"[0]\" xsi:type=\"xsd:string\"></Id>" +
                "<Id SOAP-ENC:position=\"[1]\" xsi:type=\"xsd:string\"></Id>" +
                "<Filename SOAP-ENC:position=\"[0]\" xsi:type=\"xsd:string\"></Filename>" +
                "<Filename SOAP-ENC:position=\"[1]\" xsi:type=\"xsd:string\"></Filename>" +
                "<MimeType SOAP-ENC:position=\"[0]\" xsi:type=\"xsd:string\"></MimeType>" +
                "<MimeType SOAP-ENC:position=\"[1]\" xsi:type=\"xsd:string\"></MimeType>" +
                "<ContentType SOAP-ENC:position=\"[0]\" xsi:type=\"xsd:string\"></ContentType>" +
                "<ContentType SOAP-ENC:position=\"[1]\" xsi:type=\"xsd:string\"></ContentType>" +
                "<DigestType SOAP-ENC:position=\"[0]\" xsi:type=\"xsd:string\"></DigestType>" +
                "<DigestType SOAP-ENC:position=\"[1]\" xsi:type=\"xsd:string\"></DigestType>" +
                "<DigestValue SOAP-ENC:position=\"[0]\" xsi:type=\"xsd:string\"></DigestValue>" +
                "<DigestValue SOAP-ENC:position=\"[1]\" xsi:type=\"xsd:string\"></DigestValue>" +
                "<Size SOAP-ENC:position=\"[0]\" xsi:type=\"xsd:int\"></Size>" +
                "<Size SOAP-ENC:position=\"[1]\" xsi:type=\"xsd:int\"></Size>" +
                "<Attributes xsi:type=\"d:DataFileAttribute\"></Attributes>" +
                "<Attributes xsi:type=\"d:DataFileAttribute\"></Attributes>" +
                "<Attributes xsi:type=\"d:DataFileAttribute\"></Attributes>" +
                "<Attributes xsi:type=\"d:DataFileAttribute\"></Attributes>" +
                "<DfData SOAP-ENC:position=\"[0]\" xsi:type=\"xsd:string\"></DfData>" +
                "<DfData SOAP-ENC:position=\"[1]\" xsi:type=\"xsd:string\"></DfData>" +
                "</datafile>" +
                "</mns:StartSession></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

}
