package model3;

import java.io.IOException;

import com.rabbitmq.client.AMQP;  
import com.rabbitmq.client.Channel;  
import com.rabbitmq.client.Connection;  
import com.rabbitmq.client.ConnectionFactory;  
import com.rabbitmq.client.Consumer;  
import com.rabbitmq.client.DefaultConsumer;  
import com.rabbitmq.client.Envelope;  
  
/**  
 * 发布订阅模式：类似于广播推送消息，收听了广播的都能收到消息
 * @author 消息接收者 - 消费者  
 */  
public class Rec {  
    /**  
     * 交换器名称  
     */  
    private static final String EXCHANGE_NAME = "logs";  
    /**  
     * 异步接收  
     * @throws Exception   
     */  
    public static void main(String[] args) throws Exception{  
        ConnectionFactory factory = new ConnectionFactory();  
        factory.setHost("192.168.80.130");  
        Connection connection = factory.newConnection();  
        final Channel channel = connection.createChannel();  
        //消费者也需要定义队列 有可能消费者先于生产者启动   
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout",true);  
        //产生一个随机的队列 该队列用于从交换器获取消息  
        String queueName = channel.queueDeclare().getQueue();  
        //将队列和某个交换机丙丁 就可以正式获取消息了 routingkey和交换器的一样都设置成空  
        channel.queueBind(queueName, EXCHANGE_NAME, "");  
          
        //定义回调抓取消息  
        Consumer consumer = new DefaultConsumer(channel) {  
            @Override  
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {  
                String message = new String(body, "UTF-8");  
                System.out.println(" [x] Received '" + message + "'");  
            }  
        };  
        //参数2 false表示手动确认  
        channel.basicConsume(queueName, true, consumer);  
          
    }  
}  
