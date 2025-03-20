package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.LgtProtocol;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

@Slf4j
@Getter
@Setter
@ToString
public class LgtDeliveryReportResMessage extends LgtSoapMessage {

    protected String tid;
    protected String statusCode;
    protected String statusText;
    protected String messageId;

    public LgtDeliveryReportResMessage() throws MMSException {}

    @Builder
    public LgtDeliveryReportResMessage(String tid, String statusCode, String statusText, String messageId) throws MMSException {
        this.tid = tid;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.messageId = messageId;
    }

    @Override
    public SOAPMessage toSOAPMessage() throws MMSException {
        try {
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
            soapHeader.addHeaderElement(new QName(DirectProtocol.LGT_TRANSACTION_ID_URL, "TransactionID", "mm7"))
                    .addTextNode(tid)
                    .setAttribute("env:mustUnderstand", "1");

            /* SOAP Body */
            SOAPBody soapBody = envelope.getBody();
            soapBody.setPrefix("env");

            SOAPBodyElement deliveryReportRsp = soapBody.addBodyElement(new QName(DirectProtocol.LGT_TRANSACTION_ID_URL, LgtProtocol.DELIVERY_REPORT_RES, "mm7"));

            SOAPElement status = deliveryReportRsp.addChildElement("Status");
            status.addChildElement("StatusCode").addTextNode(statusCode);
            status.addChildElement("StatusText").addTextNode(statusText);

            deliveryReportRsp.addChildElement("MM7Version").addTextNode("5.3.0");

            return soapMessage;
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.DELIVERY_RES_SOAP_ERROR, null);
        }
    }


    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();

            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.LGT_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get mm7:DeliveryReportRsp element
            Element submitRspElement = (Element) document.getElementsByTagName("mm7:DeliveryReportRsp").item(0);

            // Extract values from mm7:DeliveryReportRsp element
            this.statusCode = getElementValue(submitRspElement, "StatusCode").orElse("");
            this.statusText = getElementValue(submitRspElement, "StatusText").orElse("");
            this.messageId = getElementValue(submitRspElement, "MessageID").orElse("");
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.DELIVERY_RES_SOAP_ERROR, null);
        }
    }
}
