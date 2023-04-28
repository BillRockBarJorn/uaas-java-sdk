package com.heredata.comm;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.heredata.utils.LogUtils.getLog;

/**
 * <p>Title: IdleConnectionReaper</p>
 * <p>Description: 用于定期检查连接池空闲状态的守护进程线程连接 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:52
 */
public final class IdleConnectionReaper extends Thread {
    private static final int REAP_INTERVAL_MILLISECONDS = 5 * 1000;
    private static final ArrayList<HttpClientConnectionManager> connectionManagers = new ArrayList<HttpClientConnectionManager>();

    private static IdleConnectionReaper instance;

    private static long idleConnectionTime = 60 * 1000;

    private volatile boolean shuttingDown;

    private IdleConnectionReaper() {
        super("idle_connection_reaper");
        setDaemon(true);
    }

    public static synchronized boolean registerConnectionManager(HttpClientConnectionManager connectionManager) {
        if (instance == null) {
            instance = new IdleConnectionReaper();
            instance.start();
        }
        return connectionManagers.add(connectionManager);
    }

    public static synchronized boolean removeConnectionManager(HttpClientConnectionManager connectionManager) {
        boolean b = connectionManagers.remove(connectionManager);
        if (connectionManagers.isEmpty()) {
            shutdown();
        }
        return b;
    }

    private void markShuttingDown() {
        shuttingDown = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        while (true) {
            if (shuttingDown) {
                getLog().debug("Shutting down reaper thread.");
                return;
            }

            try {
                Thread.sleep(REAP_INTERVAL_MILLISECONDS);
            } catch (InterruptedException e) {
            }

            try {
                List<HttpClientConnectionManager> connectionManagers = null;
                synchronized (IdleConnectionReaper.class) {
                    connectionManagers = (List<HttpClientConnectionManager>) IdleConnectionReaper.connectionManagers
                            .clone();
                }
                for (HttpClientConnectionManager connectionManager : connectionManagers) {
                    try {
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(idleConnectionTime, TimeUnit.MILLISECONDS);
                    } catch (Exception ex) {
                        getLog().warn("Unable to close idle connections", ex);
                    }
                }
            } catch (Throwable t) {
                getLog().debug("Reaper thread: ", t);
            }
        }
    }

    public static synchronized boolean shutdown() {
        if (instance != null) {
            instance.markShuttingDown();
            instance.interrupt();
            connectionManagers.clear();
            instance = null;
            return true;
        }
        return false;
    }

    public static synchronized int size() {
        return connectionManagers.size();
    }

    public static synchronized void setIdleConnectionTime(long idletime) {
        idleConnectionTime = idletime;
    }

}
