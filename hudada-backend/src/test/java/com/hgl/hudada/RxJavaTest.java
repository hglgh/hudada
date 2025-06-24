package com.hgl.hudada;


import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RxJavaTest
 * @Author 请别把我整破防
 * @Description //TODO
 * @Date 2025/6/20 17:17
 */
@SpringBootTest
public class RxJavaTest {

    @Test
    public void test() {
        //创建数据流
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(item -> ++item)
                //订阅指定执行线程池
                .subscribeOn(Schedulers.io());

        //订阅 flowable 流，打印每个接受到的数字
        flowable
                .observeOn(Schedulers.io())
                .doOnNext(System.out::println).subscribe();

        //阻塞当前线程，保证 main 函数不退出
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
