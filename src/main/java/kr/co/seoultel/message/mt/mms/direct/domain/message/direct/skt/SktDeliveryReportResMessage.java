package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt;

import jakarta.xml.soap.*;
import kr.co.seoultel.message.mt.mms.direct.common.exception.ErrorCode;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.SktProtocol;
import lombok.Builder;
import lombok.ToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

@ToString
public class SktDeliveryReportResMessage extends SktSoapMessage {

    protected String tid;
    protected String statusCode;
    protected String statusText;

    public SktDeliveryReportResMessage() throws MMSException {
    }

    @Builder
    public SktDeliveryReportResMessage(String tid, String statusCode, String statusText) throws MMSException {
        this.tid = tid;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    @Override
    public SOAPMessage toSOAPMessage() throws MMSException {
        try {
            // Create SOAP message
            SOAPMessage soapMessage = messageFactory.createMessage();
            soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
            soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "euc-kr");

            /* SOAP Part */
            SOAPPart soapPart = soapMessage.getSOAPPart();

            /* SOAP Envelope */
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

            SOAPBodyElement deliveryReportReq = soapBody.addBodyElement(new QName(DirectProtocol.SKT_TRANSACTION_ID_URL, SktProtocol.DELIVERY_REPORT_RES, "mm7"));
            deliveryReportReq.addChildElement("MM7Version").addTextNode("5.3.0");

            SOAPElement status = deliveryReportReq.addChildElement("Status");
            status.addChildElement("StatusCode").addTextNode(statusCode);
            status.addChildElement("StatusText").addTextNode(statusText);

            return soapMessage;
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.DELIVERY_RES_SOAP_ERROR, null);
        }
    }

    @Override
    public void fromSOAPMessage(SOAPMessage soapMessage) throws MMSException {
        try {
            SOAPHeader soapHeader = soapMessage.getSOAPHeader();

            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(DirectProtocol.SKT_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
            this.tid = transactionIdElement != null ? transactionIdElement.getValue() : null;

            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();

            // Get mm7:SubmitReq element
            Element deliveryReportRsqElement = (Element) document.getElementsByTagName(String.format("mm7:%s", SktProtocol.DELIVERY_REPORT_RES)).item(0);

            this.statusCode = getElementValue(deliveryReportRsqElement, "StatusCode").orElse("");
            this.statusText = getElementValue(deliveryReportRsqElement, "StatusText").orElse("");
        } catch (Exception e) {
            throw new MMSException(null, ErrorCode.DELIVERY_RES_SOAP_ERROR, null);
        }
    }

}
