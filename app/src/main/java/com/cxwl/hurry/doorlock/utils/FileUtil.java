package com.cxwl.hurry.doorlock.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by William on 2018/6/21.
 */

public class FileUtil {
    public static File saveFile(Response response, final int id, String destFileDir, String destFileName) throws
            IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();

            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
//                OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        inProgress(finalSum * 1.0f / total, total, id);
//                    }
//                });
            }
            fos.flush();
            Log.e("下载", "成功完成");
            return file;
        } finally {
            try {
                response.body().close();
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }

        }
    }

    /**
     * 获取本地存储路径下的所有文件
     * @return
     * @param dir
     */
    public static File[] getAllLocalFiles(String dir) {
        File[] files = new File[0];
        File path = new File(dir);
        if (path.isDirectory()) {
            files = path.listFiles();
        }
        return files;
    }
}
