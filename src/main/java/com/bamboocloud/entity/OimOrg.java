package com.bamboocloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OimOrg implements Serializable {
    public String oimKey;
    public String orgType;
    public String orgName;
    public String code;
    public String createBy;
    public String updateBy;
    public String oimParentKey;
    public String status;
    public String companyCode;
    public String hrParentNode;
    public String setID;
    public String hrTreeNode;
    public String companyName;
}
