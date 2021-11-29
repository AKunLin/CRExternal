package com.bamboocloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OimUser implements Serializable {
    public String userLogin;
    public String oimActKey;
    public String oimKey;
    public String nationalID;
    public String firstName;
    public String lastName;
    public String displayName;
    public String userType;
    public String password;
    public String email;
    public String status;
    public String empType;
    public String manager;
    public String createBy;
    public String updateBy;
    public String empNo;

    public String commonName;
    public String description;
    public String telephoneNumber;
    public String ldapGUID;
    public String ldapDN;
    public String buID;
    public String supporterDept;
    public String deptID;
    public String lastNameCHN;
    public String firstNameCHN;
    public String jobCode;
    public String setID;
    public String nationalIDLast4;
    public String supporterCorpname;
    public String secEmail;
    public String priEmail;

    public String mobile;
    public Date startDate;
    public Date endDate;
    public Date hireDate;
    public Date pwdExpireDate;
}
