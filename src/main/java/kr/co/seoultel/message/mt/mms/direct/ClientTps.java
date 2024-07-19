package kr.co.seoultel.message.mt.mms.direct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class ClientTps {

    private String id;
    private int limit;
    private int current;

    public boolean isTpsOver() {
        return current > limit;
    }

    public ClientTps count() {
        this.current += 1;
        return this;
    }

    public ClientTps clear() {
        this.current = 0;
        return this;
    }

    public ClientTps(String id, int limit, int current) {
        this.id = id;
        this.limit = limit;
        this.current = current;
    }
}
