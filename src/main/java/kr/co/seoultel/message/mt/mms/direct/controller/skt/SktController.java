package kr.co.seoultel.message.mt.mms.direct.controller.skt;


import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.direct.common.condition.LgtCondition;
import kr.co.seoultel.message.mt.mms.direct.common.condition.SktCondition;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.common.filter.CachedHttpServletRequest;
import kr.co.seoultel.message.mt.mms.direct.common.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.RandomUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.SktUtil;
import kr.co.seoultel.message.mt.mms.direct.controller.ReportSoapMessageWrapper;
import kr.co.seoultel.message.mt.mms.direct.domain.CpidInfo;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt.LgtDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt.SktDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt.SktSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt.SktSubmitResMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.SktProtocol;
import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.QueueStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol.EUC_KR;


@Slf4j
@RestController
@RequiredArgsConstructor
@Conditional(SktCondition.class)
public class SktController {

    private final Map<String, CpidInfo> cpidInfoMap;
    private final QueueStorage<ReportSoapMessageWrapper<SktDeliveryReportReqMessage>> reportQueue;

    @PostMapping(DirectProtocol.SKT_REQUEST_URL)
    public ResponseEntity<String> handleMM7SubmitReq(HttpServletRequest httpServletRequest) throws Exception {
        CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(httpServletRequest);
        String soapMessageStr = StreamUtils.copyToString(cachedHttpServletRequest.getInputStream(), Charset.forName(EUC_KR));
        String xml = convertStringToXml(soapMessageStr);
        assert xml != null;

        // <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"><SOAP-ENV:Header><mm7:TransactionID SOAP-ENV:mustUnderstand="1" xmlns:mm7="http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-0">00001</mm7:TransactionID></SOAP-ENV:Header><SOAP-ENV:Body><mm7:SubmitRsp xmlns:mm7="http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-0"><MM7Version>5.3.0</MM7Version><Status><StatusCode>2101</StatusCode><StatusText>Invalid Content</StatusText></Status><MessageID>y_r2P9IB27N</MessageID></mm7:SubmitRsp></SOAP-ENV:Body></SOAP-ENV:Envelope>
//        return ResponseEntity.badRequest().body("dasfsdf");
//        log.info("xml : {}", xml);
        SktSubmitReqMessage sktSubmitReqMessage = new SktSubmitReqMessage();
        sktSubmitReqMessage.fromXml(xml);

        String statusCode = "1000";
        String cpid = sktSubmitReqMessage.getCpid();
        if (!cpidInfoMap.containsKey(cpid)) {
            statusCode = SktProtocol.CLIENT_ERROR;
        } else {
            CpidInfo cpidInfo = cpidInfoMap.get(cpid);
            if (cpidInfo.isTpsOver()) {
                statusCode = SktProtocol.EXCEED_MAX_TRANS;
            }
        }

        String statusText = SktUtil.getStatusCodeKor(statusCode);
        String dstMsgId = DateUtil.getDate() + RandomUtil.getRandomNumberString(10);

        /* Render SktSubmitResponseMessage */
        SktSubmitResMessage sktSubmitResMessage = SktSubmitResMessage.builder()
                .tid(sktSubmitReqMessage.getTid())
                .messageId(dstMsgId)
                .statusCode(statusCode)
                .statusText(statusText)
                .build();

        if (sktSubmitResMessage.isSuccess()) {
            /* Render SktDeliveryReportRequestMessage */
            SktDeliveryReportReqMessage sktDeliveryReportReqMessage = getSktDeliveryReportReqMessage(sktSubmitReqMessage, sktSubmitResMessage);
            reportQueue.add(new ReportSoapMessageWrapper(sktDeliveryReportReqMessage, cpidInfoMap.get(cpid).getReportUrl()));
            log.info("[REPORT-QUEUE] Successfully add SktDeliveryReportReqMessage[{}] to reportQueue", sktDeliveryReportReqMessage);
        }

        /* render http header */
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"");
        headers.add(DirectProtocol.SOAP_ACTION, "\"\"");

        return new ResponseEntity<>(sktSubmitResMessage.convertSOAPMessageToString(), headers, HttpStatus.OK);
    }


    public SktDeliveryReportReqMessage getSktDeliveryReportReqMessage(SktSubmitReqMessage sktSubmitReqMessage, SktSubmitResMessage sktSubmitResMessage) throws MMSException {
        // String statusCode = SktUtil.getRandomStatusCodeByReport();
        String statusCode = "1000";
        String statusText = SktUtil.getStatusCodeKor(statusCode);


        return SktDeliveryReportReqMessage.builder()
                .tid(sktSubmitResMessage.getTid())
                .messageId(sktSubmitResMessage.getMessageId())
                .senderAddress(sktSubmitReqMessage.getCallback())
                .receiver(sktSubmitReqMessage.getReceiver())
                .statusCode(statusCode)
                .statusText(statusText)
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