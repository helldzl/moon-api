/*
package com.mifan.award.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.mifan.award.domain.Record;
import com.mifan.award.service.RecordService;

public class ThreadUpdateAddress implements Runnable {

    private RecordService recordService;

    private String recordId;
    private String ipAddress;

    public void setRecordService(RecordService recordService) {
        this.recordService = recordService;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        JSONObject jsonObject = (JSONObject) JSONObject.parse(IpUtil.getAddressByIp(ipAddress));

        Record record =new Record();
        record.setId(this.recordId);
        record.setIpState(jsonObject.getString("errNum"));
        record.setIpAddress(jsonObject.getString("retData"));
        recordService.update(record);
    }
}
*/
