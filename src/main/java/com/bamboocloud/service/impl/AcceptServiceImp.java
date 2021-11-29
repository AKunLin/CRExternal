package com.bamboocloud.service.impl;

import com.bamboocloud.dao.AcceptMapper;
import com.bamboocloud.entity.Flow;
import com.bamboocloud.service.AcceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 实现类
 *
 * @author luaku
 * @date 2021/10/25
 */
@Service
@Transactional
public class AcceptServiceImp implements AcceptService{
@Autowired
public AcceptMapper acceptMapper;

    @Override
    public int addFlower(Flow flow)throws Exception{
        int insert = 0;
            insert = acceptMapper.insert(flow);
            return insert;
        }

    @Override
    public Flow findByBinId(String bindId){
        Flow flow = acceptMapper.selectByBindId(bindId);
        return flow;
    }

    @Override
    public void updateByEn(Flow flow) {
        acceptMapper.updateById(flow);
    }
}
