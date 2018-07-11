package com.ms.zookeeper.authz;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

class FileData {
    private long timestamp;
    private Set<String> users;

    long getTimestamp() {
        return timestamp;
    }
    void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    Set<String> getUsers() {
        return users;
    }
    void setUsers(final Set<String> users) {
        this.users = users;
    }
};

public class ZkFileAuthorizer implements ZkAuthorizer, Runnable {

    private static final Logger LOG = Logger.getLogger(ZkFileAuthorizer.class);

    private static volatile ZkFileAuthorizer instance = null;

    // expr and set of users who are allowed
    private final ConcurrentHashMap<String, FileData> cache = new ConcurrentHashMap<String, FileData>();
    // in minutes
    private final long authzFileCacheRefreshInterval;

    public ZkFileAuthorizer() {
        authzFileCacheRefreshInterval = Integer.parseInt(System.getProperty("authzFileCacheRefreshInterval", "600000"));

        new Thread(this, "auth file cache updater").start();
    }

    public static ZkFileAuthorizer getInstance() {
        if (instance == null) {
            instance = new ZkFileAuthorizer();
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            refreshCache();
            try {
                Thread.sleep(authzFileCacheRefreshInterval);
            } catch (InterruptedException e) {
                LOG.error("File authz updater thread interrupted", e);
            }
        }
    }

    public Map<String, FileData> getCache() {
        return cache;
    }

    public synchronized void refreshCache() {
        for (Map.Entry<String, FileData> entry : cache.entrySet()) {
            updateFile(entry.getKey(), entry.getValue());
        }
    }

    private FileData loadFile(final String file) {
        FileData data = new FileData();
        File f = new File(file);
        data.setTimestamp(f.lastModified());
        data.setUsers(loadUsers(f));
        cache.put(file, data);
        return data;
    }

    private Set<String> loadUsers(final File file) {
        Set<String> users = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        Scanner scanner = null;
        try {
            scanner = new Scanner(file, "US-ASCII");
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                users.add(line.trim());
            }
        } catch (Exception e) {
            // If failed we don't allow anyone
            LOG.warn("Exception in reading authz file " + file.getName(), e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return users;
    }

    private void updateFile(final String file, final FileData data) {
        File f = new File(file);
        long timestamp = f.lastModified();
        if (data.getTimestamp() != timestamp) {
            data.setTimestamp(timestamp);
            data.setUsers(loadUsers(f));
        }
    }

    public void clearCache() {
        cache.clear();
    }

    @Override
    public boolean authorize(final String user, final String expr) {
        String file = expr.split("://")[1];
        boolean result = authorizeImpl(user, file);
        if (result) {
            LOG.debug("Grant " + user + " using " + file);
        } else {
            LOG.debug("Deny " + user + " using " + file);
        }
        return result;
    }

    private boolean authorizeImpl(final String user, final String file) {
        // check cache
        FileData data = cache.get(file);
        if (data == null) {
            data = loadFile(file);
        }
        return data != null && data.getUsers() != null
                && (data.getUsers().contains(user) || data.getUsers().contains("*"));
    }
}
