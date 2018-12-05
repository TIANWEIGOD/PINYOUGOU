import cn.itcast.demo.ProducerDemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-mq.xml")
public class ProducerTest {

    @Autowired
    private ProducerDemo producerDemo;

    @Test
    public void testSendMessage(){
        producerDemo.saveMessage();
        System.out.println("send_ok");
    }

}
