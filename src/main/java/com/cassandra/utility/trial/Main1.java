package com.cassandra.utility.trial;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.ResultSet;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by siddharth on 30/8/16.
 */
public class Main1 {
    public static void main(String... args) throws IOException, InterruptedException {
        if(args.length == 0){
            args=new String [4];
            args[0]="30.0.3.79";
            args[1]="cams_team";
            args[2]="cams0all";
            args[3]="/home/siddharth/Downloads/describering.txt";
        }
        Trials trials = new Trials(args[0],args[1],args[2],args[3]);
        Map<Host,Cluster> hostClusterMap = trials.getHostToCluster();
        LinkedBlockingQueue<ResultSet> queue = new LinkedBlockingQueue<>();
        int numberOfThreads = hostClusterMap.size();
        String tableIdentifier = "cams.product_filter_mapping";
        CountDownLatch latch = new CountDownLatch(hostClusterMap.size());
        for(Host host : hostClusterMap.keySet()){
            new Producer(hostClusterMap.get(host),trials.getHostToTokenRange().get(host.getAddress().toString().substring(1)),tableIdentifier,queue,latch);
        }
        queue.put(new ConsumerEnd());
        latch.await();

    }
}