package kr.co.seoultel.message.mt.mms.direct.controller.skt;


import kr.co.seoultel.message.mt.mms.direct.common.condition.KtfCondition;
import kr.co.seoultel.message.mt.mms.direct.common.condition.SktCondition;
import kr.co.seoultel.message.mt.mms.direct.common.util.RandomUtil;
import kr.co.seoultel.message.mt.mms.direct.common.util.Util;
import kr.co.seoultel.message.mt.mms.direct.controller.ReportSoapMessageWrapper;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt.SktDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.QueueStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol.EUC_KR;

@Slf4j
@RequiredArgsConstructor
public class SktReporter {

    protected RestTemplate restTemplate = new RestTemplate();

    private final QueueStorage<ReportSoapMessageWrapper<SktDeliveryReportReqMessage>> reportQueue;

    @Scheduled(initialDelay = 1000L, fixedRate = 500L)
    public void scheduleing() {
        while (!reportQueue.isEmpty()) {
            Util.sleep(RandomUtil.getRandomNumberInRange(8, 15));
            ReportSoapMessageWrapper<SktDeliveryReportReqMessage> wrapper = Objects.requireNonNull(reportQueue.poll());

            try {
                sendReport(wrapper);
            } catch (Exception e) {
                reportQueue.add(wrapper);
                log.error("[REPORT] Fail to send report[{}] to KTF Sender, do requeue", wrapper, e);
            }
        }
    }

    protected void sendReport(ReportSoapMessageWrapper<SktDeliveryReportReqMessage> wrapper) throws Exception {
        String reportUrl = wrapper.getReportUrl();
        SktDeliveryReportReqMessage sktDeliveryReportReqMessage = (SktDeliveryReportReqMessage) wrapper.getSoapMessage();
        String toString = sktDeliveryReportReqMessage.convertSOAPMessageToString();

        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.put(HttpHeaders.CONTENT_TYPE, List.of("text/xml; charset=\"euc-kr\""));
        header.put(HttpHeaders.ACCEPT_CHARSET, List.of(EUC_KR));
        header.put("SOAPAction", List.of(DirectProtocol.SOAP_ACTION));

        HttpEntity<String> httpEntity = new HttpEntity<>(toString, header);




        ResponseEntity<String> response = restTemplate.exchange(reportUrl, HttpMethod.POST, httpEntity, String.class);
        String body = response.getBody();
        log.info("[REPORT-ACK] Successfully received ReportAck[{}] from SKT Sender", body);
    }
}
