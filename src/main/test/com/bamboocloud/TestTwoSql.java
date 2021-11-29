package com.bamboocloud;


import com.alibaba.fastjson.JSONObject;
import com.bamboocloud.dao.AcceptMapper;
import com.bamboocloud.entity.OimUser;
import com.bamboocloud.oracle.OimDataMapper;
import com.bamboocloud.entity.Flow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExternalSpringBootApplication.class)
public class TestTwoSql {

    @Autowired
    private OimDataMapper oimDataMapper;

    @Autowired
    private AcceptMapper acceptMapper;

    @Test
    public void test1() throws Exception {
        List<OimUser> result = oimDataMapper.getUserByPage(5,11);
        System.out.println(JSONObject.toJSONString(result));
        Flow flow = acceptMapper.selectByBindId("123");
        System.out.println(flow);
    }
}
