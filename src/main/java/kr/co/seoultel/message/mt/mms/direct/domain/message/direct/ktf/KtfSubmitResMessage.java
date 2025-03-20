package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.KtfProtocol;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;



@Setter
@Getter
public class KtfSubmitResMessage extends KtfSoapMessage {

    protected String tid;
    protected String messageId;
    protected String statusCode;
    protected String statusText;

    public KtfSubmitResMessage() throws MMSException {
    }

    @Builder
    public KtfSubmitResMessage(String tid, String messageId, String statusCode, String statusText) throws MMSException {
        this.tid = tid;
        this.messageId = messageId;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public boolean isSuccess() {
        return statusCode.equals(KtfProtocol.KTF_SUBMIT_ACK_SUCCESS_RESULT)
                || statusCode.equals(KtfProtocol.KTF_REPORT_SUCCESS)
                || statusCode.equals(KtfProtocol.KTF_REPORT_SUCCESS_ANSIM);
    }

    public boolean isTpsOver() {
        return statusCode.equals(KtfProtocol.KTF_SUBMIT_ACK_HUB_OVER_INTRAFFIC_RESULT);
    }

    public boolean isHubspError() {
        return (statusCode.equals(KtfProtocol.KTF_SUBMIT_ACK_HUB_AUTH_ERROR_RESULT) ||
        statusCode.equals(KtfProtocol.KTF_SUBMIT_ACK_HUB_NOTFOUND_RESULT) ||
        statusCode.equals(KtfProtocol.KTF_SUBMIT_ACK_HUB_BLOCK_RESULT) ||
        statusCode.equals(KtfProtocol.KTF_SUBMIT_ACK_HUB_EXPIRED_RESULT) ||
        statusCode.equals(KtfProtocol.KTF_SUBMIT_ACK_HUB_IP_INVALID_RESULT) ||
        statusCode.equals(KtfProtocol.KTF_SUBMIT_ACK_HUB_CALLBACK_INVALID_RESULT));
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
        soapHeader.addHeaderElement(new QName(DirectProtocol.KTF_TRANSACTION_ID_URL, "TransactionID", "mm7"))
                .addTextNode(tid)
                .setAttribute("env:mustUnderstand", "1");

        /* SOAP Body */
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("env");

        String localpart;
        switch (statusCode) {
            case KtfProtocol.KTF_SUBMIT_ACK_SUCCESS_RESULT:
                localpart = KtfProtocol.SUBMIT_RES;
                break;

            case KtfProtocol.KTF_SUBMIT_ACK_HUB_AUTH_ERROR_RESULT:
            case KtfProtocol.KTF_SUBMIT_ACK_HUB_NOTFOUND_RESULT:
            case KtfProtocol.KTF_SUBMIT_ACK_HUB_BLOCK_RESULT:
            case KtfProtocol.KTF_SUBMIT_ACK_HUB_EXPIRED_RESULT:
            case KtfProtocol.KTF_SUBMIT_ACK_HUB_IP_INVALID_RESULT:
            case KtfProtocol.KTF_SUBMIT_ACK_HUB_CALLBACK_INVALID_RESULT:
                localpart = KtfProtocol.VASP_ERROR_RES;
                break;

            default:
                localpart = KtfProtocol.RS_ERROR_RES;
                break;
        }

        SOAPBodyElement submitRsp = soapBody.addBodyElement(new QName(DirectProtocol.KTF_TRANSACTION_ID_URL, localpart, "mm7"));

        SOAPElement status = submitRsp.addChildElement("Status");
        status.addChildElement("StatusCode").addTextNode(statusCode);
        status.addChildElement("StatusText").addTextNode(statusText);

        submitRsp.addChildElement("MM7Version").addTextNode(mm7Version);
        if (localpart.equals(KtfProtocol.SUBMIT_RES)) submitRsp.addChildElement("MessageID").addTextNode(messageId);

        return soapMessage;
    }

    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();

            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.KTF_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get element
            String localpart = document.getDocumentElement().getTagName();
            Element submitRspElement = (Element) document.getElementsByTagName(localpart).item(0);

            // Extract values from mm7:SubmitReq element
            this.statusCode = getElementValue(submitRspElement, "StatusCode").orElse("");
            this.statusText = getElementValue(submitRspElement, "StatusText").orElse("");
            if (localpart.contains(KtfProtocol.SUBMIT_RES)) this.messageId = getElementValue(submitRspElement, "MessageID").orElse("");

        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.SUBMIT_RES_SOAP_CREATE_ERROR, null);
        }
    }

    @Override
    public String toString() {
        return "KtfSubmitResMessage{" +
                "tid='" + tid + '\'' +
                ", messageId='" + messageId + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", statusText='" + statusText + '\'' +
                '}';
    }
}
