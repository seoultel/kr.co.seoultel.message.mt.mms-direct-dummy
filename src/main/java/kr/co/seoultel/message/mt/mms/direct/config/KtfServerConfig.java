package kr.co.seoultel.message.mt.mms.direct.config;

import com.google.gson.reflect.TypeToken;
import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
import kr.co.seoultel.message.mt.mms.core.messages.direct.ktf.KtfDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core.messages.direct.skt.SktDeliveryReportReqMessage;
import kr.co.seoultel.message.mt.mms.core_module.modules.report.MrReport;
import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.core_module.storage.QueueStorage;
import kr.co.seoultel.message.mt.mms.direct.ClientTps;
import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfCondition;
import kr.co.seoultel.message.mt.mms.direct.modules.lgt.LgtCondition;
import kr.co.seoultel.message.mt.mms.direct.tps.CpidInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@Slf4j
@Getter
@Setter
@Configuration
@Conditional(KtfCondition.class)
@ConfigurationProperties("direct")
public class KtfServerConfig {

    @Value("${direct.endpoint.ip}")
    private String ip;

    @Value("${direct.endpoint.port}")
    private int port;



    private List<CpidInfo> cpids;

    @Bean
    public HashMapStorage<String, ClientTps> tpsStorage(@Value("${direct.storage.tps.file-path}") String filePath) throws IOException {
        HashMapStorage<String, ClientTps> tpsStorage = new HashMapStorage<String, ClientTps>(filePath);

        Type type = new TypeToken<Collection<ClientTps>>(){}.getType();
        tpsStorage.createFileAnd().readFileAsCollection(type).ifPresent(collection ->
                collection.forEach(clientTps -> tpsStorage.put(clientTps.getId(), clientTps))
        );

        cpids.forEach((cpid) -> {
            tpsStorage.put(cpid.getCpid(), new ClientTps(cpid.getCpid(), cpid.getTps(), 0));
        });

        return (HashMapStorage<String, ClientTps>) tpsStorage.destroyBy(tpsStorage::values);
    }


    @Bean
    public HashMapStorage<String, MessageHistory> historyStorage(@Value("${direct.storage.history.file-path}") String filePath) throws IOException {
        HashMapStorage<String, MessageHistory> historyStorage = new HashMapStorage<String, MessageHistory>(filePath);

        Type type = new TypeToken<Collection<MessageHistory>>(){}.getType();
        historyStorage.createFileAnd().readFileAsCollection(type).ifPresent(collection ->
                collection.forEach(messageHistory -> historyStorage.put(messageHistory.getMessageId(), messageHistory))
        );

        return (HashMapStorage<String, MessageHistory>) historyStorage.destroyBy(historyStorage::values);
    }


    @Bean
    public QueueStorage<KtfDeliveryReportReqMessage> reportQueueStorage(@Value("${direct.storage.report.file-path}") String filePath) throws IOException {
        QueueStorage<KtfDeliveryReportReqMessage> queueStorage = new QueueStorage<KtfDeliveryReportReqMessage>(filePath);
        Type type = new TypeToken<Collection<KtfDeliveryReportReqMessage>>(){}.getType();
        queueStorage.createFileAnd().readFileAsCollection(type).ifPresent(queueStorage::addAll);

        return queueStorage;
    }
}
