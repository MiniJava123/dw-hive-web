package com.dianping.dw.hive.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.dianping.dw.hive.util.CommonUtil;

/**
 * 查询结果对象
 * 
 * @author tao.meng
 */
@XmlRootElement
public class ResultDO<T> {

    private boolean success;
    private String messages;
    private T result;
    private List<T> results = new ArrayList<T>();

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return CommonUtil.toJson(this);
    }

}
