package com.bamboocloud.mq;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bamboocloud.constant.LogCollect;
import com.bamboocloud.enmu.LogType;
import com.bamboocloud.entity.OimOrg;
import com.bamboocloud.entity.OimUser;
import com.bamboocloud.im.entity.Organization;
import com.bamboocloud.im.entity.User;
import com.bamboocloud.service.BimHessianService;
import com.bamboocloud.utils.ConvertSyncObjUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weblogic.wsee.util.StringUtil;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Component
@Slf4j
public class RabbitMqReceiver {

    @Autowired
    private BimHessianService bimHessianService;

    private static RabbitMqReceiver rabbitMqReceiver;

    //没有这玩意,@Component中无法直接@Autowired
    @PostConstruct
    public void init() {
        rabbitMqReceiver = this;
    }

    @RabbitListener(queues = "oimSyncUserQueue")//监听的队列名称,多个消费者抢占消息
    @RabbitHandler
    public void oimSyncUserQueue(Channel channel, Message message, OimUser oimUser) throws Exception {
        JSONObject messageContent = (JSONObject) JSONObject.toJSON(oimUser);
        log.info("DirectReceiver消费者收到消息 : {}", oimUser);
        if (bimHessianService.getUserByUserName(oimUser.getUserLogin()) != null) {  //用户名重复的不要
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        User user = ConvertSyncObjUtils.getConvertSyncObjUtils().convertUser(messageContent,true);
        bimHessianService.getUserSecretFromOIM(user.getUsername());
        bimHessianService.addBimUser(user);
        //消息的标识，false只确认当前一个消息收到，true确认所有consumer获得的消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }

    @RabbitListener(queues = "oimSyncOrgQueue")//监听的队列名称,多个消费者抢占消息
    @RabbitHandler
    @LogCollect(LogType.ERROR)
    public void oimSyncOrgQueue(Channel channel, Message message, OimOrg oimOrg) throws IOException {
        JSONObject messageContent = (JSONObject) JSONObject.toJSON(oimOrg);
        log.info("DirectReceiver消费者收到消息 : {}", messageContent);
        Organization org = ConvertSyncObjUtils.getConvertSyncObjUtils().convertOrg(messageContent);
        if (StringUtil.isEmpty(org.getCode())) {  //机构code为空的不要
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        if (bimHessianService.getOrgByGuid(org.getGuid()) != null) {  //机构guid重复的不要
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        bimHessianService.addBimOrg(org);
        //消息的标识，false只确认当前一个消息收到，true确认所有consumer获得的消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        //重新放入队列
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        //抛弃此条消息
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
    }
}
