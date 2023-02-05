package com.will.java_riak;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.bucket.Bucket;

/**
 * Riak Java Client library quick start guide: https://github.com/basho/riak-java-client/tree/1.4.4#quick-start
 *
 */
public class quickstart 
{
    public static void main( String[] args ) throws RiakException
    {
        // create a client (see Configuration below in this README for more details)
        System.out.println("Creating client");
        IRiakClient riakClient = RiakFactory.pbcClient(); //or RiakFactory.httpClient();
            // Default values are "127.0.0.1", 8087

        // create a new bucket
        System.out.println("Creating bucket");
        Bucket myBucket = riakClient.createBucket("myBucket").execute();

        // add data to the bucket
        System.out.println("Adding data to bucket");
        myBucket.store("key1", "value1").execute();

        //fetch it back
        System.out.println("Fetching data");
        IRiakObject myData = myBucket.fetch("key1").execute();
        System.out.println("Data:");
        System.out.println(myData.getKey());

        // you can specify extra parameters to the store operation using the
        // fluent builder style API
        System.out.println("Storing again");
        myData = myBucket.store("key1", "value2").returnBody(true).execute();

        // delete
        System.out.println("Deleting");
        myBucket.delete("key1").rw(3).execute();

        return;
    }
}
