package downloader.singleTask.util;

import downloader.model.DownloadConst;
import downloader.singleTask.Mission;
import downloader.utils.TextUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DownloadHelper {
    // 保存 任务
    public static boolean saveConfig(Mission mission) {
        //System.out.println(downloader.DownloadManager.DOWNLOAD_LOG_TAG + "saveConfig(...). " + mission);
        try {
            if (null == mission) {
//                System.out.println(downloader.DownloadManager.DOWNLOAD_LOG_TAG + " the mission has delete.");
                return false;
            }
            // 目录创建
            File file = new File(getMissionCfgDir(mission));
            if (!file.exists()) {
                if (!file.mkdirs()) {
//                    LogUtil.LogMsg(DownloadHelper.class, downloader.DownloadManager.DOWNLOAD_LOG_TAG + "create Config dir fail. " + DownloadConst.DOWNLOAD_CONFIG_absolute_DIR);
                    return false;
                }
            }

            //文件
            String cfg = getMissionCfgPath(mission);
            File cfgFile = new File(cfg);
            if (!cfgFile.exists()) {
                if (!cfgFile.createNewFile()) {
//                    LogUtil.LogMsg(DownloadHelper.class, downloader.DownloadManager.DOWNLOAD_LOG_TAG + "create Config file fail. " + cfg);
                    return false;
                }
            }
            FileWriter fw = new FileWriter(cfgFile, false);
            BufferedWriter bw = new BufferedWriter(fw);

            String json = mission.toJsonString();
//            String compressString = CompressUtil.compress(json);
            bw.write(json);

            bw.close();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    // 读取所有任务
    public static List<Mission> readCfgFromDir(String dir) {

        try {


            File dirFile = new File(dir);
            if (dirFile.isDirectory() && dirFile.exists()) {

                String[] FileNames = dirFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.substring(filename.length() - DownloadConst.MISSION_CONFIG_SUFFIX.length()).equals(DownloadConst.MISSION_CONFIG_SUFFIX);
                    }
                });

                List<downloader.singleTask.Mission> missionList = new ArrayList<Mission>();
                for (String fileName : FileNames) {

                    String cfgPath = dir + fileName;
                    downloader.singleTask.Mission mission = readCfg(cfgPath);
                    if (mission != null) {
                        missionList.add(mission);
                    }
                }
                return missionList;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 读取单个任务
     */
    public static downloader.singleTask.Mission readCfg(String cfgPath) {

        try {
            //后缀错误
            if (!cfgPath.substring(cfgPath.length() - DownloadConst.MISSION_CONFIG_SUFFIX.length()).equals(DownloadConst.MISSION_CONFIG_SUFFIX))
                return null;

            File cfgFile = new File(cfgPath);
            if (!cfgFile.exists())
                return null;

            FileReader fr = new FileReader(cfgFile);
            BufferedReader br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            char[] buff = new char[1024];
            int length;
            while ((length = br.read(buff)) > 0) {
                sb.append(buff, 0, length);
            }

            fr.close();
            br.close();

            String compressString = sb.toString();
//            String jsonString = CompressUtil.deCompress(compressString);

            downloader.singleTask.Mission mission = new downloader.singleTask.Mission();
            mission.setModelByJson(compressString);

            if (mission.savedFile_path == null || TextUtils.isEmpty(mission.url))
                return null;

            return mission;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除任务的相关文件（配置文件）
     */
    public static boolean deleteMissionFile(downloader.singleTask.Mission mission) {
        //System.out.println(downloader.DownloadManager.DOWNLOAD_LOG_TAG + "deleteMissionFile(...). " + mission);
        try {
            //配置文件
            String cfg = getMissionCfgPath(mission);
            File cfgFile = new File(cfg);
            if (cfgFile.exists()) {
                if (!cfgFile.delete()) {
                    System.out.println("deleteMissionFile(...). cfgFile delete fail." + cfg);
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getMissionCfgPath(Mission m) {
        return m.savedFile_path + File.separator + DownloadConst.DOWNLOAD_LOCAL_relative_DIR_CFG + File.separator + m.savedFilename + DownloadConst.MISSION_CONFIG_SUFFIX;
    }
    public static String getMissionCfgDir(Mission m) {
        return m.savedFile_path + File.separator + DownloadConst.DOWNLOAD_LOCAL_relative_DIR_CFG;
    }

}
