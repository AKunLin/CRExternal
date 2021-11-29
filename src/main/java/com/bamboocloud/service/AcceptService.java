package com.bamboocloud.service;

import com.bamboocloud.entity.Flow;

/**
 * 账号service
 *
 * @author luaku
 * @date 2021/10/25
 */

public interface AcceptService  {
    int addFlower(Flow flow) throws Exception;

    Flow findByBinId(String bindId);

    void updateByEn(Flow flow);
}
