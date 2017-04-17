import download.DownloadManager;
import download.Mission;
import interfaces.DownloadBrowserInterface;
import interfaces.DownloadInterface;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import singleTask.DownloadMission;
import singleTask.LogUtils;
import utils.TextUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Main implements DownloadBrowserInterface {
    Logger logger = LoggerFactory.getLogger("tag");

    public static final int MAX_WORK_THREAD = Runtime.getRuntime().availableProcessors();
    static List<DownloadItem> sourceData;
//    public static final int MAX_WORK_THREAD = 10;

    static String filepath = "C:\\Users\\li\\Desktop\\Temp";


    static Main downloaderTest = new Main();

    public static void main(String[] args) {
        sourceData = new ArrayList<>(30);
        try {
            initURLs(sourceData);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        dividThreads(MAX_WORK_THREAD);
        linearTasks(sourceData);

//        multiTasks(sourceData);
    }

    /**
     * max thread
     *
     * @param threadNum
     */
    private static void dividThreads(int threadNum) {
        LogUtils.log("mission start", "maxthread" + threadNum);

        int taskSize = sourceData.size() / threadNum;

        if (sourceData.size() % threadNum != 0) {
            threadNum++;
        }

        for (int i = 0; i < threadNum; i++) {

            List<DownloadItem> pieace = sourceData.subList(taskSize * i, taskSize * (i + 1) - 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    linearTasks(pieace);
                }
            }).start();
        }

    }


    private static void linearTasks(List<DownloadItem> sourceData) {
        for (int i = 0; i < sourceData.size(); i++) {
            if (i >= sourceData.size() - 1) return;

            DownloadItem item = sourceData.get(i);

            singleTask.Mission mission = new singleTask.Mission();
            mission.url = item.img_url;
            mission.savedFilename = item.saved_file_name;
            mission.savedFile_path = filepath;

            try {
                DownloadMission.startMission(mission);
                System.out.println("mission: " + i + "  name" + mission.savedFilename);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("errorMission: " + i + "  name" + mission.savedFilename + "  rul-->" + mission.url);
            }

            if (i == 99) {
                break;
            }

        }

    }

    private static void multiTasks(List<DownloadItem> sourceData) {

        DownloadInterface downloadInterface = DownloadManager.getInstance();
        for (int i = 0; i < sourceData.size(); i++) {
            DownloadItem item = sourceData.get(i);

            downloadInterface.registerBrowser(item.img_url, downloaderTest);

            Mission mission = downloadInterface.addDownloadMission(item.img_url, item.img_url, filepath, item.saved_file_name);

//            if (i == 10) {
//                break;
//            }
        }

    }


    private static void initURLs(List<DownloadItem> sourceData) throws IOException {
        if (sourceData == null) return;
        sourceData.clear();

        Reader in = new FileReader("C:/Users/li/Desktop/kad.csv");
//        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader("img", "name", "approval").parse(in);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withSkipHeaderRecord(true).parse(in);

        int count = 0;
        for (CSVRecord record : records) {
            count++;
            if (count == 1) continue;


            String img_url = record.get(7);//url
            String name = record.get(8);
            String approval = record.get(0);

            String nameSuffix = img_url.substring(img_url.lastIndexOf("/") + 1);

            String saved_file_name = name.replaceAll("/", "") + "_" + approval.replaceAll("/", "") + nameSuffix;

            if (!TextUtils.isEmpty(img_url)) {
                sourceData.add(new DownloadItem(img_url, saved_file_name));
            }

        }


    }


    @Override
    public void onMissionChange(Mission mission) {
        logger.debug(mission.toJsonFormatString());
    }
}
