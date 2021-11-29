package com.bamboocloud.controller;

import com.bamboocloud.entity.ExternalResult;
import com.bamboocloud.entity.OimOrg;
import com.bamboocloud.entity.OimUser;
import com.bamboocloud.service.BimHessianService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


/**
 * @author leojack
 * @message 理论上是给oim读取库写数据到我们的mq(被动)
 */
@RestController
@Slf4j
public class RabbitMqController {

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @Autowired
    BimHessianService bimHessianService;

    @GetMapping("/Recycle/OimDataFromView/{objType}")
    public ExternalResult OimDataFromView(@RequestParam Integer page, @RequestParam Integer row, @PathVariable String objType) {
        if ("USER".equals(objType)) {
            List<OimUser> userList = bimHessianService.getOIMUserByPage(page, row);
            log.info("usrSize: {}", bimHessianService.getOIMUserSize());
            /*log.info("usrList: {}", JSONObject.toJSONString(userList));*/
            for (int i = 0; i < userList.size(); i++) {
                rabbitTemplate.convertAndSend("oimSyncExchange", "oimSyncUserQueue", userList.get(i));
            }
        } else if ("ORGANIZATION".equals(objType)) {
            List<OimOrg> orgList = bimHessianService.getOIMOrgByPage(page, row);
            log.info("orgSize: {}", bimHessianService.getOIMOrgSize());
            /*log.info("orgList: {}", JSONObject.toJSONString(orgList));*/
            for (int i = 0; i < orgList.size(); i++) {
                OimOrg oimOrg = orgList.get(i);
                rabbitTemplate.convertAndSend("oimSyncExchange", "oimSyncOrgQueue", oimOrg);
            }
        }else {
            return ExternalResult.dataError("未知实体ObjType",null);
        }

        //将消息携带绑定键值：oimSyncExchange oimSyncExchange

        return ExternalResult.success(null);
    }

    @GetMapping("/Recycle/OimDataFromViewTest/{message}")
    public ExternalResult OimDataFromViewTest(@PathVariable String message) {
        OimOrg oimOrg = new OimOrg();
        oimOrg.setCode(message);
        rabbitTemplate.convertAndSend("oimSyncExchange", "oimSyncOrgQueue", oimOrg);
        return ExternalResult.success(null);
    }


}
