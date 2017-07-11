package org.treadmill.zk.plugin;

import com.google.common.cache.LoadingCache;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.treadmill.zk.plugin.utils.Configuration.get;

public class ZkListener {

    ZooKeeper zooKeeper;
    CountDownLatch connectedSignal = new CountDownLatch(1);

    public void connect(String hosts) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(
                hosts,
                5000000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            connectedSignal.countDown();
                        }
                    }
                }
        );
        connectedSignal.await();
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    private ZooKeeper getZooKeeper() {
        if (null == zooKeeper || !zooKeeper.getState().equals(ZooKeeper.States.CONNECTED)) {
            throw new IllegalStateException("ZooKeeper is not connected.");
        }
        return zooKeeper;
    }

    public static void listen(LoadingCache cache, String node) throws KeeperException, InterruptedException, IOException {
        ZkListener zkc = new ZkListener();
        zkc.connect(zkURL());
        ZooKeeper zk = zkc.getZooKeeper();
        String path = "/" + node;

        zk.getChildren(path, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    cache.invalidate(node);
                }
            }
        });
    }

    private static String zkURL() throws IOException {
        String url = get("treadmill_zk");
        return url.contains("@") ? url.split("@", 2)[1] : url;
    }
}