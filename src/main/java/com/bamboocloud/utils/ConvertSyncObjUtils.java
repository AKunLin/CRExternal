package com.bamboocloud.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.bamboocloud.constant.LogCollect;
import com.bamboocloud.constant.SyncOrgConstant;
import com.bamboocloud.constant.SyncUserConstant;
import com.bamboocloud.enmu.LogType;
import com.bamboocloud.im.entity.Lookup;
import com.bamboocloud.im.entity.Organization;
import com.bamboocloud.im.entity.User;
import com.bamboocloud.service.BimHessianService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author leojack
 * @message 一个转换类, 将传过来的obj转化为我们系统的User和Org实体类
 */
@Component
public class ConvertSyncObjUtils {

    private static ConvertSyncObjUtils convertSyncObjUtils;

    @NacosValue(value = "${external.default.orgId:null}", autoRefreshed = true)
    private String defaultOrganizationId;

    @NacosValue(value = "${external.convert.userJson:null}", autoRefreshed = true)
    private String userJson;

    @NacosValue(value = "${external.convert.orgJson:null}", autoRefreshed = true)
    private String orgJson;

    @Autowired
    BimHessianService bimHessianService;

    //单例模式
    public static ConvertSyncObjUtils getConvertSyncObjUtils() {
        synchronized (ConvertSyncObjUtils.class) {
            if (convertSyncObjUtils == null) {
                convertSyncObjUtils = new ConvertSyncObjUtils();
            }
            return convertSyncObjUtils;
        }
    }

    //没有这玩意,@Component中无法直接@Autowired
    @PostConstruct
    public void init() {
        convertSyncObjUtils = this;
    }

    /**
     *
     * @param objUser 用户实体
     * @param simpleType 是否简单插入，简单插入的话和hessian交互很少,更快
     * @return
     */
    public User convertUser(JSONObject objUser,Boolean simpleType) {
        User user = new User();
        JSONObject userJsonObj = JSONObject.parseObject(userJson);
        Set<Map.Entry<String, Object>> entries = userJsonObj.entrySet();
        Iterator<Map.Entry<String, Object>> entryIterator = entries.iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> entry = entryIterator.next();
            String key = entry.getKey();
            String value = (String) entry.getValue();
            Object data;
            //基础属性,写死,后续新增要改代码
            user.setUsername(objUser.getString("userLogin"));
            user.setOrganizationId(objUser.getString("oimActKey"));
            user.setFirstname(objUser.getString("firstName"));
            user.setLastname(objUser.getString("lastName"));
            user.setFullname(objUser.getString("displayName"));
            user.setPassword(objUser.getString("password"));
            if (objUser.getDate("startDate") != null) {
                user.setStartDate(new Date(objUser.getDate("startDate").getTime()));
            }
            if (objUser.getDate("endDate") != null) {
                user.setEndDate(new Date(objUser.getDate("endDate").getTime()));
            }
            user.setEmail(objUser.getString("email"));
            user.setCreateBy(objUser.getString("createBy"));
            user.setUpdateBy(objUser.getString("updateBy"));
            user.setEmployeeNo(objUser.getString("empNo"));
            user.setMobile(objUser.getString("mobile"));
            user.setGuid(objUser.getString("oimKey"));
            user.setIdentityCard(objUser.getString("nationalID"));

            //一些需要数据字段转换的属性
           if(!simpleType){
               switchUserLookUp(user, objUser);
           }
            //扩展且不需要数据字段转换的属性
            if ((data = objUser.get(key)) != null) {
                user.getData().put(value, data);
            }

        }

        if(!simpleType){
            if (user.getOrganizationId() != null) {
                SwitchUserOrgId(user);
            }
        }else {
            user.setOrganizationId(defaultOrganizationId);
        }


        return user;
    }


    public Organization convertOrg(JSONObject objOrg) {
        Organization org = new Organization();
        JSONObject orgJsonObj = JSONObject.parseObject(orgJson);
        Set<Map.Entry<String, Object>> entries = orgJsonObj.entrySet();
        Iterator<Map.Entry<String, Object>> entryIterator = entries.iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, Object> entry = entryIterator.next();
            String key = entry.getKey();
            String value = (String) entry.getValue();
            Object data;
            //基础属性,写死,后续新增要改代码
            org.setGuid(objOrg.getString("oimKey"));
            org.setName(objOrg.getString("orgName"));
            org.setCode(objOrg.getString("code"));
            org.setFullname(objOrg.getString("orgName"));
            org.setCreateBy(objOrg.getString("createBy"));
            org.setUpdateBy(objOrg.getString("updateBy"));
            org.setParentId(objOrg.getString("oimParentKey"));

            //一些需要数据字段转换的属性
            switchOrgLookUp(org, objOrg);


            //扩展且不需要数据字段转换的属性
            if ((data = objOrg.get(key)) != null) {
                org.getData().put(value, data);
            }
        }

        //parentId也可能是传org的code过来,转换一下
        if (StringUtils.isNotEmpty(org.getParentId())) {
            SwitchOrgParentId(org);
        }
        //感觉一般人不会把TreeId带上,但是不带上又会报错
        if (StringUtils.isEmpty(org.getTreeId())) {
            org.setTreeId("000000000000000000000000SORGT001");
        }

        return org;
    }

    private void switchOrgLookUp(Organization org, JSONObject objOrg) {
        //禁用状态
        String status = objOrg.getString("status");
        if (StringUtils.isNotEmpty(status)) {
            switch (status) {
                case SyncOrgConstant.ISDISABLED_ACTIVE:
                    org.setIsDisabled(false);
                    break;
                case SyncOrgConstant.ISDISABLED_INACTIVE:
                    org.setIsDisabled(true);
                    break;
                case SyncOrgConstant.ISDISABLED_DELETED:
                    org.setIsDisabled(true);
                    break;
                default:
                    org.setIsDisabled(false);
            }
        }

    }

    private void switchUserLookUp(User user, JSONObject objUser) {

        //禁用状态
        String status = objUser.getString("status");
        if (StringUtils.isNotEmpty(status)) {
            switch (status) {
                case SyncUserConstant.ISDISABLED_ACTIVE:
                    user.setIsDisabled(false);
                    break;
                case SyncUserConstant.ISDISABLED_DISABLED:
                    user.setIsDisabled(true);
                    break;
                case SyncUserConstant.ISDISABLED_DELETED:
                    user.setIsDisabled(true);
                    break;
            }
        }

        //雇佣类型
        String empType = objUser.getString("empType");
        if (StringUtils.isNotEmpty(empType)) {
            Lookup lookup = bimHessianService.getLookupByLdfCodeAndLookupCode("system.user.employee.type", empType);
            if (lookup != null) {
                user.setEmployeeType(lookup.getId());
            }
        }

        //员工类型
        String userType = objUser.getString("userType");
        if (StringUtils.isNotEmpty(userType)) {
            String userTypeId = bimHessianService.getUserTypeIdByCode(userType);
            user.setTypeId(userTypeId);
        }

        //直接上级manager
        String managerUserName = objUser.getString("manager");
        if (StringUtils.isNotEmpty(managerUserName)) {
            User manager = bimHessianService.getUserByUserName(managerUserName);
            if (manager != null) {
                user.setManagerId(manager.getId());
            }
        }

    }

    public void SwitchUserOrgId(User user) {
        //机构传guid和id都支持
        String organizationId = user.getOrganizationId();
        //机构太多了,这里就不多查一遍了
        if (StringUtils.isNotEmpty(organizationId)/* && bimHessianService.getOrgById(organizationId) == null*/) {
            Organization org = bimHessianService.getOrgByGuid(organizationId);//怀疑这个查询很慢
            if (org == null) {
                // 现在机构找不到不报错了,丢到一个临时机构下
                //  throw new RuntimeException("操作用户无法查询到对应机构信息!");
                user.setOrganizationId(defaultOrganizationId);
            } else {
                user.setOrganizationId(org.getId());
            }
        }
    }

    public void SwitchOrgParentId(Organization org) {
        //TODO
        //机构传guid和id都支持
        String parentId = org.getParentId();
        if (StringUtils.isNotEmpty(parentId) && bimHessianService.getOrgById(parentId) == null) {
            Organization parntOrg = bimHessianService.getOrgByGuid(parentId);
            if (parntOrg == null) {
                org.setParentId(null); //父机构找不到不报错,直接丢到顶级机构上
                //  throw new RuntimeException("操作机构父Id无法查询到对应机构信息!");
            } else {
                org.setParentId(parntOrg.getId());
            }
        }
    }

}
