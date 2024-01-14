/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.newlandframework.rpc.controller;

import com.newlandframework.rpc.services.AddCalculate;
import com.newlandframework.rpc.services.MultiCalculate;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:RpcParallelTest.java
 * @description:RpcParallelTest功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
@Controller
public class RpcParallelController {

    @Resource
    private ApplicationContext applicationContext;

    @GetMapping("/v1/test")
    public void test() throws InterruptedException {
        //并行度1000
        int parallel = 1000;
        for (int i = 0; i < 1; i++) {
            this.addTask((AddCalculate) applicationContext.getBean("addCalc"), parallel);
            this.multiTask((MultiCalculate) applicationContext.getBean("multiCalc"), parallel);
            System.out.printf("[author tangjie] Netty RPC Server 消息协议序列化第[%d]轮并发验证结束!\n\n", i);
        }
    }


    public void parallelAddCalcTask(AddCalculate calc, int parallel) throws InterruptedException {
        //开始计时
        StopWatch sw = new StopWatch();
        sw.start();
        CountDownLatch signal = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(parallel);

        for (int index = 0; index < parallel; index++) {
            AddCalcParallelRequestThread client = new AddCalcParallelRequestThread(calc, signal, finish, index);
            new Thread(client).start();
        }
        signal.countDown();
        finish.await();
        sw.stop();
        String tip = String.format("加法计算RPC调用总共耗时: [%s] 毫秒", sw.getTime());
        System.out.println(tip);
    }

    public void parallelMultiCalcTask(MultiCalculate calc, int parallel) throws InterruptedException {
        //开始计时
        StopWatch sw = new StopWatch();
        sw.start();
        CountDownLatch signal = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(parallel);
        for (int index = 0; index < parallel; index++) {
            MultiCalcParallelRequestThread client = new MultiCalcParallelRequestThread(calc, signal, finish, index);
            new Thread(client).start();
        }
        signal.countDown();
        finish.await();
        sw.stop();
        String tip = String.format("乘法计算RPC调用总共耗时: [%s] 毫秒", sw.getTime());
        System.out.println(tip);
    }

    public void addTask(AddCalculate calc, int parallel) throws InterruptedException {
        this.parallelAddCalcTask(calc, parallel);
        TimeUnit.MILLISECONDS.sleep(30);
    }

    public void multiTask(MultiCalculate calc, int parallel) throws InterruptedException {
        this.parallelMultiCalcTask(calc, parallel);
        TimeUnit.MILLISECONDS.sleep(30);
    }
}
