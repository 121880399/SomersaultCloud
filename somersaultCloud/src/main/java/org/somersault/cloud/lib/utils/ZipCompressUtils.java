package org.somersault.cloud.lib.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/30 8:55
 * 描    述：使用Zip对文件或者文件夹进行压缩
 * 修订历史：
 * ================================================
 */
class ZipCompressUtils {

    private static boolean isCreateSrcDir = true;//是否创建源目录
    private static final String TAG = "ZipCompress";


    /**
    * @param srcPath 要压缩的文件，可以是文件夹
     * @param archivePath 压缩包存放的路径
     * @param comment 压缩包注释
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/30 9:08
    */
    public static void zipCompress(String srcPath, String archivePath,
                                 String comment) throws FileNotFoundException, IOException {
        //----压缩文件：
        FileOutputStream f = new FileOutputStream(archivePath);
        //使用指定校验和创建输出流
        CheckedOutputStream csum = new CheckedOutputStream(f, new CRC32());

        ZipOutputStream zos = new ZipOutputStream(csum);

        BufferedOutputStream out = new BufferedOutputStream(zos);
        //设置压缩包注释
        zos.setComment(comment);
        //启用压缩
        zos.setMethod(ZipOutputStream.DEFLATED);
        //压缩级别为最强压缩，但时间要花得多一点
        zos.setLevel(Deflater.BEST_COMPRESSION);

        File srcFile = new File(srcPath);

        if (!srcFile.exists() || (srcFile.isDirectory() && srcFile.list().length == 0)) {
            throw new FileNotFoundException(
                    "File must exist and  ZIP file must have at least one entry.");
        }
        //获取压缩源所在父目录
        srcPath = srcPath.replaceAll("\\\\", "/");
        String prefixDir = null;
        if (srcFile.isFile()) {
            prefixDir = srcPath.substring(0, srcPath.lastIndexOf("/") + 1);
        } else {
            prefixDir = (srcPath.replaceAll("/$", "") + "/");
        }

        //如果不是根目录
        if (prefixDir.indexOf("/") != (prefixDir.length() - 1) && isCreateSrcDir) {
            prefixDir = prefixDir.replaceAll("[^/]+/$", "");
        }

        //开始压缩
        writeRecursive(zos, out, srcFile, prefixDir);

        out.close();
        // 注：校验和要在流关闭后才准备，一定要放在流被关闭后使用
        Logger.Companion.d(TAG,"Checksum: " + csum.getChecksum().getValue());
    }



    /**
     * 使用 java api 中的 ZipInputStream 类解压文件，但如果压缩时采用了
     * org.apache.tools.zip.ZipOutputStream时，而不是 java 类库中的
     * java.util.zip.ZipOutputStream时，该方法不能使用，原因就是编码方
     * 式不一致导致，运行时会抛如下异常：
     * java.lang.IllegalArgumentException
     * at java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:290)
     *
     * 当然，如果压缩包使用的是java类库的java.util.zip.ZipOutputStream
     * 压缩而成是不会有问题的，但它不支持中文
     *
     * @param archive 压缩包路径
     * @param decompressDir 解压路径
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void zipUnCompress(String archive, String decompressDir)
            throws FileNotFoundException, IOException {
        BufferedInputStream bi;
        //----解压文件(ZIP文件的解压缩实质上就是从输入流中读取数据):
        Logger.Companion.d(TAG,"开始读压缩文件");
        FileInputStream fi = new FileInputStream(archive);
        CheckedInputStream csumi = new CheckedInputStream(fi, new CRC32());
        ZipInputStream in2 = new ZipInputStream(csumi);
        bi = new BufferedInputStream(in2);
        java.util.zip.ZipEntry ze;//压缩文件条目
        //遍历压缩包中的文件条目
        while ((ze = in2.getNextEntry()) != null) {
            String entryName = ze.getName();
            if (ze.isDirectory()) {
                Logger.Companion.d(TAG,"正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(decompressDir + "/" + entryName);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                Logger.Companion.d(TAG,"正在创建解压文件 - " + entryName);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                        decompressDir + "/" + entryName));
                byte[] buffer = new byte[1024];
                int readCount = bi.read(buffer);

                while (readCount != -1) {
                    bos.write(buffer, 0, readCount);
                    readCount = bi.read(buffer);
                }
                bos.close();
            }
        }
        bi.close();
        Logger.Companion.d(TAG,"Checksum: " + csumi.getChecksum().getValue());
    }

    /**
     * 递归压缩
     * @param zos
     * @param bo
     * @param srcFile
     * @param prefixDir
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void writeRecursive(ZipOutputStream zos, BufferedOutputStream bo,
                                       File srcFile, String prefixDir) throws IOException, FileNotFoundException {
        ZipEntry zipEntry;

        String filePath = srcFile.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(
                "//", "/");
        if (srcFile.isDirectory()) {
            filePath = filePath.replaceAll("/$", "") + "/";
        }
        String entryName = filePath.replace(prefixDir, "").replaceAll("/$", "");
        if (srcFile.isDirectory()) {
            if (!"".equals(entryName)) {
                Logger.Companion.d(TAG,"正在创建目录 - " + srcFile.getAbsolutePath()
                        + "  entryName=" + entryName);

                //如果是目录，则需要在写目录后面加上 /
                zipEntry = new ZipEntry(entryName + "/");
                zos.putNextEntry(zipEntry);
            }

            File srcFiles[] = srcFile.listFiles();
            for (int i = 0; i < srcFiles.length; i++) {
                writeRecursive(zos, bo, srcFiles[i], prefixDir);
            }
        } else {
            Logger.Companion.d(TAG,"正在写文件 - " + srcFile.getAbsolutePath() + "  entryName="
                    + entryName);
            BufferedInputStream bi = new BufferedInputStream(new FileInputStream(srcFile));

            //开始写入新的ZIP文件条目并将流定位到条目数据的开始处
            zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int readCount = bi.read(buffer);

            while (readCount != -1) {
                bo.write(buffer, 0, readCount);
                readCount = bi.read(buffer);
            }
            //注，在使用缓冲流写压缩文件时，一个条件完后一定要刷新一把，不
            //然可能有的内容就会存入到后面条目中去了
            bo.flush();
            //文件读完后关闭
            bi.close();
        }
    }
}
