package kr.co.seoultel.message.mt.mms.direct.modules.skt;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.common.exceptions.message.soap.MCMPSoapRenderException;
import kr.co.seoultel.message.mt.mms.core.common.protocol.SktProtocol;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktSoapMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktSubmitResMessage;
import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
import kr.co.seoultel.message.mt.mms.direct.ClientTps;
import kr.co.seoultel.message.mt.mms.direct.filter.CachedHttpServletRequest;
import kr.co.seoultel.message.mt.mms.direct.util.DirectUtil;
import kr.co.seoultel.mms.server.core.modules.tps.TpsScheduler;
import kr.co.seoultel.mms.server.core.util.TestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.tags.EscapeBodyTag;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static kr.co.seoultel.message.mt.mms.core.common.constant.Constants.EUC_KR;

@Slf4j
@Controller
@RequiredArgsConstructor
@Conditional(SktCondition.class)
public class SktController {


    protected final HashMapStorage<String, ClientTps> tpsStorage;

    protected final HashMapStorage<String, MessageHistory> historyStorage;
    protected final QueueStorage<SktDeliveryReportReqMessage> reportQueueStorage;


    /*
     * TODO : 1. TPS 초과 로직 작성,
     *        2. 어떤 StatusCode 에 리포트를 전송하는지
     */
    @PostMapping(Constants.SKT_REQUEST_URL)
    public ResponseEntity<String> handleMM7SubmitReq(HttpServletRequest httpServletRequest) throws Exception {
        CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(httpServletRequest);
        String soapMessageStr = StreamUtils.copyToString(cachedHttpServletRequest.getInputStream(), Charset.forName(EUC_KR));
        String xml = ConvertorUtil.convertStringToXml(soapMessageStr);

        SktSubmitReqMessage sktSubmitReqMessage = new SktSubmitReqMessage();
        SOAPMessage soapMessage = sktSubmitReqMessage.fromXml(xml);

        String statusCode = "1000";

        String cpid = sktSubmitReqMessage.getCpid();
        if (!tpsStorage.containsKey(cpid)) {
            statusCode = SktProtocol.CLIENT_ERROR;
        } else {
            ClientTps clientTps = tpsStorage.get(cpid);
            if (clientTps.isTpsOver()) {
                statusCode = SktProtocol.EXCEED_MAX_TRANS;
            } else {
                tpsStorage.put(clientTps.getId(), clientTps.count());
            }
        }

        String statusText = SktUtil.getStatusCodeKor(statusCode);
        String dstMsgId = DateUtil.getDate() + TestUtil.getRandomNumberString(10);

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
            reportQueueStorage.add(sktDeliveryReportReqMessage);
            log.info("[REPORT-QUEUE] Successfully add SktDeliveryReportReqMessage[{}] to reportQueue", sktDeliveryReportReqMessage);
        }

        /* render http header */
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"");
        headers.add(Constants.SOAP_ACTION, "\"\"");

        return new ResponseEntity<>(sktSubmitResMessage.convertSOAPMessageToString(), headers, HttpStatus.OK);
    }


    public void countAndCheckTps(String cpid) {



    }
    public SktDeliveryReportReqMessage getSktDeliveryReportReqMessage(SktSubmitReqMessage sktSubmitReqMessage, SktSubmitResMessage sktSubmitResMessage) throws MCMPSoapRenderException {
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
}
