package kr.co.seoultel.message.mt.mms.direct.modules.ktf;

import jakarta.xml.soap.SOAPException;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.common.exceptions.message.soap.MCMPSoapRenderException;
import kr.co.seoultel.message.mt.mms.core.dataVault.DataVault;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
import kr.co.seoultel.message.mt.mms.direct.Application;
import kr.co.seoultel.message.mt.mms.direct.config.KtfServerConfig;
import kr.co.seoultel.mms.server.core.config.ServerDataVaultConfig;
import kr.co.seoultel.mms.server.core.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static kr.co.seoultel.message.mt.mms.core.common.constant.Constants.EUC_KR;


@Slf4j
@Component
@Conditional(KtfCondition.class)
public class KtfReportProcessor extends Thread {

    protected final RestTemplate restTemplate = new RestTemplateBuilder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"")
            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, EUC_KR)
            .defaultHeader("SOAPAction", Constants.SOAP_ACTION)
            .build();

    private final KtfServerConfig ktfServerConfig;

    protected final HashMapStorage<String, MessageHistory> historyStorage;
    protected final QueueStorage<KtfDeliveryReportReqMessage> reportQueueStorage;

    protected KtfReportProcessor(KtfServerConfig ktfServerConfig, HashMapStorage<String, MessageHistory> historyStorage, QueueStorage<KtfDeliveryReportReqMessage> reportQueueStorage) {
        this.ktfServerConfig = ktfServerConfig;

        this.historyStorage = historyStorage;
        this.reportQueueStorage = reportQueueStorage;
    }


    @Override
    public void run() {
        while (Application.isStarted()) {
            if (reportQueueStorage.isEmpty()) {
                CommonUtil.doThreadSleep(250L);
            } else {
                KtfDeliveryReportReqMessage ktfDeliveryReportReqMessage = Objects.requireNonNull(reportQueueStorage.poll());
                CommonUtil.doThreadSleep(TestUtil.getRandomNumberInRange(15, 50));

                try {
                    sendReport(ktfDeliveryReportReqMessage);
                } catch (Exception e) {
                    reportQueueStorage.add(ktfDeliveryReportReqMessage);
                    log.error("[REPORT] Fail to send report[{}] to KTF Sender, do requeue", ktfDeliveryReportReqMessage, e);
                }
            }
        }
    }

    protected void sendReport(KtfDeliveryReportReqMessage ktfDeliveryReportReqMessage) throws MCMPSoapRenderException {
        String soapMessageToString = ktfDeliveryReportReqMessage.convertSOAPMessageToString();
        String url = "http://" + ktfServerConfig.getIp() + ":" + String.valueOf(ktfServerConfig.getPort());

        HttpEntity<String> httpEntity = new HttpEntity<>(soapMessageToString);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        String body = response.getBody();
        log.info("[REPORT-ACK] Successfully received ReportAck[{}] from KTF Sender", body);
    }
}
