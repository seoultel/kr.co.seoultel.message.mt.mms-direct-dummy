package kr.co.seoultel.message.mt.mms.direct.modules.skt;

import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfCondition;
import kr.co.seoultel.message.mt.mms.direct.modules.lgt.LgtCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Conditional(LgtCondition.class)
public class SktController {

    @PostMapping("/")
    public void handleMM7SubmitReq() {

    }
}
