package downloader.model;

import java.io.File;

/**
 * Created by li on 2016/3/3.
 */
public class DownloadConst {

    public static final int DEFAULT_CONN_TIMEOUT = 20000;//连接超时时间

    public final static String ROOT_FOLDER = "D:\\Temp";


    //文件配置下载保存相对路径
    public final static String DOWNLOAD_LOCAL_relative_DIR_CFG = "temp_cfg";

    //文件下载任务配置绝对路径
    public final static String DOWNLOAD_CONFIG_absolute_DIR = ROOT_FOLDER + File.separator + DOWNLOAD_LOCAL_relative_DIR_CFG + File.separator;
    //任务配置文件后缀
    public final static String MISSION_CONFIG_SUFFIX = ".cfg";

}
