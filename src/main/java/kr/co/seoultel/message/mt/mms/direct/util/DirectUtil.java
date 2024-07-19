package kr.co.seoultel.message.mt.mms.direct.util;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.core.common.exceptions.TpsOverExeption;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktSubmitReqMessage;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DirectUtil {

    public static SOAPMessage getSOAPMessageByString(String soapMessageStr) throws Exception {
        MessageFactory factory = MessageFactory.newInstance();
        return factory.createMessage(null, new ByteArrayInputStream(soapMessageStr.getBytes()));
    }

    public static String convertSOAPMessageToString(SOAPMessage soapMessage) throws SOAPException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapMessage.writeTo(baos);

        return baos.toString();
    }
}
