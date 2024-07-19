package kr.co.seoultel.message.mt.mms.direct;



import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfCondition;
import kr.co.seoultel.message.mt.mms.direct.modules.ktf.KtfReportProcessor;
import kr.co.seoultel.message.mt.mms.direct.modules.skt.SktCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Conditional(KtfCondition.class)
public class KtfApplicationStartUpTask implements ApplicationListener<ApplicationReadyEvent> {

    protected final KtfReportProcessor ktfReportProcessor;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Application.startApplicatiaon();

        ktfReportProcessor.start();
    }
}