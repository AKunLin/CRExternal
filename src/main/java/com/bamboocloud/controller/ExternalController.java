package com.bamboocloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.bamboocloud.constant.LogCollect;
import com.bamboocloud.enmu.LogType;
import com.bamboocloud.entity.ExternalResult;
import com.bamboocloud.entity.OimOrg;
import com.bamboocloud.entity.OimUser;
import com.bamboocloud.exception.DECException;
import com.bamboocloud.fw.exception.SysException;
import com.bamboocloud.im.entity.Organization;
import com.bamboocloud.im.entity.User;
import com.bamboocloud.service.BimHessianService;
import com.bamboocloud.utils.ConvertSyncObjUtils;
import com.bamboocloud.utils.RequestCheckUtils;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.bamboocloud.constant.ExternalConstant.*;

/**
 * @author leojack
 * @message 上游调用往bim新增用户和机构的接口
 */
@RestController
@RequestMapping("/external")
@Slf4j
public class ExternalController {

    @NacosValue(value = "${external.username:null}", autoRefreshed = true)
    private String username;

    @NacosValue(value = "${external.password:null}", autoRefreshed = true)
    private String password;

    @NacosValue(value = "${external.convert.ifCheckRequest:null}", autoRefreshed = true)
    private Boolean ifCheckRequest;

    @Autowired
    BimHessianService bimHessianService;

    /**
     * 用户新增
     * @param req
     * @param data
     * @return
     */
    @PostMapping("/user/handleUser/add")
    @LogCollect(LogType.ALL)
    public ExternalResult addUser(HttpServletRequest req, @RequestBody Map<String, Object> data) {
        //直接用JSONObject 接收他会自动排序,对于后面的校验来说很坑
        JSONObject objUser = new JSONObject(data);
        User user = null;
        try {
            RequestCheckUtils.CheckRequest(req, username, password, objUser, ifCheckRequest);
            user = ConvertSyncObjUtils.getConvertSyncObjUtils().convertUser(objUser, false);
            if (user.getUsername() == null || user.getOrganizationId() == null) {
                return ExternalResult.dataLack(SYNC_USER_ADD_DATA_LAKE, user);
            }
            if (bimHessianService.getUserByUserName(user.getUsername()) != null) {
                return ExternalResult.dataExist(SYNC_USER_ADD_DATA_EXIST);
            }
            bimHessianService.addBimUser(user);
            return ExternalResult.success(user);
        } catch (DECException e) {
            return ExternalResult.rejected(e.getMessage());
        } catch (SysException e) {
            return ExternalResult.dataError(e.getMessage(), user);
        }

    }

    /**
     * 用户更新
     * @param req
     * @param data
     * @return
     */
    @PostMapping("/user/handleUser/update")
    @LogCollect(LogType.ALL)
    public ExternalResult updateUser(HttpServletRequest req, @RequestBody Map<String, Object> data) {
        //直接用JSONObject 接收他会自动排序,对于后面的校验来说很坑
        JSONObject objUser = new JSONObject(data);
        User user = null;
        try {
            RequestCheckUtils.CheckRequest(req, username, password, objUser, ifCheckRequest);
            user = ConvertSyncObjUtils.getConvertSyncObjUtils().convertUser(objUser, false);
            String username = user.getUsername();
            if (StringUtils.isEmpty(username)) {
                return ExternalResult.dataLack(SYNC_USER_UPDATE_DATA_LAKE, user);
            }
            User originUser = bimHessianService.getUserByUserName(username);
            if (originUser == null) {
                return ExternalResult.dataNotExist(SYNC_USER_UPDATE_DATA_NOTFOUND);
            }
            user.setId(originUser.getId());
            bimHessianService.updateBimUser(user);
            return ExternalResult.success(user);
        } catch (DECException e) {
            return ExternalResult.rejected(e.getMessage());
        } catch (SysException e) {
            return ExternalResult.dataError(e.getMessage(), user);
        }

    }

    /**
     * 机构新增
     * @param req
     * @param data
     * @return
     */
    @PostMapping("/org/handleOrg/add")
    @LogCollect(LogType.ALL)
    public ExternalResult addOrg(HttpServletRequest req, @RequestBody Map<String, Object> data) {
        //直接用JSONObject 接收他会自动排序,对于后面的校验来说很坑
        JSONObject objOrg = new JSONObject(data);
        Organization org = null;
        try {
            RequestCheckUtils.CheckRequest(req, username, password, objOrg, ifCheckRequest);
            org = ConvertSyncObjUtils.getConvertSyncObjUtils().convertOrg(objOrg);
            if (org.getName() == null || org.getCode() == null || org.getGuid() == null) {
                return ExternalResult.dataLack(SYNC_ORG_ADD_DATA_LAKE, org);
            }
            if (bimHessianService.getOrgByGuid(org.getGuid()) != null) {
                return ExternalResult.dataExist(SYNC_ORG_ADD_DATA_EXIST);
            }
            bimHessianService.addBimOrg(org);
            return ExternalResult.success(org);
        } catch (DECException e) {
            return ExternalResult.rejected(e.getMessage());
        } catch (SysException e) {
            return ExternalResult.dataError(e.getMessage(), org);
        }
    }

    /**
     * 机构更新
     * @param req
     * @param data
     * @return
     */
    @PostMapping("/org/handleOrg/update")
    @LogCollect(LogType.ALL)
    public ExternalResult updateOrg(HttpServletRequest req, @RequestBody Map<String, Object> data) {
        //直接用JSONObject 接收他会自动排序,对于后面的校验来说很坑
        JSONObject objOrg = new JSONObject(data);
        Organization org = null;
        try {
            RequestCheckUtils.CheckRequest(req, username, password, objOrg, ifCheckRequest);
            org = ConvertSyncObjUtils.getConvertSyncObjUtils().convertOrg(objOrg);
            String orgGuid = org.getGuid();
            if (StringUtils.isEmpty(orgGuid)) {
                return ExternalResult.dataLack(SYNC_ORG_UPDATE_DATA_LAKE, org);
            }
            Organization originOrg = bimHessianService.getOrgByGuid(org.getGuid());
            if (originOrg == null) {
                return ExternalResult.dataNotExist(SYNC_ORG_UPDATE_DATA_NOTFOUND);
            }
            org.setId(originOrg.getId());
            org.setTreeId(originOrg.getTreeId());
            bimHessianService.updateBimOrg(org);
            return ExternalResult.success(org);
        } catch (DECException e) {
            return ExternalResult.rejected(e.getMessage());
        } catch (SysException e) {
            return ExternalResult.dataError(e.getMessage(), org);
        }
    }

    /**
     * 这个我就不告诉你是啥了
     * @param username
     * @return
     * @throws Exception
     */
    @PostMapping("/user/getUserSecret")
    public ExternalResult getUserSecret(@RequestParam String username) throws Exception {
        String secret = bimHessianService.getUserSecretFromOIM(username);
        return ExternalResult.success(secret);
    }

    /**
     * 从LDAP中拿到实际的数据，用于比较是否我mapper映射写错导致某些数据未回收
     * @param key
     * @param objType
     * @return
     */
    @PostMapping("/data/getReal/{objType}")
    public ExternalResult getUserSecret(@RequestParam String key, @PathVariable String objType) {
        Map<String, Object> result;
        if("USER".equals(objType)){
            result = bimHessianService.getOIMRealUser(key);
        }else {
            result = bimHessianService.getOIMRealOrg(key);
        }
        return ExternalResult.success(JSONObject.toJSONString(result));
    }

    /**
     * 使用guava来筛出此次同步未成功的用户
     * @param objType
     * @return
     */
    @PostMapping("/data/guava/{objType}")
    public ExternalResult guavaCheck(@PathVariable String objType) {
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 1000000, 0.0001);
        log.info("开始调用Guava BloomFilter 查询 ===================>");
        if("USER".equals(objType)){
            fullUserBloom(bloomFilter);
            //开始调用hessian的接口获取bim的数据来比较
            int num = 0;
            int size = 10000;
            List<User> userList = bimHessianService.getUserByPage(num, size);
            while (true) {
                for (int i = 0; i < userList.size(); i++) {
                    User user = userList.get(i);
                    if(bloomFilter.mightContain(user.getUsername())){
                        log.info("根据BloomFilter检查结果,用户不存在:{}",user);
                    }
                }
                if(userList.size() < size) {
                    break;
                }else {
                    num++;
                }
            }
        }else {
            fullOrgBloom(bloomFilter);
            //开始调用hessian的接口获取bim的数据来比较
            int num = 0;
            int size = 10000;
            List<Organization> orgList = bimHessianService.getOrgByPage(num, size);
            while (true) {
                for (int i = 0; i < orgList.size(); i++) {
                    Organization org = orgList.get(i);
                    if(bloomFilter.mightContain(org.getCode())){
                        log.info("根据BloomFilter检查结果,机构不存在:{}",org);
                    }
                }
                if(orgList.size() < size) {
                    break;
                }else {
                    num++;
                }
            }
        }
        return ExternalResult.success(null);
    }

    private void fullOrgBloom(BloomFilter<String> bloomFilter) {
        int page = 1;
        int row = 10000;
        while (true){
            log.info("开始注入数据到BloomFilter page: {}  row:{} ===================>",page,row);
            List<OimOrg> orgList = bimHessianService.getOIMOrgByPage(page, row);
            for (int i = 0; i < orgList.size(); i++) {
                OimOrg oimOrg = orgList.get(i);
                bloomFilter.put(oimOrg.getCode());
            }
            if(orgList.size() < row){
                log.info("BloomFilter数据注入完毕 开始比较IAM中数据============>");
                break;
            }else {
                orgList.clear();
                page++;
            }
        }
    }

    private void fullUserBloom( BloomFilter<String> bloomFilter) {
        int page = 1;
        int row = 10000;
        while (true){
            log.info("开始注入数据到BloomFilter page: {}  row:{} ===================>",page,row);
            List<OimUser> userList = bimHessianService.getOIMUserByPage(page, row);
            for (int i = 0; i < userList.size(); i++) {
                OimUser oimUser = userList.get(i);
                bloomFilter.put(oimUser.getUserLogin());
            }
            if(userList.size() < row){
                log.info("BloomFilter数据注入完毕 开始比较IAM中数据============>");
                break;
            }else {
                userList.clear();
                page++;
            }
        }
    }


}
