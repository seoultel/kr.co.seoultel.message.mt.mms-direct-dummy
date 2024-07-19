package kr.co.seoultel.message.mt.mms.direct.modules.skt;

import jakarta.xml.soap.SOAPException;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.common.exceptions.message.soap.MCMPSoapRenderException;
import kr.co.seoultel.message.mt.mms.core.dataVault.DataVault;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
import kr.co.seoultel.message.mt.mms.direct.Application;
import kr.co.seoultel.message.mt.mms.direct.config.SktServerConfig;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static kr.co.seoultel.message.mt.mms.core.common.constant.Constants.EUC_KR;


@Slf4j
@Component
@Conditional(SktCondition.class)
public class SktReportProcessor extends Thread {

    protected final RestTemplate restTemplate = new RestTemplateBuilder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"")
            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, EUC_KR)
            .defaultHeader("SOAPAction", Constants.SOAP_ACTION)
            .build();

    private final SktServerConfig sktServerConfig;
    protected final HashMapStorage<String, MessageHistory> historyStorage;
    protected final QueueStorage<SktDeliveryReportReqMessage> reportQueueStorage;


    protected SktReportProcessor(SktServerConfig sktServerConfig, HashMapStorage<String, MessageHistory> historyStorage, QueueStorage<SktDeliveryReportReqMessage> reportQueueStorage) {
        this.sktServerConfig = sktServerConfig;

        this.historyStorage = historyStorage;
        this.reportQueueStorage = reportQueueStorage;
    }

    @Override
    public void run() {
        while (Application.isStarted()) {
            if (reportQueueStorage.isEmpty()) {
                CommonUtil.doThreadSleep(250L);
            } else {
                SktDeliveryReportReqMessage sktDeliveryReportReqMessage = Objects.requireNonNull(reportQueueStorage.remove());
                CommonUtil.doThreadSleep(TestUtil.getRandomNumberInRange(15, 50));

                try {
                    sendReport(sktDeliveryReportReqMessage);
                } catch (Exception e) {
                    reportQueueStorage.add(sktDeliveryReportReqMessage);
                    log.error("[REPORT] Fail to send report[{}] to SKT Sender, do requeue", sktDeliveryReportReqMessage, e);
                }
            }
        }
    }

    protected void sendReport(SktDeliveryReportReqMessage sktDeliveryReportReqMessage) throws MCMPSoapRenderException {
        String toString = sktDeliveryReportReqMessage.convertSOAPMessageToString();
        String url = "http://" + sktServerConfig.getIp() + ":" + String.valueOf(sktServerConfig.getPort());

        HttpEntity<String> httpEntity = new HttpEntity<>(toString);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        String body = response.getBody();
        log.info("[REPORT-ACK] Successfully received ReportAck[{}] from SKT Sender", body);
    }
}
