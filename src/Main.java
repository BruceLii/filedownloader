import downloader.DownloadManager;
import downloader.singleTask.LogUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by liyonglin on 2017/4/18.
 */
public class Main {
    public static void main(String[] args) {
        start();
//        startUnfinished();
    }

    static void start() {
        DownloadManager manager = new DownloadManager("yiyaowang1", "C:\\Users\\li\\Desktop\\yiyaowang.csv");
        manager.sourceData = new ArrayList<>(30);
        try {
            manager.initURLs(manager.sourceData);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        dividThreads(MAX_WORK_THREAD);
        long begintime = (System.currentTimeMillis() / 1000);
        LogUtils.log("begin:", "time:" + (System.currentTimeMillis() / 1000));
//        linearTasks(sourceData);
        manager.dividThreads(DownloadManager.MAX_WORK_THREAD, manager.sourceData.subList(0, 1000));
//        dividThreads(MAX_WORK_THREAD, sourceData.subList(1000, sourceData.size()));
        downloader.singleTask.LogUtils.log("end:", "time:" + (System.currentTimeMillis() / 1000 - begintime));
    }

    static void startUnfinished() {
        new DownloadManager("yiyaowang1", "C:\\Users\\li\\Desktop\\yiyaowang.csv").startUnfinishedMissions();
    }
}
