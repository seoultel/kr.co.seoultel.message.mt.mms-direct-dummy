package kr.co.seoultel.message.mt.mms.direct.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Setter
@Getter
public class CpidInfo {

    private String cpid;
    private int maxTps;
    private int current = 0;
    private String reportUrl;

    public boolean isTpsOver() {
        current += 1;
        return current >= maxTps;
    }

    @Override
    public String toString() {
        return "CpidInfo{" +
                "cpid='" + cpid + '\'' +
                ", maxTps=" + maxTps +
                ", reportUrl='" + reportUrl + '\'' +
                '}';
    }
}
