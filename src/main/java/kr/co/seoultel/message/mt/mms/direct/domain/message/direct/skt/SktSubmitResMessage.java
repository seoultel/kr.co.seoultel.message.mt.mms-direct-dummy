package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.SktProtocol;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

@Slf4j
@Getter
@ToString
public class SktSubmitResMessage extends SktSoapMessage {

    protected String tid;
    protected String statusCode;
    protected String statusText;
    protected String messageId;


    public SktSubmitResMessage() throws MMSException {
    }

    @Builder
    public SktSubmitResMessage(String tid, String statusCode, String statusText, String messageId) throws MMSException {
        this.tid = tid;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.messageId = messageId;
    }

    public boolean isTpsOver() {
        return statusCode.equals(SktProtocol.EXCEED_MAX_TRANS);
    }

    public boolean isHubspError() {
        return statusCode.equals(SktProtocol.ADDRESSS_ERROR) || statusCode.equals(SktProtocol.SERVICE_DENIED);
    }

    public boolean isSuccess() {
        return statusCode.equals(SktProtocol.SUCCESS);
    }

    @Override
    public SOAPMessage toSOAPMessage() throws Exception {
        // Create SOAP message
        SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
        soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "euc-kr");

        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix("env");

        /* SOAP Header */
        SOAPHeader soapHeader = envelope.getHeader();
        soapHeader.setPrefix("env");
        soapHeader.addHeaderElement(new QName(DirectProtocol.SKT_TRANSACTION_ID_URL, "TransactionID", "mm7"))
                .addTextNode(tid)
                .setAttribute("env:mustUnderstand", "1");

        /* SOAP Body */
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("env");

        SOAPBodyElement submitRsp = soapBody.addBodyElement(new QName(DirectProtocol.SKT_TRANSACTION_ID_URL, SktProtocol.SUBMIT_RES, "mm7"));
        submitRsp.addChildElement("MM7Version").addTextNode("5.3.0");

        SOAPElement status = submitRsp.addChildElement("Status");
        status.addChildElement("StatusCode").addTextNode(statusCode);
        status.addChildElement("StatusText").addTextNode(statusText);

        submitRsp.addChildElement("MessageID").addTextNode(messageId);

        return soapMessage;
    }

    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();

            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.SKT_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get element
            String localpart = document.getDocumentElement().getTagName();
            Element submitRspElement = (Element) document.getElementsByTagName(localpart).item(0);

            // Get mm7:SubmitReq element
            // Element submitRspElement = (Element) document.getElementsByTagName(String.format("mm7:%s", SktProtocol.SUBMIT_RES)).item(0);

            // Extract values from mm7:SubmitReq element
            this.statusCode = getElementValue(submitRspElement, "StatusCode").orElse("");
            this.statusText = getElementValue(submitRspElement, "StatusText").orElse("");
            this.messageId = getElementValue(submitRspElement, "MessageID").orElse("");
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.SUBMIT_RES_SOAP_CREATE_ERROR, null);
        }
    }
}
