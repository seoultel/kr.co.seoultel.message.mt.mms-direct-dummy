package kr.co.seoultel.message.mt.mms.direct.modules.lgt;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.common.exceptions.message.soap.MCMPSoapRenderException;
import kr.co.seoultel.message.mt.mms.core.common.protocol.KtfProtocol;
import kr.co.seoultel.message.mt.mms.core.common.protocol.LgtProtocol;
import kr.co.seoultel.message.mt.mms.core.common.protocol.SktProtocol;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfResMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtSoapMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtSubmitResMessage;
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

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static kr.co.seoultel.message.mt.mms.core.common.constant.Constants.EUC_KR;

@Slf4j
@Controller
@RequiredArgsConstructor
@Conditional(LgtCondition.class)
public class LgtController {

    protected final HashMapStorage<String, ClientTps> tpsStorage;

    protected final HashMapStorage<String, MessageHistory> historyStorage;
    protected final QueueStorage<LgtDeliveryReportReqMessage> reportQueueStorage;


    @PostMapping(Constants.LGT_REQUEST_URL)
    public ResponseEntity<String> handleMM7SubmitReq(HttpServletRequest httpServletRequest) throws Exception {
        CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(httpServletRequest);
        String soapMessageStr = StreamUtils.copyToString(cachedHttpServletRequest.getInputStream(), Charset.forName(EUC_KR));
        String xml = ConvertorUtil.convertStringToXml(soapMessageStr);

        LgtSubmitReqMessage lgtSubmitReqMessage = new LgtSubmitReqMessage();
        SOAPMessage soapMessage = lgtSubmitReqMessage.fromXml(xml);

        String statusCode = "1000";
        String cpid = lgtSubmitReqMessage.getVasId();
        if (!tpsStorage.containsKey(cpid)) {
            statusCode = LgtProtocol.INVALID_AUTH_PASSWORD;
        } else {
            ClientTps clientTps = tpsStorage.get(cpid);
            if (clientTps.isTpsOver()) {
                statusCode = LgtProtocol.TRAFFIC_IS_OVER;
            } else {
                tpsStorage.put(clientTps.getId(), clientTps.count());
            }
        }

        String statusText = LgtUtil.getLgtResultMessageKor(statusCode);
        String dstMsgId = DateUtil.getDate() + TestUtil.getRandomNumberString(10);



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
            reportQueueStorage.add(lgtDeliveryReportReqMessage);

            log.info("[REPORT] Successfully add LgtDeliveryReportReqMessage[{}] to reportQueue", lgtDeliveryReportReqMessage);
        }

        /* render http header */
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"");
        headers.add(Constants.SOAP_ACTION, "\"\"");

        return new ResponseEntity<>(lgtSubmitResMessage.convertSOAPMessageToString(), headers, HttpStatus.OK);
    }



    public LgtDeliveryReportReqMessage getLgtDeliveryReportReqMessage(LgtSubmitReqMessage lgtSubmitReqMessage, LgtSubmitResMessage lgtSubmitResMessage) throws MCMPSoapRenderException {
        return LgtDeliveryReportReqMessage.builder()
                                          .tid(lgtSubmitResMessage.getTid())
                                          .messageId(lgtSubmitResMessage.getMessageId())
                                          .callback(lgtSubmitReqMessage.getCallback())
                                          .receiver(lgtSubmitReqMessage.getReceiver())
                                          .mmStatus("1000")
                                          .build();
    }
}
