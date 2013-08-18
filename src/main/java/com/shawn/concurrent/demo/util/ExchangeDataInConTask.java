package com.shawn.concurrent.demo.util;

import java.util.List;
import java.util.concurrent.Exchanger;

import com.google.common.collect.Lists;

public class ExchangeDataInConTask {
    public static void main(String[] args) {
        List<String> buffer1 =Lists.newArrayList();
        List<String> buffer2 = Lists.newArrayList();

        Exchanger<List<String>> exchanger = new Exchanger<List<String>>();

        ExchangeProducer producer = new ExchangeProducer(buffer1,exchanger);
        ExchangeConsumer consumer = new ExchangeConsumer(buffer2, exchanger);

        Thread threadProducer = new Thread(producer);
        Thread threadConsumer = new Thread(consumer);

        threadConsumer.start();
        threadProducer.start();
    }
}

class ExchangeProducer implements Runnable{
    private List<String> buffer;

    private final Exchanger<List<String>> exchanger;

    public ExchangeProducer(List<String> buffer, Exchanger<List<String>> exhcanger){
        this.buffer = buffer;
        this.exchanger = exhcanger;
    }

    public void run() {
        int cycle = 1;
        for (int i = 0; i < 10; i++) {
            System.out.printf("Producer: cycle %d \n",cycle);
            for (int j = 0; j < 10; j++) {
                String message = "Event " + ((i*10) + j);
                System.out.printf("Producer: %s\n",message);
                buffer.add(message);
            }
            try {
                buffer = exchanger.exchange(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Producer: " + buffer.size());
            cycle++;
        }
    }
}

    class ExchangeConsumer implements Runnable{
        private List<String> buffer;

        private final Exchanger<List<String>> exchanger;

        public ExchangeConsumer(List<String> buffer, Exchanger<List<String>> exchanger){
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        public void run() {
            int cycle = 1;
            for (int i = 0; i < 10; i++) {
                System.out.printf("Consumer: Cycle %d \n",cycle);
                try {
                    buffer = exchanger.exchange(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Consumer: " + buffer.size());
                for (int j = 0; j < 10; j++) {
                    String message = buffer.get(0);
                    System.out.println("Consumer: " + message);
                    buffer.remove(0);
                }
                cycle++;
            }
        }
    }


