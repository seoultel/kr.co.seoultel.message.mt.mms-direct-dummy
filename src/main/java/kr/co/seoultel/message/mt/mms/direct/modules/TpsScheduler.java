package kr.co.seoultel.message.mt.mms.direct.modules;

import kr.co.seoultel.message.mt.mms.core_module.storage.HashMapStorage;
import kr.co.seoultel.message.mt.mms.direct.ClientTps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TpsScheduler {
    private final HashMapStorage<String, ClientTps> tpsStorage;


    @Scheduled(fixedDelay = 1000L)
    public void clear() {
        LocalDateTime now = LocalDateTime.now();
        log.info("============+============+============+============+============+============+============+============+");
        tpsStorage.entrySet().forEach((entry) -> {
            String cpid = entry.getKey();
            ClientTps clientTps = entry.getValue();

            log.info("[TPS CLEAR] TIME[{}] cpid : {}, current : {} -> 0, limit : {}", now, cpid, clientTps.getCurrent(), clientTps.getLimit());
        });
        log.info("============+============+============+============+============+============+============+============+");


        tpsStorage.values().forEach((clientTps) -> {
            tpsStorage.put(clientTps.getId(), clientTps.clear());
        });


    }
}
