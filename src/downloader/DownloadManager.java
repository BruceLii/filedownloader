package downloader;

import downloader.model.DownloadConst;
import downloader.model.DownloadItem;
import downloader.singleTask.DownloadMission;
import downloader.singleTask.LogUtils;
import downloader.singleTask.Mission;
import downloader.singleTask.util.DownloadHelper;
import downloader.utils.TextUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class DownloadManager {
    private static volatile int missionCount = 0;
    public static final int MAX_WORK_THREAD = Runtime.getRuntime().availableProcessors();
//    public static final int MAX_WORK_THREAD = 1;

    public List<DownloadItem> sourceData;


    String fileFoldername = "Temp";
    String cvs_resource_Path = "";
    String filepath;

    public DownloadManager(String fileFoldername, String cvs_resource_Path) {
        this.fileFoldername = fileFoldername;
        this.cvs_resource_Path = cvs_resource_Path;

        filepath = DownloadConst.ROOT_FOLDER + File.separator + fileFoldername;
    }


    /**
     * 设置下载文件名，，所有文件保存的父文件夹为 D:\Temp\
     *
     * @param fileFoldername
     */
    public void setFileFoldername(String fileFoldername) {
        this.fileFoldername = fileFoldername;
    }

    int approvalIndex, nameIndex, imgUrlIndex;

    /**
     * max thread
     *
     * @param threadNum
     */
    public void dividThreads(int threadNum, List<DownloadItem> datas) {
        LogUtils.log("mission start", "maxthread" + threadNum);

        int taskSize = datas.size() / threadNum;

        if (datas.size() % threadNum != 0) {
            threadNum++;
        }

        for (int i = 0; i < threadNum; i++) {
            int fromIndex, toIndex;
            fromIndex = taskSize * i;

            if (taskSize * (i + 1) > datas.size()) {
                toIndex = fromIndex + datas.size() % taskSize;
            } else {
                toIndex = taskSize * (i + 1);
            }

            List<DownloadItem> pieace = datas.subList(fromIndex, toIndex);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    linearTasks(pieace);
                }
            }).start();
        }

    }


    private void linearTasks(List<DownloadItem> sourceData) {
        for (int i = 0; i < sourceData.size(); i++) {

            DownloadItem item = sourceData.get(i);

            Mission mission = new downloader.singleTask.Mission();
            mission.url = item.img_url;
            mission.savedFilename = item.saved_file_name;
            mission.savedFile_path = filepath;

            try {
//                if (i >=0&& i<=10) {
//                    throw new IndexOutOfBoundsException("测试错误" + i + "保存失败文件");
//                }
                DownloadMission.startMission(mission);
                System.out.println(Thread.currentThread().getName() + " mission: " + (missionCount++) + "  name" + mission.savedFilename);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + "errorMission: " + (missionCount++) + "  name" + mission.savedFilename + "  rul-->" + mission.url);

                //TODO   保存下载失败的文件.
                DownloadHelper.saveConfig(mission);
            }
        }
    }

    public void initURLs(List<DownloadItem> sourceData) throws IOException {
        if (sourceData == null) return;
        sourceData.clear();

        Reader in = new FileReader(cvs_resource_Path);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withSkipHeaderRecord(true).parse(in);

        int count = 0;
        for (CSVRecord record : records) {
            count++;
            if (count == 1) {//init header.
                initIndex(record);
                continue;
            }


            String img_url = record.get(imgUrlIndex);//url


            String name = record.get(nameIndex);
            String approval = record.get(approvalIndex);

            String legal_name;
            if (img_url.contains("?")) {
                legal_name = img_url.substring(0, img_url.lastIndexOf("?"));
            } else {
                legal_name = img_url;
            }
            String nameSuffix = TextUtils.getFileName(legal_name);


            String saved_file_name = TextUtils.getFileName(approval) + "_" + TextUtils.getFileName(name) + "_random(" + TextUtils.getRandomNum() + ")_" + "_" + nameSuffix;

            if (!TextUtils.isEmpty(img_url)) {
                //针对医药网的连接格式问题，  http://p3.maiyaole.com/img/971/971862/330_330.jpg?a=1479859986  去掉  jpg 后的 ?a=1479859986

                sourceData.add(new DownloadItem(img_url, saved_file_name));
            }

        }


    }

    //    找到指定的index
    private void initIndex(CSVRecord record) {
        for (int i = 0; i < record.size(); i++) {
            String colomnName = record.get(i);
            if (colomnName != null && colomnName.equals("name")) {
                nameIndex = i;
            }
            if (colomnName != null && colomnName.equals("img")) {
                imgUrlIndex = i;
            }
            if (colomnName != null && colomnName.equals("approval")) {
                approvalIndex = i;
            }
        }
    }

    public void startUnfinishedMissions() {
        //每个任务执行完成之后，检测其保存的失败文件，并重新加入下载，，
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<downloader.singleTask.Mission> unfinishedTask = DownloadHelper.readCfgFromDir(DownloadManager.this.filepath + File.separator + DownloadConst.DOWNLOAD_LOCAL_relative_DIR_CFG + File.separator);
                if (unfinishedTask != null) {
                    for (int i = 0; i < unfinishedTask.size(); i++) {
                        downloader.singleTask.Mission m = unfinishedTask.get(i);
                        try {
                            DownloadMission.startMission(m, new DownloadMission.OnStatusListener() {
                                @Override
                                public void onSuccess(Mission mission) {
                                    DownloadHelper.deleteMissionFile(mission);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }
}
