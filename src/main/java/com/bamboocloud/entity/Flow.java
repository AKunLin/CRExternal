package com.bamboocloud.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

/**
 * flow实体类
 * 流程平台flow用户内bo_eu_ldap_flow表对应实体类
 * @author luaku
 * @date 2021/10/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("bo_eu_ldap_flow")
public class Flow implements Serializable {
    @TableId
    String id;
    @TableField("ORGID")
    String orgId;
    @TableField("BINDID")
    String bindId;
    @TableField("CREATEDATE")
    Date createDate;
    @TableField("CREATEUSER")
    String createUser;
    @TableField("UPDATEDATE")
    Date updateDate;
    @TableField("UPDATEUSER")
    String updateUser;
    @TableField("PROCESSDEFID")
    String processdefId;
    @TableField("ISEND")
    int isEnd;
    @TableField("USER_NAME")
    String username;
    @TableField("LDAP_ACCOUT")
    String ldapaccout;
    @TableField("EMAIL")
    String email;
    @TableField("TELPHONE")
    String telphone;
    @TableField("VALIDITY")
    String validity;
    @TableField("IDCARD")
    String idCard;
    @TableField("COMPANY_NAME")
    String companyName;
    @TableField("APPLYTIME")
    Date applyTime;
    @TableField("ACTING_NAME")
    String actingName;
    @TableField("UNITS_NAME")
    String unitsName;
    @TableField("ACTING_LDAPACC")
    String actingLdapAcc;
}
