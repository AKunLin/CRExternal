package com.bamboocloud.constant;

/**
 * @author leojack
 * @message 为上游回收定义的一些常量
 */
public interface ExternalConstant {
     String LOG_BASE_SYNC = "操作方法:{} 结果:{} 耗时(毫秒):{} 原因:{} 实体:{}";
     String LOG_BASE_AUTH = "接口鉴权失败,待加密字符串: {} 待对比签名: {}";
     String LOG_SUCCESS = "成功";
     String LOG_FAILURE = "失败";
     String LOG_WAITTING = "待定";
     String LOG_FINISH = "完成";
     String SYSTEM_ERROR = "系统内部异常";

     //用户同步过程中定义的一些错误constant
     String SYNC_USER_ADD_DATA_LAKE = "新增过程中,用户名和机构Id为必传字段!";
     String SYNC_USER_UPDATE_DATA_LAKE = "更新过程中,用户名为必传字段!";
     String SYNC_USER_ADD_DATA_EXIST = "根据用户名检索用户已存在!";
     String SYNC_USER_UPDATE_DATA_NOTFOUND = "更新过程中,根据用户名无法查询到对应用户!";

     //机构同步过程中定义的一些错误constant
     String SYNC_ORG_ADD_DATA_LAKE = "新增过程中,机构名称,guid,code为必传字段!";
     String SYNC_ORG_UPDATE_DATA_LAKE = "更新过程中,机构guid为必传字段!";
     String SYNC_ORG_ADD_DATA_EXIST = "根据机构guid检索机构已存在!";
     String SYNC_ORG_UPDATE_DATA_NOTFOUND = "更新过程中,根据机构guid无法查询到对应机构!";

}
