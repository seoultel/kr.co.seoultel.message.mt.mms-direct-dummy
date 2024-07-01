package kr.co.seoultel.message.mt.mms.direct.modules.ktf;

import io.netty.handler.timeout.IdleStateEvent;
import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfProtocol;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfResMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.filter.CachedHttpServletRequest;

import kr.co.seoultel.message.mt.mms.direct.modules.ktf.message.KtfSubmitMessage;
import kr.co.seoultel.mms.server.core.util.TestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static kr.co.seoultel.message.mt.mms.core.common.constant.Constants.EUC_KR;

@Slf4j
@Controller
@RequiredArgsConstructor
@Conditional(KtfCondition.class)
public class KtfController {
    protected final ConcurrentLinkedQueue<KtfSubmitMessage> reportQueue;

    @PostMapping(value = "/")
    public ResponseEntity<String> handleSubmitReq(HttpServletRequest httpServletRequest) throws Exception {
        CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(httpServletRequest);
        String soapMessageStr = StreamUtils.copyToString(cachedHttpServletRequest.getInputStream(), Charset.forName(EUC_KR));
        String xml = ConvertorUtil.convertStringToXml(soapMessageStr);
        KtfSubmitReqMessage ktfSubmitReqMessage = new KtfSubmitReqMessage();
        SOAPMessage soapMessage = ktfSubmitReqMessage.fromXml(xml);

        String dstMsgId = DateUtil.getDate() + TestUtil.getRandomNumberString(10);
        KtfResMessage ktfResMessage = new KtfResMessage(KtfProtocol.SUBMIT_RES);
        ktfResMessage.setTid(ktfSubmitReqMessage.getTid());
        ktfResMessage.setMessageId(dstMsgId);
        ktfResMessage.setStatusCode("1000");
        ktfResMessage.setStatusText("Success");

        log.info("ktfResMessage : {}", ktfResMessage);
        String mm7SubmitResString = ktfResMessage.convertSOAPMessageToString();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"");
        headers.add(Constants.SOAP_ACTION, "\"\"");

        KtfSubmitMessage ktfSubmitMessage = new KtfSubmitMessage(ktfSubmitReqMessage, ktfResMessage);
        reportQueue.add(ktfSubmitMessage);
        return new ResponseEntity<>(mm7SubmitResString, headers, HttpStatus.OK);
    }




    private String readRequestBodyToString(CachedHttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[10000];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        byte[] requestBodyBytes = byteArrayOutputStream.toByteArray();

        return new String(requestBodyBytes, StandardCharsets.UTF_8);
    }
}