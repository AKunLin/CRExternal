package com.bamboocloud.service;

import com.bamboocloud.entity.OimOrg;
import com.bamboocloud.entity.OimUser;
import com.bamboocloud.im.entity.Lookup;
import com.bamboocloud.im.entity.Organization;
import com.bamboocloud.im.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用BimService
 *
 * @author luaku
 * @date 2021/10/26
 */
public interface BimHessianService {
    //用户
    void addBimUser(User user);

    void updateBimUser(User user);

    User getUserByUserName(String username);

    //机构
    void addBimOrg(Organization org);

    void updateBimOrg(Organization org);

    Organization getOrgById(String orgId);

    Organization getOrgByCode(String code);

    Organization getOrgByGuid(String guid);

    //数据字典

    Lookup  getLookupByLdfCodeAndLookupCode(String defCode,String lookupCode);

    String getUserTypeIdByCode(String code);

    //获取密文用户密码
    String getUserSecretFromOIM(String username) throws Exception;

    List<OimUser> getOIMUserByPage(int page , int row);

    List<OimOrg> getOIMOrgByPage(int page , int row);

    Integer getOIMUserSize();

    Integer getOIMOrgSize();

    Map<String,Object> getOIMRealUser(String username);

    Map<String,Object> getOIMRealOrg(String actKey);

    List<User> getUserByPage(int pageNum,int pageSize);

    List<Organization> getOrgByPage(int pageNum,int pageSize);

}
