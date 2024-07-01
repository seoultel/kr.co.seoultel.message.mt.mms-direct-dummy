//package kr.co.seoultel.message.mt.mms.direct.modules.skt;
//
//import io.netty.channel.Channel;
//import kr.co.seoultel.message.mt.mms.core.entity.MessageHistory;
//import kr.co.seoultel.message.mt.mms.core.messages.smtnt.delivery.SmtntDeliveryAckMessage;
//import kr.co.seoultel.message.mt.mms.core.messages.smtnt.report.SmtntReportMessage;
//import kr.co.seoultel.message.mt.mms.core.util.CommonUtil;
//import kr.co.seoultel.message.mt.mms.core.util.DateUtil;
//import kr.co.seoultel.message.mt.mms.direct.entity.SoapMessage;
//import kr.co.seoultel.mms.server.core.modules.server.DefaultServer;
//import kr.co.seoultel.mms.server.core.util.TestUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PreDestroy;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//import static kr.co.seoultel.message.mt.mms.core.messages.smtnt.SmtntProtocol.DELIVERY_RESULT_EXCEED_TPS;
//import static kr.co.seoultel.message.mt.mms.core.messages.smtnt.SmtntProtocol.MMS_MSG_TYPE;
//
//@Slf4j
//@Component
//public class SktReportProcessor extends kr.co.seoultel.mms.server.core.modules.SvrReportProcessor<SoapMessage> {
//    private final Set<MessageHistory> messageHistories;
//    protected SktReportProcessor(DefaultServer server, ConcurrentLinkedQueue<SmtntDeliveryAckMessage> reportQueue, Set<MessageHistory> messageHistories) {
//        super(server, reportQueue);
//        this.messageHistories = messageHistories;
//    }
//
//    @Override
//    protected void sendReport(Channel channel, SoapMessage soapMessage) {
//
//    }
//
//    @Override
//    protected void init() {
//        Optional<List<SmtntDeliveryAckMessage>> optionalMessages = reportQueueDataVault.readAll(.class);
//        optionalMessages.ifPresent(reportQueue::addAll);
//    }
//
//    @Override
//    public void run() {
//        while (Application.isStarted()) {
//            Channel rChannel = ((SmtntServer) server).getSmtntHandler().getRChannel();
//            while ((rChannel != null && rChannel.isOpen()) && !reportQueue.isEmpty()) {
//                SmtntDeliveryAckMessage message = (SmtntDeliveryAckMessage) reportQueue.remove();
//
//                CommonUtil.doThreadSleep(TestUtil.getRandomNumberInRange(1, 5));
//                sendReport(rChannel, message);
//            }
//
//            CommonUtil.doThreadSleep(250L);
//        }
//    }
//
//    @Override
//    protected void sendReport(Channel channel, SmtntDeliveryAckMessage message) {
//        int result = ReportResult.getReportResult();
//
//        String umsMsgId = message.getUserMsgId();
//        String srcMsgId = message.getUserMsgSubId();
//        String phoneNo = message.getPhoneNo();
//        String userData = message.getUserData();
//        String telecom = TestUtil.getRandomStr("SKT", "KTF", "LGT");
//
//        // result = DELIVERY_RESULT_EXCEED_TPS;
//        if (result == DELIVERY_RESULT_EXCEED_TPS) messageHistories.removeIf(mh -> mh.getUmsMsgId().equals(umsMsgId));
//
//        SmtntReportMessage smtntReportMessage = SmtntReportMessage.builder()
//                                                                    .userMsgId(umsMsgId)
//                                                                    .userMsgSubId(srcMsgId)
//                                                                    .serverMsgId(String.valueOf(TestUtil.getRandomNumberInRange(1000000000L, 20000000000000L)))
//                                                                    .msgType(MMS_MSG_TYPE)
//                                                                    .phoneNo(phoneNo)
//                                                                    .result(result)
//                                                                    .telecom(telecom)
//                                                                    .deliveryTime(DateUtil.getDate(0))
//                                                                    .userData(userData)
//                                                                    .build();
//
//        try {
//            channel.writeAndFlush(smtntReportMessage).addListener(future -> {
//                if (future.isSuccess()) {
//                    log.info("[REPORT] successfully sended report of message[umsMsgId : {}] to sender", umsMsgId);
//                } else {
//                    reportQueue.add(message);
//                    log.info("[REPORT] failed to send report of message[umsMsgId : {}] to sender", umsMsgId);
//                }
//            });
//        } catch (Exception e) {
//            reportQueue.add(message);
//        }
//    }
//
//    @Override
//    @PreDestroy
//    protected void destroy() {
//        reportQueueDataVault.destroy(reportQueue);
//    }
//}
