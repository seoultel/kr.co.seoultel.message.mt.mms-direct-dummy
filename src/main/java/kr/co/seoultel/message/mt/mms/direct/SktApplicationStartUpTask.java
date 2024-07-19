package kr.co.seoultel.message.mt.mms.direct;



import kr.co.seoultel.message.mt.mms.direct.modules.skt.SktCondition;
import kr.co.seoultel.message.mt.mms.direct.modules.skt.SktReportProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Conditional(SktCondition.class)
public class SktApplicationStartUpTask implements ApplicationListener<ApplicationReadyEvent> {

    protected final SktReportProcessor sktReportProcessor;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Application.startApplicatiaon();

        sktReportProcessor.start();
    }
}