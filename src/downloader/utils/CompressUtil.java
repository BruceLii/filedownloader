package downloader.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by King on 2015/12/11.
 *
 * universal compress tool
 */
public class CompressUtil {

    /**
     * compress string to byte[] then Base64 encoding
     * @param src   source text
     * @return      the compress text
     */
    public static String compress(String src) {

        String result = null;

        try {

            if (src != null) {

                ByteArrayOutputStream bos = null;
                GZIPOutputStream gos = null;
                try {
                    bos = new ByteArrayOutputStream();
                    gos = new GZIPOutputStream(bos);

                    gos.write(src.getBytes());


                    gos.finish();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (gos != null)
                            gos.close();
                        byte[] bytes = bos.toByteArray();
                        result = new BASE64Encoder().encode(bytes);
                    } catch (Exception e) {
                    }
                    try {
                        if (bos != null)
                            bos.close();
                    } catch (Exception e) {
                    }
                }
            }

        } catch (Exception e) {

        }

        return result;
    }

    /**
     * decompress the compressString with Base64 encoding
     * @param compressSrc   compressedString
     * @return               decompressString
     */
    public static String deCompress(String compressSrc) {

        String result = null;

        try {

            if (compressSrc != null) {

                byte[] bytes = new BASE64Decoder().decodeBuffer(compressSrc);

                result = deCompress(bytes);

            }

        } catch (Exception e) {

        }

        return result;
    }

    /**
     * decompress the compress bytes to original
     * @param compressBytes bytes with compress
     * @return  originally string
     */
    public static String deCompress(byte[] compressBytes) {
        return deCompress(compressBytes, "utf-8");
    }
    public static String deCompress(byte[] compressBytes, String encode) {

        String result = null;

        try {
            ByteArrayInputStream bis = null;
            GZIPInputStream gis = null;
            ByteArrayOutputStream bos = null;

            try {
                bis = new ByteArrayInputStream(compressBytes);
                gis = new GZIPInputStream(bis);
                bos = new ByteArrayOutputStream();

                byte[] buff = new byte[4096];
                int len;
                while ((len = gis.read(buff)) > 0) {
                    bos.write(buff, 0, len);
                }
                result = new String(bos.toByteArray(), encode);

            } catch (Exception e) {
                e.printStackTrace();
                result = new String(compressBytes, encode); // 若解压缩失败，则直接返回原文
            } finally {
                try {
                    if (bos != null)
                        bos.close();
                } catch (Exception e){}
                try {
                    if (gis != null)
                        gis.close();
                } catch (Exception e){}
                try {
                    if (bis != null)
                        bis.close();
                } catch (Exception e){}
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}