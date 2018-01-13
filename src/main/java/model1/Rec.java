package model1;

import java.io.IOException;  

import com.rabbitmq.client.AMQP;  
import com.rabbitmq.client.Channel;  
import com.rabbitmq.client.Connection;  
import com.rabbitmq.client.ConnectionFactory;  
import com.rabbitmq.client.Consumer;  
import com.rabbitmq.client.DefaultConsumer;  
import com.rabbitmq.client.Envelope;  
  
/**
 * 简单模式：生产者推送到队列的消息只能由一个消费者获取
 * @author 消费者
 */
public class Rec {  
    
    // 获取消息队列名称  
    private final static String QUEUE_NAME = "hello";  
    /**  
     * 异步接收  
     * @throws Exception   
     */  
    public static void main(String[] args) throws Exception{  
        ConnectionFactory factory = new ConnectionFactory();  
        factory.setHost("192.168.80.130");  
        Connection connection = factory.newConnection();  
        Channel channel = connection.createChannel();  
        //消费者也需要定义队列 有可能消费者先于生产者启动   
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);  
        //定义回调抓取消息  
        Consumer consumer = new DefaultConsumer(channel) {  
            @Override  
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {  
                String message = new String(body, "UTF-8");  
                System.out.println(" [x] Received '" + message + "'");  
            }  
        };  
        channel.basicConsume(QUEUE_NAME, true, consumer);  
    }  
}