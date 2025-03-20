package kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf;//package kr.co.seoultel.message.mt.mms.direct.messages.direct.ktf;
//
//import jakarta.xml.soap.*;
//import kr.co.seoultel.message.mt.mms.direct.exception.NewMMSException;
//import kr.co.seoultel.message.mt.mms.direct.exception.message.soap.MCMPSoapRenderException;
//import kr.co.seoultel.message.mt.mms.direct.protocol.Constants;
//import kr.co.seoultel.message.mt.mms.direct.protocol.KtfProtocol;
//
//import javax.xml.namespace.QName;
//
//public class KtfEchoReqMessage extends KtfSoapMessage {
//
//    protected final String echo = "Echo";
//
//    public KtfEchoReqMessage() throws NewMMSException {
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
//            soapHeader.addHeaderElement(new QName(Constants.KTF_TRANSACTION_ID_URL, "TransactionID", "mm7"))
//                    .addTextNode("echo")
//                    .setAttribute("env:mustUnderstand", "1");
////
//            /* SOAP Body */
//            SOAPBody soapBody = envelope.getBody();
//            soapBody.setPrefix("env");
////
//            SOAPBodyElement echoReq = soapBody.addBodyElement(new QName(Constants.KTF_TRANSACTION_ID_URL, KtfProtocol.ECHO_REQ, "mm7"));
//            echoReq.addChildElement(echo).addTextNode(echo);
//            echoReq.addChildElement("MM7Version").addTextNode(mm7Version);
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
//
//    }
//
//
//
//    @Override
//    public String toString() {
//        return "KtfEchoReqMessage{" +
//                "echo='" + echo + '\'' +
//                '}';
//    }
//}
