package com.bamboocloud.dao;

import com.bamboocloud.entity.Flow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * mapper
 *
 * @author luaku
 * @date 2021/10/25
 */
@Repository
public interface AcceptMapper extends BaseMapper<Flow> {
    Flow selectByBindId(String bindId);
}
