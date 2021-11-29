package com.bamboocloud.constant;

/**
 * @author leojack
 * @message 回收用户的一些数据字典
 */
public interface SyncUserConstant {

    //禁用
    String ISDISABLED_ACTIVE = "Active"; //活动
    String ISDISABLED_DISABLED = "Disabled"; //禁用
    String ISDISABLED_DELETED= "Deleted"; //删除

    //员工类型
    String EMPTYPE_TEMP = "Temp"; //临时
    String EMPTYPE_FULL_TIME = "Full-Time"; //内部员工
    String EMPTYPE_OTHER = "OTHER"; //特殊账号
    String EMPTYPE_CONTRACTOR = "Contractor"; //外部账号

    //用户类型
    String USERTYPE_END_USER = "End-User";
    String USERTYPE_ADMIN = "Administrator";

}
