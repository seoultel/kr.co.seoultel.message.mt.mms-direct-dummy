package kr.co.seoultel.message.mt.mms.direct.domain.message.direct;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.message.Message;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

public abstract class SoapMessage extends Message implements ConvertableToSOAPMessage {

    protected final transient MessageFactory messageFactory;

    public SoapMessage() throws MMSException {
        try {
            this.messageFactory = MessageFactory.newInstance();
        } catch (SOAPException e) {
            throw new MMSException(null, ErrorCode.MESSAGE_FACTORY_CREATE_ERROR, null);
        }
    }


    // Helper method to extract text content of an element by tag name
    public Optional<String> getElementValue(Element parentElement, String tagName) throws Exception {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return Optional.of(nodeList.item(0).getTextContent().trim());
        }

        return Optional.empty();
    }

    public String convertSOAPMessageToString() throws Exception {
//        try {
            SOAPMessage soapMessage = toSOAPMessage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            soapMessage.writeTo(baos);

            return baos.toString();
//        } catch (Exception e) {
//            throw new MCMPSoapRenderException("[SOAP] Fail to create soap message", e);
//        }
    }

    public SOAPMessage fromXml(String xml) throws Exception {
        SOAPMessage soapMessage = messageFactory.createMessage((MimeHeaders) null, new ByteArrayInputStream(xml.getBytes()));
        fromSOAPMessage(soapMessage);

        return soapMessage;
    }
}
