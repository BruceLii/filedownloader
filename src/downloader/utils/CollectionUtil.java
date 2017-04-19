package downloader.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by huafu on 2015/3/23.
 */
public final class CollectionUtil {

    //
    public static boolean isCollectionEmpty(Collection coll){
        return (null == coll) || (coll.isEmpty());
    }

    //
    public static boolean isCollectionNotEmpty(Collection coll){
        return !isCollectionEmpty(coll);
    }

    //
    public static boolean isListEmpty(List list){
        return (null == list) || (list.isEmpty());
    }

    //
    public static boolean isListNotEmpty(List list){
        return !isListEmpty(list);
    }

    //
    public static boolean isMapEmpty(Map map){
        return (null == map) || (map.isEmpty());
    }

    //
    public static boolean isMapNotEmpty(Map map){
        return !isMapEmpty(map);
    }

    /** 加分隔符合并字符串 */
    public static String join(Collection<String> coll, String separator) {
        StringBuilder joinBuilder = new StringBuilder();
        if (CollectionUtil.isCollectionEmpty(coll)) {
            return joinBuilder.toString();
        }
        for (String item : coll){
            joinBuilder.append(item).append(separator);
        }
        return joinBuilder.toString().substring(0, joinBuilder.toString().length()-1);
    }
}
