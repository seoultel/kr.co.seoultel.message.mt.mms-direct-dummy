package kr.co.seoultel.message.mt.mms.direct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class Application {

    private static boolean isStop = true;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new ApplicationPidFileWriter("application.pid"));
        application.run(args);
    }

    public static void startApplicatiaon() {
        isStop = false;
    }

    public static void stopApplicatiaon() {
        isStop = true;
    }

    public static boolean isStarted() {
        return !isStop;
    }


    @Bean
    public Set<String> submitHistory() {
        return new HashSet<String>();
    }

}
