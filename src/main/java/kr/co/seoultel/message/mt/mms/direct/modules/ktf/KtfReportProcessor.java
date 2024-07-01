package kr.co.seoultel.message.mt.mms.direct.modules.ktf;

import jakarta.xml.soap.SOAPException;
import kr.co.seoultel.message.mt.mms.core.common.constant.Constants;
import kr.co.seoultel.message.mt.mms.core.dataVault.DataVault;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.direct.modules.ktf.message.KtfSubmitMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
import kr.co.seoultel.message.mt.mms.direct.Application;
import kr.co.seoultel.message.mt.mms.direct.config.DirectServerConfig;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static kr.co.seoultel.message.mt.mms.core.common.constant.Constants.EUC_KR;


@Slf4j
@Component
@Conditional(KtfCondition.class)
@DependsOn({"serverDataVaultConfig", "directServerConfig"})
public class KtfReportProcessor extends Thread {

    protected final RestTemplate restTemplate = new RestTemplateBuilder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"")
            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, EUC_KR)
            .defaultHeader("SOAPAction", Constants.SOAP_ACTION)
            .build();

    private final DirectServerConfig directServerConfig;
    private final Set<MessageHistory> messageHistories;

    protected DataVault<KtfSubmitMessage> reportQueueDataVault;
    protected final ConcurrentLinkedQueue<KtfSubmitMessage> reportQueue;


    protected KtfReportProcessor(DirectServerConfig directServerConfig, ConcurrentLinkedQueue<KtfSubmitMessage> reportQueue, Set<MessageHistory> messageHistories) {
        this.directServerConfig = directServerConfig;
        this.messageHistories = messageHistories;
        this.reportQueue = reportQueue;
        this.reportQueueDataVault = new DataVault("report-data-vault", ServerDataVaultConfig.REPORT_FILE_PATH);
    }


    
    protected void sendReport(KtfSubmitMessage message) throws SOAPException, IOException {
        KtfDeliveryReportReqMessage ktfDeliveryReportReqMessage = KtfDeliveryReportReqMessage.builder()
                                                                                            .tid(message.getTid())
                                                                                            .messageId(message.getMessageId())
                                                                                            .receiver(message.getReceiver())
                                                                                            .callback(message.getCallback())
                                                                                            .timeStamp(DateUtil.getDate())
                                                                                            .mmStatus("1000")
                                                                                            .build();

        String soapMessageToString = ktfDeliveryReportReqMessage.convertSOAPMessageToString();
        String url = "http://" + directServerConfig.getIp() + ":" + String.valueOf(directServerConfig.getPort()) + "/";

        HttpEntity<String> httpEntity = new HttpEntity<>(soapMessageToString);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        String body = response.getBody();
    }

    protected void init() {
        Optional<List<KtfSubmitMessage>> optionalMessages = reportQueueDataVault.readAll(KtfSubmitMessage.class);
        optionalMessages.ifPresent(reportQueue::addAll);
    }

    @Override
    public void run() {
        while (Application.isStarted()) {
            KtfSubmitMessage message = reportQueue.poll();
            if (message != null) {
                CommonUtil.doThreadSleep(TestUtil.getRandomNumberInRange(1, 5));
                log.info("KtfSubmitReqMessage : {}", message);

                try {
                    sendReport(message);
                } catch (SOAPException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            CommonUtil.doThreadSleep(250L);
        }
    }


    @PreDestroy
    protected void destroy() {
        reportQueueDataVault.destroy(reportQueue);
    }
}
