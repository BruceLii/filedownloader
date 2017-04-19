package downloader.utils;

import java.util.Random;

/**
 * Created by liyonglin on 2017/4/13.
 */
public class TextUtils {

    static Random r = new Random(1000);

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }


    /**
     * 去掉window 系统 文件名 不合法字符  \ /  : * ? " < > |
     */
    public static String getFileName(String target) {
        String result = "";
        if (!isEmpty(target)) {
            result = target.replaceAll("\\\\", "")
                    .replaceAll("/", "")
                    .replaceAll(":", "")
                    .replaceAll("\\*", "")
                    .replaceAll("\\?", "")
                    .replaceAll("\"", "")
                    .replaceAll("<", "")
                    .replaceAll(">", "")
                    .replaceAll(">", "")
                    .replaceAll("\\|", "");
        }
        return result;

    }

    public static int getRandomNum() {
        return r.nextInt(100000);
    }
}
