package kr.co.seoultel.message.mt.mms.direct.controller.ktf;

import kr.co.seoultel.message.mt.mms.direct.Application;
import kr.co.seoultel.message.mt.mms.direct.common.condition.KtfCondition;
import kr.co.seoultel.message.mt.mms.direct.common.exception.MMSException;
import kr.co.seoultel.message.mt.mms.direct.common.util.RandomUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.Util;
import kr.co.seoultel.message.mt.mms.direct.controller.ReportSoapMessageWrapper;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.QueueStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import static kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol.EUC_KR;

@Slf4j
@RequiredArgsConstructor
public class KtfReporter {

    protected RestTemplate restTemplate = new RestTemplate();

    protected final QueueStorage<ReportSoapMessageWrapper<KtfDeliveryReportReqMessage>> reportQueue;

    @Scheduled(initialDelay = 1000L, fixedRate = 500L)
    public void scheduleing() {
        while (!reportQueue.isEmpty()) {
            Util.sleep(RandomUtil.getRandomNumberInRange(8, 15));
            ReportSoapMessageWrapper<KtfDeliveryReportReqMessage> wrapper = Objects.requireNonNull(reportQueue.poll());

            try {
                sendReport(wrapper);
            } catch (Exception e) {
                reportQueue.add(wrapper);
                log.error("[REPORT] Fail to send report[{}] to KTF Sender, do requeue", wrapper, e);
            }
        }
    }

    protected void sendReport(ReportSoapMessageWrapper<KtfDeliveryReportReqMessage> wrapper) throws Exception {
        KtfDeliveryReportReqMessage ktfDeliveryReportReqMessage = (KtfDeliveryReportReqMessage) wrapper.getSoapMessage();

        String reportUrl = wrapper.getReportUrl();
        String soapMessageToString = ktfDeliveryReportReqMessage.convertSOAPMessageToString();

        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList("text/xml; charset=\"euc-kr\""));
        header.put(HttpHeaders.ACCEPT_CHARSET, Collections.singletonList(EUC_KR));
        header.put("SOAPAction", Collections.singletonList(DirectProtocol.SOAP_ACTION));
        HttpEntity<String> httpEntity = new HttpEntity<>(soapMessageToString, header);

        ResponseEntity<String> response = restTemplate.exchange(reportUrl, HttpMethod.POST, httpEntity, String.class);
        String body = response.getBody();
        log.info("[REPORT-ACK] Successfully received ReportAck[{}] from KTF Sender", body);
    }
}
