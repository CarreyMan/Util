package util;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.*;
import java.util.Iterator;

public class ZipUtil {/**
 * 解压缩
 * @param warPath 包地址
 * @param unzipPath 解压后地址
 */
public static void unzip(String warPath, String unzipPath) {
    File warFile = new File(warPath);
    try {
        //获得输出流
        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new FileInputStream(warFile));
        ArchiveInputStream in = new ArchiveStreamFactory()
                .createArchiveInputStream(ArchiveStreamFactory.ZIP,
                        bufferedInputStream);
        JarArchiveEntry entry = null;
        //循环遍历解压
        while ((entry = (JarArchiveEntry) in.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                new File(unzipPath, entry.getName()).mkdir();
            } else {
                OutputStream out = FileUtils.openOutputStream(new File(
                        unzipPath, entry.getName()));
                IOUtils.copy(in, out);
                out.close();
            }
        }
        in.close();
    } catch (FileNotFoundException e) {
        System.err.println("未找到war文件");
    } catch (ArchiveException e) {
        System.err.println("不支持的压缩格式");
    } catch (IOException e) {
        System.err.println("文件写入发生错误");
    }
}
    /**
     * 压缩
     * @param destFile 创建的地址及名称
     * @param zipDir 要打包的目录
     */
    public static void zip(String destFile, String zipDir) throws IOException, ArchiveException {
        BufferedOutputStream bufferedOutputStream;
        ArchiveOutputStream out=null;
        try {
            File outFile = new File(destFile);
            outFile.createNewFile();
            //创建文件
            bufferedOutputStream= new BufferedOutputStream(
                    new FileOutputStream(outFile));
            out = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP,bufferedOutputStream);
            if (zipDir.charAt(zipDir.length() - 1) != '/') {
                zipDir += '/';
            }
            File dir=new File(zipDir);
            Iterator<File> files = FileUtils.iterateFilesAndDirs(dir, TrueFileFilter.INSTANCE,TrueFileFilter.INSTANCE);
            while (files.hasNext()) {
                File file = files.next();
                if(!file.equals(dir)){//不打当前目录
                    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file,
                            file.getPath().replace(zipDir.replace("/", "\\"), ""));
                    out.putArchiveEntry(zipArchiveEntry);
                    if(file.isFile()){
                        InputStream inputStream=null;
                        try {
                            inputStream=new FileInputStream(file);
                            IOUtils.copy(inputStream, out);
                        }catch (IOException e){
                            System.err.println("压缩失败："+file.getName());
                            throw e;
                        }finally {
                            org.apache.commons.io.IOUtils.closeQuietly(inputStream);
                        }
                    }
                    out.closeArchiveEntry();
                }
            }
            out.finish();
        } catch (IOException e) {
            System.err.println("创建文件失败");
            throw e;
        } catch (ArchiveException e) {
            System.err.println("不支持的压缩格式");
            throw e;
        }finally {
            if(out!=null){
                out.close();
            }
        }
    }

}