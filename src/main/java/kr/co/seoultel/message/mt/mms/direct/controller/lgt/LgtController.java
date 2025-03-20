package kr.co.seoultel.message.mt.mms.direct.controller.lgt;

import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.direct.common.condition.KtfCondition;
import kr.co.seoultel.message.mt.mms.direct.common.condition.LgtCondition;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.common.filter.CachedHttpServletRequest;
import kr.co.seoultel.message.mt.mms.direct.common.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.LgtUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.RandomUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.Util;
import kr.co.seoultel.message.mt.mms.direct.controller.ReportSoapMessageWrapper;
import kr.co.seoultel.message.mt.mms.direct.domain.CpidInfo;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt.LgtDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt.LgtSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt.LgtSubmitResMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.LgtProtocol;
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
@Conditional(LgtCondition.class)
public class LgtController {

    private final Map<String, CpidInfo> cpidInfoMap;
    private final QueueStorage<ReportSoapMessageWrapper<LgtDeliveryReportReqMessage>> reportQueue;

    @PostMapping(DirectProtocol.LGT_REQUEST_URL)
    public ResponseEntity<String> handleMM7SubmitReq(HttpServletRequest httpServletRequest) throws Exception {
        CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(httpServletRequest);
        String soapMessageStr = StreamUtils.copyToString(cachedHttpServletRequest.getInputStream(), Charset.forName(EUC_KR));
        String xml = convertStringToXml(soapMessageStr);

        LgtSubmitReqMessage lgtSubmitReqMessage = new LgtSubmitReqMessage();
        SOAPMessage soapMessage = lgtSubmitReqMessage.fromXml(xml);

        // Util.sleep(100000L);
        String statusCode = "1000";
        String cpid = lgtSubmitReqMessage.getVasId();
        if (!cpidInfoMap.containsKey(cpid)) {
            statusCode = LgtProtocol.INVALID_AUTH_PASSWORD;
        } else {
            CpidInfo cpidInfo = cpidInfoMap.get(cpid);
            if (cpidInfo.isTpsOver()) {
                statusCode = LgtProtocol.TRAFFIC_IS_OVER;
            }
        }

        String statusText = LgtUtil.getLgtResultMessageKor(statusCode);
        String dstMsgId = DateUtil.getDate() + RandomUtil.getRandomNumberString(10);

        /* Render LgtSubmitResponseMessage */
        LgtSubmitResMessage lgtSubmitResMessage = LgtSubmitResMessage.builder()
                .tid(lgtSubmitReqMessage.getTid())
                .messageId(dstMsgId)
                .statusCode(statusCode)
                .statusText("Success")
                .build();

        if (lgtSubmitResMessage.isSuccess()) {
            /* Render LgtDeliveryReportRequestMessage */
            LgtDeliveryReportReqMessage lgtDeliveryReportReqMessage = getLgtDeliveryReportReqMessage(lgtSubmitReqMessage, lgtSubmitResMessage);
            reportQueue.add(new ReportSoapMessageWrapper<>(lgtDeliveryReportReqMessage, cpidInfoMap.get(cpid).getReportUrl()));

            log.info("[REPORT] Successfully add LgtDeliveryReportReqMessage[{}] to reportQueue", lgtDeliveryReportReqMessage);
        }

        /* render http header */
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"");
        headers.add(DirectProtocol.SOAP_ACTION, "\"\"");

         return new ResponseEntity<>(lgtSubmitResMessage.convertSOAPMessageToString(), headers, HttpStatus.OK);
//        return ResponseEntity.badRequest().build();
    }

    public LgtDeliveryReportReqMessage getLgtDeliveryReportReqMessage(LgtSubmitReqMessage lgtSubmitReqMessage, LgtSubmitResMessage lgtSubmitResMessage) throws MMSException {
        return LgtDeliveryReportReqMessage.builder()
                .tid(lgtSubmitResMessage.getTid())
                .messageId(lgtSubmitResMessage.getMessageId())
                .callback(lgtSubmitReqMessage.getCallback())
                .receiver(lgtSubmitReqMessage.getReceiver())
                .mmStatus("Retrieved")
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