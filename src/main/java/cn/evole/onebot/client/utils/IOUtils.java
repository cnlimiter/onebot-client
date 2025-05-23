package cn.evole.onebot.client.utils;

import java.io.*;
import java.nio.file.Files;

/**
 * @Project: CmdKey
 * @Author: cnlimiter
 * @CreateTime: 2025/3/16 01:47
 * @Description:
 */
public class IOUtils {
    public static void writeFile(String data, File file){
        OutputStream out = null;
        try {
            out = Files.newOutputStream(file.toPath());
            out.write(data.getBytes());
            out.flush();
        } catch (IOException e) {}
        finally {
            close(out);
        }
    }

    public static String readFile(File file){
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = Files.newInputStream(file.toPath());
            out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
            return out.toString();
        } catch (IOException e) {
            return "";
        }
        finally {
            close(in);
            close(out);
        }
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // nothing
            }
        }
    }
}
