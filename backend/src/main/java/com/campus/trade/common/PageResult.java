package com.campus.trade.common;

import java.util.List;

//分页结果封装类，返回分类查询的数据
public class PageResult<T> {
    //当前页面
    private List<T> records;
    //总数量
    private long total;
    //分页
    private int page;
    //这页有多少条
    private int size;

    public PageResult() {
    }

    public PageResult(List<T> records, long total, int page, int size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
