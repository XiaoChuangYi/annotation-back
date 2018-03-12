package cn.malgo.annotation.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by cjl on 2018/3/9.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class LambdaTest {

    @Test
    @Ignore
    public void testStreamFlatMap(){
        System.out.println(">>>>>>>>>>>>>>>>>>>--------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<");
        Stream.of(
                Arrays.asList(1),
                Arrays.asList(2,3),
                Arrays.asList(4,5,6)
        ).flatMap(x->x.stream()).forEach(i->System.out.println("num："+i));
    }

    @FunctionalInterface
    public interface WorkerInterface{
        void doSomeWork();
    }
    public static void execute(WorkerInterface worker){
        worker.doSomeWork();
    }
    @Test
    public void testFunctionInterface(){
        execute(() -> System.out.println("what the fuck.interface can be new !!!!"));
    }
}
