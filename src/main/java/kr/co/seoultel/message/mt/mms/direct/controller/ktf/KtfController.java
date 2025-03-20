package kr.co.seoultel.message.mt.mms.direct.controller.ktf;

import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.direct.common.condition.KtfCondition;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.common.filter.CachedHttpServletRequest;
import kr.co.seoultel.message.mt.mms.direct.common.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.KtfUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.RandomUtil;
import kr.co.seoultel.message.mt.mms.direct.controller.ReportSoapMessageWrapper;
import kr.co.seoultel.message.mt.mms.direct.domain.CpidInfo;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf.KtfSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf.KtfSubmitResMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.KtfProtocol;
import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.QueueStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol.EUC_KR;

@Slf4j
@RestController
@RequiredArgsConstructor
@Conditional(KtfCondition.class)
public class KtfController {

    private final Map<String, CpidInfo> cpidInfoMap;
    private final QueueStorage<ReportSoapMessageWrapper<KtfDeliveryReportReqMessage>> reportQueue;

    @PostMapping(DirectProtocol.KTF_REQUEST_URL)
    public ResponseEntity<String> handleMM7SubmitReq(HttpServletRequest httpServletRequest) throws Exception {
        CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(httpServletRequest);
        String soapMessageStr = StreamUtils.copyToString(cachedHttpServletRequest.getInputStream(), Charset.forName(EUC_KR));
        String xml = convertStringToXml(soapMessageStr);
        log.info("xml : {}", xml);

        KtfSubmitReqMessage ktfSubmitReqMessage = new KtfSubmitReqMessage();
        SOAPMessage soapMessage = ktfSubmitReqMessage.fromXml(xml);

        String statusCode = "1000";
        String cpid = ktfSubmitReqMessage.getCpid();
        if (!cpidInfoMap.containsKey(cpid)) {
            statusCode = KtfProtocol.KTF_SUBMIT_ACK_CLIENT_ERROR_RESULT;
        } else {
            CpidInfo cpidInfo = cpidInfoMap.get(cpid);
            if (cpidInfo.isTpsOver()) {
                statusCode = KtfProtocol.KTF_SUBMIT_ACK_HUB_OVER_INTRAFFIC_RESULT;
            }
        }

        String statusText = KtfUtil.getSubmitAckStatusCodeKor(statusCode);
        String dstMsgId = DateUtil.getDate() + RandomUtil.getRandomNumberString(10);

        KtfSubmitResMessage ktfSubmitResMessage = new KtfSubmitResMessage();
        ktfSubmitResMessage.setTid(ktfSubmitReqMessage.getTid());
        ktfSubmitResMessage.setMessageId(dstMsgId);
        ktfSubmitResMessage.setStatusCode(statusCode);
        ktfSubmitResMessage.setStatusText(statusText);

        if (ktfSubmitResMessage.isSuccess()) {
            /* Render KtfDeliveryReportRequestMessage */
            KtfDeliveryReportReqMessage ktfDeliveryReportReqMessage = getKtfDeliveryReportReqMessage(ktfSubmitReqMessage, ktfSubmitResMessage);
            reportQueue.add(new ReportSoapMessageWrapper<KtfDeliveryReportReqMessage>(ktfDeliveryReportReqMessage, cpidInfoMap.get(cpid).getReportUrl()));

            log.info("[REPORT] Successfully add KtfDeliveryReportReqMessage[{}] to reportQueue", ktfDeliveryReportReqMessage);
        }

        /* render http header */
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"");
        headers.add(DirectProtocol.SOAP_ACTION, "\"\"");

        return new ResponseEntity<>(ktfSubmitResMessage.convertSOAPMessageToString(), headers, HttpStatus.OK);
    }


    public KtfDeliveryReportReqMessage getKtfDeliveryReportReqMessage(KtfSubmitReqMessage ktfSubmitReqMessage,  KtfSubmitResMessage ktfSubmitResMessage) throws MMSException {
        String statusCode = KtfUtil.getRandomStatusCodeByReport();
        String statusText = KtfUtil.getReportStatusCodeKor(statusCode);

        // TestUtil.getRandomStr()
        return KtfDeliveryReportReqMessage.builder()
                .tid(ktfSubmitResMessage.getTid())
                .messageId(ktfSubmitResMessage.getMessageId())
                .callback(ktfSubmitReqMessage.getCallback())
                .receiver(ktfSubmitReqMessage.getReceiver())
                .mmStatus(statusCode)
                .build();
    }

    public static String convertStringToXml(String str) {
        String pattern = "<\\?xml[\\s\\S]*?<\\/env:Envelope>";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(str);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return null; // or throw an exception if XML not found
        }
    }
}