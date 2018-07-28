/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.yansuan.oma.model;

/**
 *
 * @author user
 */
public class DbaDataFilesInfo {

    private String fileName;
    private String tablespaceName;
    private double mb;
    private String bigfile;

    public void setFileName(String value) {
        this.fileName = value;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setTablespaceName(String value) {
        this.tablespaceName = value;
    }

    public String getTablespaceName() {
        return this.tablespaceName;
    }

    public void setMb(double value) {
        this.mb = value;
    }

    public double getMb() {
        return this.mb;
    }
    
    public void setBigfile(String value) {
        this.bigfile = value;
    }
    public String getBigfile() {
        return this.bigfile;
    }
}
