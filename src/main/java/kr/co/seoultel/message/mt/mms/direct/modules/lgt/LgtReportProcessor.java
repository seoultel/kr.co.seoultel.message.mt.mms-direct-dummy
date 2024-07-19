package kr.co.seoultel.message.mt.mms.direct.modules.lgt;

import jakarta.xml.soap.SOAPException;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.common.exceptions.message.soap.MCMPSoapRenderException;
import kr.co.seoultel.message.mt.mms.core.dataVault.DataVault;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.lgt.LgtDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
import kr.co.seoultel.message.mt.mms.direct.Application;
import kr.co.seoultel.message.mt.mms.direct.config.LgtServerConfig;
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
@Conditional(LgtCondition.class)
public class LgtReportProcessor extends Thread {

    protected final RestTemplate restTemplate = new RestTemplateBuilder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"")
            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, EUC_KR)
            .defaultHeader("SOAPAction", Constants.SOAP_ACTION)
            .build();

    private final LgtServerConfig lgtServerConfig;


    protected final HashMapStorage<String, MessageHistory> historyStorage;
    protected final QueueStorage<LgtDeliveryReportReqMessage> reportQueueStorage;


    protected LgtReportProcessor(LgtServerConfig lgtServerConfig, HashMapStorage<String, MessageHistory> historyStorage, QueueStorage<LgtDeliveryReportReqMessage> reportQueueStorage) {
        this.lgtServerConfig = lgtServerConfig;

        this.historyStorage = historyStorage;
        this.reportQueueStorage = reportQueueStorage;
    }



    
    protected void sendReport(LgtDeliveryReportReqMessage lgtDeliveryReportReqMessage) throws MCMPSoapRenderException {
        String soapMessageToString = lgtDeliveryReportReqMessage.convertSOAPMessageToString();
        String url = "http://" + lgtServerConfig.getIp() + ":" + String.valueOf(lgtServerConfig.getPort());

        HttpEntity<String> httpEntity = new HttpEntity<>(soapMessageToString);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        String body = response.getBody();
        log.info("[REPORT-ACK] Successfully received ReportAck[{}] from LGT Sender", body);
    }



    @Override
    public void run() {
        while (Application.isStarted()) {
            if (reportQueueStorage.isEmpty()) {
                CommonUtil.doThreadSleep(250L);
            } else {
                LgtDeliveryReportReqMessage lgtDeliveryReportReqMessage = Objects.requireNonNull(reportQueueStorage.remove());
                CommonUtil.doThreadSleep(TestUtil.getRandomNumberInRange(15, 50));

                try {
                    sendReport(lgtDeliveryReportReqMessage);
                } catch (Exception e) {
                    reportQueueStorage.add(lgtDeliveryReportReqMessage);
                    log.error("[REPORT] Fail to send report[{}] to LGT Sender, do requeue", lgtDeliveryReportReqMessage, e);
                }
            }
        }
    }
}
