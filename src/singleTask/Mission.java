package singleTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyonglin on 2017/4/17.
 */
public class Mission {
    public String url;//任务地址   此为主键。
    public String savedFilename;//文件名：

    public String savedFile_path;//保存路径


    public int missionStatus = 0;

    public int fileLength = 0; // 文件长度

    public int currentPosion = 0;//当前任务进度

//    public List<Integer> currentPosList = new ArrayList<>();// 每个任务的当前下载位置
//    public List<Integer> startPosList = new ArrayList<>(); // 每个任务的开始位置


}
