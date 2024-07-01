package kr.co.seoultel.message.mt.mms.direct.modules.lgt;

import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Conditional(LgtCondition.class)
public class LgtController {

    @PostMapping("/")
    public void handleMM7SubmitReq() {

    }
}
