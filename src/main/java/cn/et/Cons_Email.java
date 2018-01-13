package cn.et;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

@RestController
public class Cons_Email {

    @Autowired
    private JavaMailSender jms;

    /**
     * 获取消息队列名称
     */
    private String QUEUE_NAME = "EMAIL_QUEUE";

    /**
     * 异步接收
     * 
     * @throws Exception
     */
    @RequestMapping("/send")
    public void asyncRec() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.80.130");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 消费者也需要定义队列 有可能消费者先于生产者启动
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // System.out.println(" [*] Waiting for messages. To exit press
        // CTRL+C");
        // 定义回调抓取消息
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                    byte[] body) throws IOException {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, String> map = (Map<String, String>) Utils.ByteToObject(body);
                    SimpleMailMessage mailMessage  =   new  SimpleMailMessage();
                    mailMessage.setFrom("2363658697@qq.com");
                    mailMessage.setTo(map.get("setTo").toString());
                    mailMessage.setSubject(map.get("setSubject").toString());
                    mailMessage.setText(map.get("setText").toString());
                    jms.send(mailMessage);
                } catch (Exception e) {
                    System.out.println("发送失败");
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
