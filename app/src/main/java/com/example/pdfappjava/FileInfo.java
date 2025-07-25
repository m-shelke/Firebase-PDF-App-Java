package com.example.pdfappjava;

public class FileInfo {

    String fileUrl,fileName;
    int nod,nol,nov;


    public FileInfo() {
    }

    public FileInfo(String fileUrl, String fileName, int nod, int nol, int nov) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.nod = nod;
        this.nol = nol;
        this.nov = nov;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getNod() {
        return nod;
    }

    public void setNod(int nod) {
        this.nod = nod;
    }

    public int getNol() {
        return nol;
    }

    public void setNol(int nol) {
        this.nol = nol;
    }

    public int getNov() {
        return nov;
    }

    public void setNov(int nov) {
        this.nov = nov;
    }
}
