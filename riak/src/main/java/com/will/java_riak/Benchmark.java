package com.will.java_riak;

import java.util.Iterator;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.bucket.Bucket;

public class Benchmark extends Thread {
    public static void main(String[] args) throws InterruptedException {
        // Insert 10'000 items concurrently
        for(int i=0; i<1000; i++){
            Benchmark t = new Benchmark();
            t.start();
            t.join();
        }

        // Get values after
        System.out.println("Reading");
        IRiakClient riakClient;
        try {
            riakClient = RiakFactory.httpClient("http://127.0.0.1:8098/riak");
            Bucket bucket = riakClient.fetchBucket("myBucket").execute();
            Iterator<String> keys_it = bucket.keys();
            while(keys_it.hasNext()){
                System.out.println(keys_it.next());
            }
        } catch (RiakException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Connect to database and insert data
        IRiakClient riakClient;
        try{
            System.out.println("Writing");
            riakClient = RiakFactory.pbcClient("127.0.0.1", 8087);
            Bucket bucket = riakClient.fetchBucket("myBucket").execute();
            String randomKey = Integer.toString((int)(Math.random()*10000));
            bucket.store(randomKey, randomKey).execute();
        } catch(Exception e) {
            System.out.println("Error connecting to Riak");
        }
    }
}
