package com.will.java_riak;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.http.RiakClient;

public class cluster_read_write {
    public static void main(String[] args) throws RiakException {
        // Client to the coordinator node
        IRiakClient coordinator_client = RiakFactory.httpClient("http://127.17.0.2:65368/riak");

        // Client to a member node
        IRiakClient member2_client = RiakFactory.httpClient("http://127.17.0.6:65388/riak");

        // Create a bucket
        Bucket bucket = coordinator_client.createBucket("distributedbucket").execute();

        // Add data to the bucket
        bucket.store("key1", "value1").execute();

        // Fetch it back
        IRiakObject myData = bucket.fetch("key1").execute();
        System.out.println(String.format("%s: %s", myData.getKey(), myData.getValueAsString()));

        // Read it from a different node
        Bucket member_bucket = member2_client.fetchBucket("distributedbucket").execute();
        IRiakObject data2 = member_bucket.fetch("key1").execute();
        System.out.println(String.format("%s: %s", data2.getKey(), data2.getValueAsString()));
    }
}
