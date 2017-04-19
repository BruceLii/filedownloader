package downloader.utils;

/**
 *
 * jsonFormat Util
 * Created by King on 2015/10/28.
 */
public class JsonFormatter {

    public static final String TAB = "\t\t";
    public static final String RETURN = "\n";

    public static String format(String jsonString) {

        String result = null;

        int logLineLength = 0;

        try {

            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            boolean isInQuotationMark = false;
            String currentTab = "";
            char beforeAdd;

            for(int i = 0; i < jsonString.length(); i++) {

                String c = jsonString.substring(i, i+1);

                switch (c) {

                    case "\"": {
                        if (i > 0) {
                            if (jsonString.charAt(i-1) != '\\') {
                                logLineLength = 0;
                                sb.append(c);
                                isInQuotationMark = !isInQuotationMark;
                            }

                        }
                        break;
                    }

                    case "[":
                    case "{": {
                        logLineLength = 0;
                        if (!isInQuotationMark) {
                            currentTab += TAB;
                            sb.append(c);
                            sb.append(RETURN + currentTab);
                        } else {
                            sb.append(c);
                        }
                        break;
                    }

                    case  "]":
                    case "}": {
                        if (!isInQuotationMark) {
                            currentTab = currentTab.substring(TAB.length());
                            sb.append(RETURN + currentTab);
                            sb.append(c);
                            sb.append(RETURN);
                        } else {
                            sb.append(c);
                        }

                        break;
                    }
                    case ",": {

                        if (!isInQuotationMark) {
                            sb.append(c);
                            sb.append(RETURN + currentTab);
                        } else {
                            sb.append(c);
                        }

                        break;
                    }
                    default: {
                        if(logLineLength >= 500){
                            sb.append(RETURN + currentTab);
                            logLineLength = 0;
                        }
                        logLineLength ++;
                        sb.append(c);
                    }
                }
            }
            result = sb.toString();

        } catch (Exception e) {
//            Log.e("JsonFormatter", "Error occur while format, error is " + e.getMessage());
//            System.out.print("Error occur while format, error is " + e.getMessage());
            result = jsonString;
        }
        return result;
    }

}
