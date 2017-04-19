package downloader.singleTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Stream;

import static downloader.model.DownloadConst.DEFAULT_CONN_TIMEOUT;


/**
 * Created by liyonglin on 2017/4/17.
 */
public class DownloadMission {

    Stream in;
    long timestamp = System.currentTimeMillis();
    int counter = 0;
    int INTERVAL = 1000; // one second
    int LIMIT = 1000; // bytes per INTERVAL


    /**
     * @param mission
     * @return
     * @throws IOException
     */
    public static boolean startMission(Mission mission) throws IOException {
        return startMission(mission, null);
    }

    public static boolean startMission(Mission mission, OnStatusListener listener) throws IOException {
        URL url = new URL(mission.url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("Connection", "Keep-Alive");
//        conn.setRequestProperty("Accept-Ranges", "bytes");
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(DEFAULT_CONN_TIMEOUT);
        conn.setReadTimeout(DEFAULT_CONN_TIMEOUT);
        conn.setRequestProperty(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg," +
                        "application/x-shockwave-flash, application/xaml+xml," +
                        "application/vnd.ms-xpsdocument, application/x-ms-xbap," +
                        "application/x-ms-application, application/vnd.ms-excel," +
                        "application/vnd.ms-powerpoint, application/msword, */*"
        );
        conn.setRequestProperty("Charset", "UTF-8");
//        conn.setRequestProperty("Range", "bytes=" + currentPos + "-" + endPos);//connect

        int responsecode = conn.getResponseCode();

        int fileSize = conn.getContentLength();//任务数据长度。

        mission.fileLength = fileSize;

        int currentPos = mission.currentPosion; // 开始位置  新任务的默认初始值为0 ，
        int endPos = mission.fileLength + 1; // 结束位置

        RandomAccessFile raf = new RandomAccessFile(mission.savedFile_path + File.separator + mission.savedFilename, "rw");
        raf.seek(currentPos);


        if (responsecode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream =  conn.getInputStream();

            byte[] buffer = new byte[16 * 1024];//16k buffer as default.
            int hasRead = 0;
            //读取网络数据，并写入本地文件
            while (currentPos < endPos
                    && (hasRead = inputStream.read(buffer)) != -1) {

                hasRead = (endPos - currentPos > hasRead) ? hasRead : endPos - currentPos;
                raf.write(buffer, 0, hasRead);
                // 位置移动
                currentPos += hasRead;
            }

            if (listener != null && hasRead == -1) {//下载完
                listener.onSuccess(mission);
            }


            conn.disconnect();
            inputStream.close();

            raf.close();
            conn = null;
            inputStream = null;
            raf = null;

        } else if (responsecode == HttpURLConnection.HTTP_PARTIAL) {//PARTIAL resourse snippets.

            InputStream inputStream = conn.getInputStream();

            byte[] buffer = new byte[16 * 1024];//16k buffer as default.
            int hasRead = 0;
            //读取网络数据，并写入本地文件
            //System.out.println(downloader.DownloadManager.DOWNLOAD_LOG_TAG + threadidTAG + "DownloadThread.run() while before " + currentPos + "|" + endPos);
            while (currentPos < endPos
                    && (hasRead = inputStream.read(buffer)) != -1) {

                // 只写入当前分片的数据
                hasRead = (endPos - currentPos > hasRead) ? hasRead : endPos - currentPos;
                raf.write(buffer, 0, hasRead);
                // 位置移动
                currentPos += hasRead;
            }

        }
        return true;
    }

    public interface OnStatusListener {
        void onSuccess(Mission mission);
    }

}
