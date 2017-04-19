package downloader.model;

import downloader.utils.DateUtil;
import downloader.utils.JsonFormatter;
import downloader.utils.JsonHelper;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Wayne on 2015/12/2.
 */
public class BaseModel implements Serializable {

    protected static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    /**
     * 建议子类保留无参构造函数
     */
    public BaseModel() {

    }

    /**
     * 克隆函数。复制克隆对象的同名字段
     *
     * @param reqModel 克隆对象
     */
    public void clone(BaseModel reqModel) {
        List<String> reqModelFieldNames = Arrays.asList(reqModel.getPropertyNames());
        Field[] curFields = this.getFields();
        for (Field field : curFields) {
            if (reqModelFieldNames.contains(field.getName())) {
                try {
                    field.set(this, reqModel.getValueByName(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * set downloader.model by jsonString, support multi level jsonString
     *
     * @param jsonString the jsonString
     * @param level      witch level is the downloader.model's json
     * @param levelNames all elementNames(from top) of the jsonString, the length must be equals level
     */
    public void setModelByJson(String jsonString, int level, String[] levelNames) {

        try {
            if (level == 0) {
                setModelByJson(jsonString);
                return;
            }

            if (level != levelNames.length) {
                return;
            }

            Map jsonMap = JsonHelper.json2Map(jsonString);
            while (level > 0) {

                level--;
                String elementName = levelNames[level];
                Map insideMap = (Map) jsonMap.get(elementName);
                jsonMap = insideMap;
                if (level == 0) {
                    setModelByMap(jsonMap);
                    return;
                }
            }

        } catch (Exception ex) {
//            logErr(ex);
        }

    }

    public void setModelByJson(String jsonString) {

        Map map = JsonHelper.json2Map(jsonString);
        setModelByMap(map);

    }

    public void setModelByMap(Map map) {

        try {
            if (map == null) {
                return;
            }

            Field[] fields = getFields();

            for (Field field : fields) {
                try {

                    if (!map.containsKey(field.getName())) {
                        continue;
                    }

                    Object value = map.get(field.getName());

                    if (value == null) {
                        continue;
                    }

                    Class<?> type = field.getType();

                    if (type.equals(String.class)) {

                        String val = value.toString();
                        field.set(this, val);

                    } else if (type.equals(Date.class)) {

                        if (value instanceof Date) {
                            field.set(this, value);
                            continue;
                        }

                        String val = value.toString();
                        if (val == null)
                            continue;

                        Date date = null;
                        try {
                            if (val.length() == 10) {
                                long l = Long.valueOf(val);
                                date = new Date(l * 1000);
                            } else if (val.length() == 13) {
                                long l = Long.valueOf(val);
                                date = new Date(l);
                            }

                        } catch (Exception e) {
                        }
                        if (date == null) {
                            date = DateUtil.String2Date(val, "yyyy-MM-dd HH:mm:ss");
                            if (date == null) {
                                date = DateUtil.String2Date(val, "yyyy-MM-dd HH:mm");
                                if (date == null) {
                                    date = DateUtil.String2Date(val, "yyyy-MM-dd");
                                    if (date == null) {
                                        date = DateUtil.String2Date(val, "yyyyMMdd");
                                    }
                                }
                            }
                        }
                        field.set(this, date);

                    } else if (type.equals(int.class) || type.equals(Integer.class)) {

                        String val = value.toString();
                        if (val == null)
                            continue;
                        try {
                            int i = Integer.valueOf(val);
                            field.set(this, i);
                        } catch (Exception e) {
//                            logErr(e);
                        }

                    } else if (type.equals(long.class) || type.equals(Long.class)) {

                        String val = value.toString();
                        if (val == null) {
                            continue;
                        }
                        try {
                            long l = Long.valueOf(val);
                            field.set(this, l);
                        } catch (Exception e) {
//                            logErr(e);
                        }

                    } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {

                        String val = value.toString();
                        if (val == null)
                            continue;
                        try {
                            int i = Integer.valueOf(val);
                            if (i <= 0) {
                                field.set(this, false);
                            } else {
                                field.set(this, true);
                            }
                            continue;
                        } catch (Exception e) {
                        }
                        try {
                            boolean b = Boolean.valueOf(val);
                            field.set(this, b);
                        } catch (Exception e) {
//                            logErr(e);
                        }
                    } else if (type.equals(double.class) || type.equals(Double.class)) {

                        String val = value.toString();
                        if (val == null)
                            continue;
                        try {
                            double num = Double.valueOf(val);
                            field.set(this, num);
                        } catch (Exception e) {
//                            logErr(e);
                        }

                    } else if (type.equals(float.class) || type.equals(Float.class)) {

                        String val = value.toString();
                        if (val == null)
                            continue;
                        try {
                            float num = Float.valueOf(val);
                            field.set(this, num);
                        } catch (Exception e) {
//                            logErr(e);
                        }

                    } else if (value instanceof Map) {

                        if (Map.class.isAssignableFrom(type) || type.equals(Object.class)) {
                            field.set(this, value);
                        } else if (BaseModel.class.isAssignableFrom(type)) {
                            BaseModel baseModel = (BaseModel) field.get(this);
                            if (baseModel == null)
                                baseModel = (BaseModel) type.newInstance();
                            baseModel.setModelByMap((Map) value);

                            field.set(this, baseModel);
                        }

                    } else if (value instanceof List) {

                        if (List.class.isAssignableFrom(type)) {
                            //获取泛型类型
                            Type genericType = field.getGenericType();

                            if (genericType != null && genericType instanceof ParameterizedType) {
                                try {
                                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                                    Class cls = (Class) parameterizedType.getActualTypeArguments()[0];
                                    if (cls != null && BaseModel.class.isAssignableFrom(cls)) {

                                        List list = (List) value;
                                        List modelList = new ArrayList();
                                        boolean flag = true;
                                        for (int i = 0; i < list.size(); i++) {
                                            Object obj = list.get(i);
                                            if (obj instanceof Map) {
                                                Constructor[] constructors = cls.getConstructors();
                                                Constructor constructor = constructors[0];
                                                List<Object> params = new ArrayList<Object>();
                                                for (Class<?> pType : constructor.getParameterTypes()) {
                                                    params.add((pType.isPrimitive()) ? primitiveWrapperMap.get(pType).newInstance() : null);
                                                }
                                                BaseModel baseModel;
                                                if (params.size() > 0) {
                                                    baseModel = (BaseModel) constructor.newInstance(params.toArray());
                                                } else {
                                                    baseModel = (BaseModel) constructor.newInstance();
                                                }
                                                baseModel.setModelByMap((Map) obj);
                                                modelList.add(baseModel);
                                            } else {
                                                field.set(this, value);
                                                flag = false;
                                                break;
                                            }
                                        }
                                        if (flag) {
                                            field.set(this, modelList);
                                        }


                                    } else {
                                        field.set(this, value);
                                    }

                                } catch (Exception e) {
//                                    logErr(e);
                                    field.set(this, value);
                                }

                            } else {
                                field.set(this, value);
                            }
                        } else {
                            field.set(this, value);
                        }
                    } else {
                        field.set(this, value);
                    }

                } catch (NumberFormatException e) {
//                    logErr(e);
                    field.set(this, -1);
                } catch (Exception ex) {
//                    logErr(ex);
                }
            }


        } catch (Exception ex) {
//            logErr(ex);
        }

    }

    /**
     * 获取字段。包括父类的。
     *
     * @return
     */
    public Field[] getFields() {
        try {
            Class cls = this.getClass();
            Field[] fields = cls.getFields();
            return fields;
        } catch (Exception ex) {
//            logErr(ex);
            return null;
        }
    }

    //获取所有的属性名
    public String[] getPropertyNames() {
        try {

            Field[] fields = getFields();

            String[] result = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                result[i] = fields[i].getName();
            }

            return result;

        } catch (Exception ex) {
//            logErr(ex);
            return null;
        }
    }

    //获取所有属性的值
    public Object[] getValues() {
        try {

            //先获取实例中的所有属性名
            Field[] fields = getFields();

            int length = fields.length;

            Object[] values = new Object[length];

            for (int i = 0; i < length; i++) {
                //获取属性的get方法
                Object obj = fields[i].get(this);

                values[i] = obj;
            }

            return values;

        } catch (Exception ex) {
//            logErr(ex);
            return null;
        }
    }

    public Object getValueByName(String name) {

        try {
            Field field = this.getClass().getField(name);

            Object obj = field.get(this);
            return obj;

        } catch (Exception ex) {
//            logErr(ex);
        }
        return null;
    }

    public String toJsonString() {
        try {
            return JsonHelper.map2Json(toMap());
        } catch (Exception ex) {
//            logErr(ex);
        }
        return null;
    }

    public String toJsonFormatString() {
        try {
            JsonFormatter.format(toJsonString());
        } catch (Exception e) {
//            logErr(e);
        }
        return null;
    }

    public Map<String, Object> toMap() {
        try {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            Field[] fields = getFields();
            for (Field field : fields) {
                String key = field.getName();
                Object value = field.get(this);
                if (value instanceof List) {
                    List resList = new ArrayList();
                    List valueList = (List) value;
                    for (Object obj : valueList) {
                        if (obj instanceof BaseModel) {
                            resList.add(((BaseModel) obj).toMap());
                        } else {
                            resList.add(obj);
                        }
                    }
                    resultMap.put(key, resList);
                } else if (value instanceof BaseModel) {
                    resultMap.put(key, ((BaseModel) value).toMap());
                } else {
                    resultMap.put(key, value);
                }
            }
            return resultMap;
        } catch (Exception ex) {
//            logErr(ex);
        }
        return null;
    }

    public Map<String, Object> toLinkedHashMap() {
        try {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            Field[] fields = getFields();
            for (Field field : fields) {
                String key = field.getName();
                Object value = field.get(this);
                if (value instanceof List) {
                    List resList = new ArrayList();
                    List valueList = (List) value;
                    for (Object obj : valueList) {
                        if (obj instanceof BaseModel) {
                            resList.add(((BaseModel) obj).toLinkedHashMap());
                        } else {
                            resList.add(obj);
                        }
                    }
                    resultMap.put(key, resList);
                } else if (value instanceof BaseModel) {
                    resultMap.put(key, ((BaseModel) value).toLinkedHashMap());
                } else {
                    resultMap.put(key, value);
                }
            }
            return resultMap;
        } catch (Exception ex) {
//            logErr(ex);
        }
        return null;
    }

    public void copyFrom(BaseModel baseModel) {

        try {
            if (baseModel == null)
                return;

            Class cls = baseModel.getClass();
            Class thisClass = getClass();
            if (thisClass.isAssignableFrom(cls)) {

                Field[] fields = getFields();
                for (Field field : fields) {

                    try {

                        Object value = baseModel.getValueByName(field.getName());
                        field.set(this, value);

                    } catch (Exception e) {
//                        logErr(e);
                    }
                }
            }

        } catch (Exception e) {
//            logErr(e);
        }

    }
}
