package kr.co.seoultel.message.mt.mms.direct.modules.ktf;


import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.common.exceptions.message.soap.MCMPSoapRenderException;
import kr.co.seoultel.message.mt.mms.core.common.protocol.KtfProtocol;
import kr.co.seoultel.message.mt.mms.core.common.protocol.SktProtocol;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfResMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtSubmitReqMessage;
import kr.co.seoultel.message.mt.mms.core.util.ConvertorUtil;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
import kr.co.seoultel.message.mt.mms.direct.ClientTps;
import kr.co.seoultel.message.mt.mms.direct.filter.CachedHttpServletRequest;
import kr.co.seoultel.message.mt.mms.direct.modules.skt.SktUtil;
import kr.co.seoultel.message.mt.mms.direct.tps.CpidInfo;
import kr.co.seoultel.message.mt.mms.direct.util.DirectUtil;
import kr.co.seoultel.mms.server.core.modules.tps.TpsScheduler;
import kr.co.seoultel.mms.server.core.util.TestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static kr.co.seoultel.message.mt.mms.core.common.constant.Constants.EUC_KR;

@Slf4j
@Controller
@RequiredArgsConstructor
@Conditional(KtfCondition.class)
public class KtfController {

    protected final HashMapStorage<String, ClientTps> tpsStorage;

    protected final HashMapStorage<String, MessageHistory> historyStorage;
    protected final QueueStorage<KtfDeliveryReportReqMessage> reportQueueStorage;


    @PostMapping(Constants.KTF_REQUEST_URL)
    public ResponseEntity<String> handleMM7SubmitReq(HttpServletRequest httpServletRequest) throws Exception {
        CachedHttpServletRequest cachedHttpServletRequest = new CachedHttpServletRequest(httpServletRequest);
        String soapMessageStr = StreamUtils.copyToString(cachedHttpServletRequest.getInputStream(), Charset.forName(EUC_KR));
        String xml = ConvertorUtil.convertStringToXml(soapMessageStr);

        KtfSubmitReqMessage ktfSubmitReqMessage = new KtfSubmitReqMessage();
        SOAPMessage soapMessage = ktfSubmitReqMessage.fromXml(xml);

        String statusCode = "1000";
        String cpid = ktfSubmitReqMessage.getCpid();
        if (!tpsStorage.containsKey(cpid)) {
            statusCode = KtfProtocol.KTF_SUBMIT_ACK_CLIENT_ERROR_RESULT;
        } else {
            ClientTps clientTps = tpsStorage.get(cpid);
            if (clientTps.isTpsOver()) {
                statusCode = KtfProtocol.KTF_SUBMIT_ACK_HUB_OVER_INTRAFFIC_RESULT;
            } else {
                tpsStorage.put(clientTps.getId(), clientTps.count());
            }
        }

        String statusText = KtfUtil.getSubmitAckStatusCodeKor(statusCode);
        String dstMsgId = DateUtil.getDate() + TestUtil.getRandomNumberString(10);


        /* Render KtfResMessage */
        KtfResMessage ktfResMessage = new KtfResMessage(KtfProtocol.SUBMIT_RES);
        ktfResMessage.setTid(ktfSubmitReqMessage.getTid());
        ktfResMessage.setMessageId(dstMsgId);
        ktfResMessage.setStatusCode(statusCode);
        ktfResMessage.setStatusText(statusText);

        if (ktfResMessage.isSuccess()) {
            /* Render KtfDeliveryReportRequestMessage */
            KtfDeliveryReportReqMessage ktfDeliveryReportReqMessage = getKtfDeliveryReportReqMessage(ktfSubmitReqMessage, ktfResMessage);
            reportQueueStorage.add(ktfDeliveryReportReqMessage);

            log.info("[REPORT] Successfully add KtfDeliveryReportReqMessage[{}] to reportQueue", ktfDeliveryReportReqMessage);
        }

        /* render http header */
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"");
        headers.add(Constants.SOAP_ACTION, "\"\"");

        return new ResponseEntity<>(ktfResMessage.convertSOAPMessageToString(), headers, HttpStatus.OK);
    }




    public KtfDeliveryReportReqMessage getKtfDeliveryReportReqMessage(KtfSubmitReqMessage ktfSubmitReqMessage, KtfResMessage ktfResMessage) throws MCMPSoapRenderException {
        String statusCode = KtfUtil.getRandomStatusCodeByReport();
        String statusText = KtfUtil.getReportStatusCodeKor(statusCode);

        // TestUtil.getRandomStr()
        return KtfDeliveryReportReqMessage.builder()
                .tid(ktfResMessage.getTid())
                .messageId(ktfResMessage.getMessageId())
                .callback(ktfSubmitReqMessage.getCallback())
                .receiver(ktfSubmitReqMessage.getReceiver())
                .mmStatus(statusCode)
                .build();
    }
}