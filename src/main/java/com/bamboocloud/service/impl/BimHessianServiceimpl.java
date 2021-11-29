package com.bamboocloud.service.impl;

import Thor.API.Security.XLClientSecurityAssociation;
import com.bamboocloud.constant.LogCollect;
import com.bamboocloud.enmu.LogType;
import com.bamboocloud.entity.OimOrg;
import com.bamboocloud.entity.OimUser;
import com.bamboocloud.fw.object.pagination.Page;
import com.bamboocloud.fw.object.search.SearchOperator;
import com.bamboocloud.fw.object.search.Searchable;
import com.bamboocloud.fw.remote.hessian.HessianClient;
import com.bamboocloud.im.entity.*;
import com.bamboocloud.im.service.*;
import com.bamboocloud.oracle.OimDataMapper;
import com.bamboocloud.service.BimHessianService;
import com.thortech.xl.dataaccess.tcDataBaseClient;
import com.thortech.xl.dataaccess.tcDataProvider;
import com.thortech.xl.dataaccess.tcDataSet;
import lombok.extern.slf4j.Slf4j;
import oracle.iam.platform.OIMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * BimHessianService实现类
 *
 * @author luaku
 * @date 2021/10/26
 */

@Service
@Slf4j
public class BimHessianServiceimpl implements BimHessianService {

    @Autowired
    private HessianClient bimServiceHessianClient;

    @Autowired
    private OimDataMapper oimDataMapper;

    @Override
    public void addBimUser(User user) {
        bimServiceHessianClient.getService(UserService.class).insert(user);
    }

    @Override
    public void updateBimUser(User user) {
        bimServiceHessianClient.getService(UserService.class).update(user);
    }

    @Override
    public User getUserByUserName(String username) {
        User user = bimServiceHessianClient.getService(UserService.class).getByUsername(username.toLowerCase(), true);
        return user;
    }

    @Override
    public void addBimOrg(Organization org) {
        bimServiceHessianClient.getService(OrganizationService.class).insert(org);

    }

    @Override
    public void updateBimOrg(Organization org) {
        bimServiceHessianClient.getService(OrganizationService.class).update(org);

    }

    @Override
    public Organization getOrgById(String orgId) {
        Organization org = bimServiceHessianClient.getService(OrganizationService.class).getById(orgId);
        return org;
    }

    @Override
    public Organization getOrgByCode(String code) {
        Organization org = bimServiceHessianClient.getService(OrganizationService.class).getByCode("000000000000000000000000SORGT001", code, true);
        return org;
    }

    @Override
    public Organization getOrgByGuid(String guid) {
        Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter(Organization.DP.guid.name(), SearchOperator.eq, guid);
        //允许查出禁用的
        searchable.addSearchFilter(Organization.DP.isDeleted.name(), SearchOperator.eq, "0");
        List<Organization> orgList = bimServiceHessianClient.getService(OrganizationService.class).extendFindBy("000000000000000000000000SORGT001", searchable, false, false, true, false);
        if (orgList.size() != 1) {
            return null;
        }
        return orgList.get(0);
    }

    @Override
    public Lookup getLookupByLdfCodeAndLookupCode(String defCode, String lookupCode) {
        LookupDefinition ldf = bimServiceHessianClient.getService(LookupDefinitionService.class).getByCode(defCode, true);
        if (ldf == null) {
            return null;
        }
        Lookup lookup = bimServiceHessianClient.getService(LookupService.class).getByCode(ldf.getId(), lookupCode, true);
        return lookup;
    }

    @Override
    public String getUserTypeIdByCode(String code) {
        //无法查出禁用的
        UserType userType = bimServiceHessianClient.getService(UserTypeService.class).getByCode(code, true);
        if (userType == null) {
            return null;
        }
        return userType.getId();
    }

    @Override
    public String getUserSecretFromOIM(String username) throws Exception {
        tcDataProvider dbProvider = null;
        OIMClient oimClient = null;
        try {
            String ctxFactory = "weblogic.jndi.WLInitialContextFactory"; // WebLogic Context
            String oimServerURL = "t3://10.0.62.46:14000"; // OIM URL
            String authwlConfigPath = "/app/services/external/authwl.conf"; // Path to login configuration
            String admin = "xelsysadm"; // OIM Administrator
            String password = "Uat..123"; // Administrator Password
            System.setProperty("java.security.auth.login.config", authwlConfigPath); // set the login configuration
            Hashtable<String, String> env = new Hashtable<>(); // use to store OIM environment properties
            env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, ctxFactory);
            env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, oimServerURL);
            oimClient = new OIMClient(env);
            oimClient.login(admin, password.toCharArray()); // login to OIM
            XLClientSecurityAssociation.setClientHandle(oimClient);// Needed for database client
            dbProvider = new tcDataBaseClient(); // Connection to OIM Schema
            tcDataSet dataSet = new tcDataSet(); // Stores the result set of an executed query
            String query = "SELECT * FROM USR where usr_login='%s'"; // Query Users table
            query = String.format(query,username);
            dataSet.setQuery(dbProvider, query); // Set query and database provider
            dataSet.executeQuery(); // execute query and store results into dataSet object
            int records = dataSet.getTotalRowCount(); // Get total records from result set
            if (records > 1) {
                return null;
            }
            dataSet.goToRow(0); // move pointer to next record
            String plainTextSecret = dataSet.getString("USR_PASSWORD");
            String userLogin = dataSet.getString("USR_LOGIN");
            String userStatus = dataSet.getString("USR_STATUS");
            return plainTextSecret;

        } finally {
            dbProvider.close();
            XLClientSecurityAssociation.clearThreadLoginSession();
            oimClient.logout();
        }
    }

    @Override
    public List<OimUser> getOIMUserByPage(int page, int row) {
        if(page <= 0) {
            throw new RuntimeException("Page Size Must More Than 0");
        }
        List<OimUser> userByPage = oimDataMapper.getUserByPage(page * row + 1, (page - 1) * row);
        return userByPage;
    }

    @Override
    public List<OimOrg> getOIMOrgByPage(int page, int row) {
        if(page <= 0) {
            throw new RuntimeException("Page Size Must More Than 0");
        }
        List<OimOrg> orgByPage = oimDataMapper.getOrgByPage(page * row + 1, (page - 1) * row);
        return orgByPage;
    }

    @Override
    public Integer getOIMUserSize() {
        return oimDataMapper.getUserSize();
    }

    @Override
    public Integer getOIMOrgSize() {
        return oimDataMapper.getOrgSize();
    }

    @Override
    public Map<String, Object> getOIMRealUser(String username) {
        return oimDataMapper.getOIMRealUser(username);
    }

    @Override
    public Map<String, Object> getOIMRealOrg(String actKey) {
        return oimDataMapper.getOIMRealOrg(actKey);
    }

    @Override
    public List<User> getUserByPage(int pageNum,int pageSize) {
        Searchable searchable = Searchable.newSearchable();
        searchable.addPage(pageNum,pageSize);
        Page<User> pageUser = bimServiceHessianClient.getService(UserService.class).page(searchable,false);
        return pageUser.getContent();
    }

    @Override
    public List<Organization> getOrgByPage(int pageNum,int pageSize) {
        Searchable searchable = Searchable.newSearchable();
        searchable.addPage(pageNum,pageSize);
        Page<Organization> pageOrganization = bimServiceHessianClient.getService(OrganizationService.class).page(searchable,false);
        return pageOrganization.getContent();
    }
}
