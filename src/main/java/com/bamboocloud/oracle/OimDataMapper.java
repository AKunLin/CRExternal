package com.bamboocloud.oracle;

import com.bamboocloud.entity.OimOrg;
import com.bamboocloud.entity.OimUser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface OimDataMapper {
    Integer getUserSize();

    Integer getOrgSize();

    List<OimUser> getUserByPage(Integer rownum, Integer rn);

    List<OimOrg> getOrgByPage(Integer rownum, Integer rn);

    Map<String,Object> getOIMRealUser(String username);

    Map<String,Object> getOIMRealOrg(String actKey);
}
