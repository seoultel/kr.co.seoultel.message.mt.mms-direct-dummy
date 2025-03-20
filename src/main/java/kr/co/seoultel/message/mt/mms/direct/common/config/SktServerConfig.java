package kr.co.seoultel.message.mt.mms.direct.common.config;

import com.google.gson.reflect.TypeToken;
import kr.co.seoultel.message.mt.mms.direct.common.condition.SktCondition;
import kr.co.seoultel.message.mt.mms.direct.controller.ReportSoapMessageWrapper;
import kr.co.seoultel.message.mt.mms.direct.controller.lgt.LgtReporter;
import kr.co.seoultel.message.mt.mms.direct.controller.skt.SktReporter;
import kr.co.seoultel.message.mt.mms.direct.domain.CpidInfo;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.lgt.LgtDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.message.direct.skt.SktDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol;
import kr.co.seoultel.message.mt.mms.direct.infrastructure.cache.storage.QueueStorage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.co.seoultel.message.mt.mms.direct.domain.protocol.DirectProtocol.EUC_KR;

@Slf4j
@Getter
@Setter
@Configuration
@Conditional(SktCondition.class)
public class SktServerConfig {

//    @Bean
//    public RestTemplate restTemplate(
//        RestTemplateBuilder builder
//    ) {
//        return builder
//            .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"euc-kr\"")
//            .defaultHeader(HttpHeaders.ACCEPT_CHARSET, EUC_KR)
//            .defaultHeader("SOAPAction", DirectProtocol.SOAP_ACTION)
//            .build();
//    }

    @Bean
    public QueueStorage<ReportSoapMessageWrapper<SktDeliveryReportReqMessage>> reportQueue(
            @Value("${storage.report.file-path}") String filePath
    ) throws Exception {
        QueueStorage<ReportSoapMessageWrapper<SktDeliveryReportReqMessage>> queueStorage = new QueueStorage<ReportSoapMessageWrapper<SktDeliveryReportReqMessage>>(filePath);
        Type type = new TypeToken<Collection<ReportSoapMessageWrapper<SktDeliveryReportReqMessage>>>(){}.getType();
        queueStorage.createFileAnd().readFileAsCollection(type).ifPresent(queueStorage::addAll);

        return queueStorage;
    }

    @Bean
    public SktReporter sktReporter(
        QueueStorage<ReportSoapMessageWrapper<SktDeliveryReportReqMessage>> reportQueue
    ) {
        return new SktReporter(reportQueue);
    }

}
