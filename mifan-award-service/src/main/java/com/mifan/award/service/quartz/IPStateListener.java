/*
package com.mifan.award.service.quartz;

import com.alibaba.fastjson.JSONObject;
import com.mifan.award.domain.Record;
import com.mifan.award.service.RecordService;
import com.mifan.award.service.utils.IpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("ipStateListener")
public class IPStateListener {
    private static Log logger = LogFactory.getLog(IPStateListener.class);
    @Autowired
    private RecordService recordService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void listenIPFailedState() {
        Record findRecord = new Record();
        findRecord.setIpState("0");
        List<Record> findRecordList = recordService.findAll(findRecord,0,20,null,null);
        for (Record record : findRecordList) {
            logger.info(record.getId());
            if(! StringUtils.isEmpty(record.getIp())){
                JSONObject jsonObject = (JSONObject) JSONObject.parse(IpUtil.getAddressByIp(record.getIp()));
                record.setIpState(jsonObject.getString("errNum"));
                record.setIpAddress(jsonObject.getString("retData"));
                recordService.update(record);
            }
        }
    }
}
*/
