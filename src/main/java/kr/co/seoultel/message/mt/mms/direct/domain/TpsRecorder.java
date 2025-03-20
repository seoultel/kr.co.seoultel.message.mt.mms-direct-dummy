package kr.co.seoultel.message.mt.mms.direct.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TpsRecorder {

    private final Map<String, CpidInfo> cpidInfoMap;

    @Scheduled(fixedDelay = 1000L)
    public void clear() {
        cpidInfoMap.forEach((cpid, cpidInfo) -> {
            cpidInfo.setCurrent(0);
        });
    }
}
