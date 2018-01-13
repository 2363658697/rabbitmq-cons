package model5;

import java.io.IOException;

import com.rabbitmq.client.AMQP;  
import com.rabbitmq.client.Channel;  
import com.rabbitmq.client.Connection;  
import com.rabbitmq.client.ConnectionFactory;  
import com.rabbitmq.client.Consumer;  
import com.rabbitmq.client.DefaultConsumer;  
import com.rabbitmq.client.Envelope;  
  

public class Rec {  
    /**  
     * 交换器名称  
     */  
    private static final String EXCHANGE_NAME = "student";  
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
        channel.exchangeDeclare(EXCHANGE_NAME, "topic",true);  
        //产生一个随机的队列 该队列用于从交换器获取消息  
        String queueName = channel.queueDeclare().getQueue();  
        //routingkey: #代表多层路径一直到最后一层，*代表一层
        // a.b, a.c.d      a.#:获取以a.开头的所有的数据,忽略其中的层次   
        //a.c ,a.b   a.*：获取以a.开头的下一层的所有数据
        channel.queueBind(queueName, EXCHANGE_NAME, "1610.*.boy");  
          
        //定义回调抓取消息  
        Consumer consumer = new DefaultConsumer(channel) {  
            @Override  
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,  
                    byte[] body) throws IOException {  
                String message = new String(body, "UTF-8");  
                String routingKey=envelope.getRoutingKey();  
                System.out.println(" [x] Received '" + message + "'"+"---"+routingKey);  
            }  
        };  
        //参数2 false表示手动确认  
        channel.basicConsume(queueName, true, consumer);  
          
    }  
}  
