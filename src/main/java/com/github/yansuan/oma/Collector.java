/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.yansuan.oma;

import com.github.yansuan.oma.util.Database;
import com.github.yansuan.oma.model.DbaDataFilesInfo;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author user
 */
public class Collector {

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("url", true,"Oracle jdbc url eg:jdbc:oracle:thin:system/password@192.168.2.102:1521/orcl");
        options.addOption("w", true,"Write sql to files.");
//        CommandLineParser parser = new PosixParser();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options,args);
        
        String url = null;
        if (cmd.hasOption("url")) {
            url = cmd.getOptionValue("url");
        } else {
            String formatstr = "java -jar OMA-1.0-jar-with-dependencies.jar";
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(formatstr, "", options, "");
            return;
        }
        
        String filename = null;
        if (cmd.hasOption("w")) {
            filename = cmd.getOptionValue("w");
        }

        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);

        Database db = null;
        try {
            db = Database.newInstance(url);
            List<DbaDataFilesInfo> datafiles = db.getDbaDataFiles();

            // 生成表空间创建脚本
            StringBuilder sb = new StringBuilder();
            String tablespaceName = null;
            for (int i = 0; i < datafiles.size(); i++) {
                DbaDataFilesInfo info = datafiles.get(i);

                if (tablespaceName != null) {
                    //判断是添加,或;
                    if (info.getTablespaceName().equals(tablespaceName)) {
                        sb.append(",\r\n");
                    } else {
                        sb.append(";\r\n");
                    }
                }

                //判断是否生成create tablespace
                if (tablespaceName == null || !tablespaceName.equals(info.getTablespaceName())) {
                    sb.append("CREATE ");

                    //判断是否有bigfile
                    if (info.getBigfile().equals("YES")) {
                        sb.append("BIGFILE ");
                    }

                    sb.append("TABLESPACE ");
                    sb.append(info.getTablespaceName());
                    sb.append(" DATAFILE \r\n");
                }

                //生成数据文件
                sb.append("SIZE " + nf.format(info.getMb()) + "M AUTOEXTEND ON NEXT 1000M MAXSIZE UNLIMITED");
                tablespaceName = info.getTablespaceName();

            }

            if (tablespaceName != null) {
                sb.append(";\r\n");
            }

            if (filename == null) {
                System.out.println(sb.toString());
            } else {
                //写文件
                FileWriter writer = null;
                try {
                    writer = new FileWriter(filename);
                    writer.write(sb.toString());
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            db.close();
        }
    }
}
