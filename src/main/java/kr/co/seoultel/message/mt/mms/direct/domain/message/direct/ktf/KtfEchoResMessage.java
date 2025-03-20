package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf;//package kr.co.seoultel.message.mt.mms.direct.messages.direct.ktf;
//
//import jakarta.xml.soap.*;
//import kr.co.seoultel.message.mt.mms.direct.exception.NewMMSException;
//import kr.co.seoultel.message.mt.mms.direct.protocol.Constants;
//import kr.co.seoultel.message.mt.mms.direct.protocol.KtfProtocol;
//import lombok.Getter;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import javax.xml.namespace.QName;
//
//@Getter
//public class KtfEchoResMessage extends KtfSoapMessage {
//
//    protected String statusCode;
//
//    public KtfEchoResMessage() throws NewMMSException {
//    }
//
//    @Override
//    public SOAPMessage toSOAPMessage() throws MCMPSoapRenderException {
//        try {
//            /* Create SOAP message */
//            SOAPMessage soapMessage = messageFactory.createMessage();
//            soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
//            soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "euc-kr");
//
//            /* SOAP Part */
//            SOAPPart soapPart = soapMessage.getSOAPPart();
//
//            /* SOAP Envelope */
//            SOAPEnvelope envelope = soapPart.getEnvelope();
//            envelope.removeNamespaceDeclaration("SOAP-ENV");
//            envelope.setPrefix("env");
//
//
//            /* SOAP Header */
//            SOAPHeader soapHeader = envelope.getHeader();
//            soapHeader.setPrefix("env");
//            soapHeader.addHeaderElement(new QName(Constants.KTF_REPORT_TRANSACTION_ID_URL, "TransactionID", "mm7"))
//                    .addTextNode("echo")
//                    .setAttribute("env:mustUnderstand", "1");
//
//            /* SOAP Body */
//            SOAPBody soapBody = envelope.getBody();
//            soapBody.setPrefix("env");
//
//            SOAPBodyElement echoRes = soapBody.addBodyElement(new QName(Constants.KTF_REPORT_TRANSACTION_ID_URL, KtfProtocol.ECHO_RES, "mm7"));
//            echoRes.addChildElement("MM7Version").addTextNode(mm7Version);
//
//            SOAPElement status = echoRes.addChildElement("Status");
//            status.addChildElement("StatusCode").addTextNode(statusCode);
//
//            return soapMessage;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new MCMPSoapRenderException("[SOAP] Fail to create KtfSubmitReqMessage", e);
//        }
//    }
//
//    @Override
//    public void fromSOAPMessage(SOAPMessage soapMessage) throws MCMPSoapRenderException {
//        try {
//            SOAPHeader soapHeader = soapMessage.getSOAPHeader();
//            // Directly access the TransactionID element by its QName
//            SOAPElement transactionIdElement = (SOAPElement) soapHeader.getChildElements(new QName(Constants.KTF_TRANSACTION_ID_URL, "TransactionID", "mm7")).next();
//
//            SOAPBody soapBody = soapMessage.getSOAPBody();
//            Document document = soapBody.extractContentAsDocument();
//
//            Element echoReqElement = (Element) document.getElementsByTagName(String.format("mm7:%s", KtfProtocol.ECHO_RES));
//            this.statusCode = getElementValue(echoReqElement, "StatusCode");
//        } catch (Exception e) {
//            throw new MCMPSoapRenderException("[SOAP] Fail to create KtfSubmitReqMessage from SOAPMessage", e);
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "KtfEchoResMessage{" +
//                "statusCode='" + statusCode + '\'' +
//                '}';
//    }
//}
