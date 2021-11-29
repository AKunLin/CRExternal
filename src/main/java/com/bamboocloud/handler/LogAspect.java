package com.bamboocloud.handler;

import com.alibaba.fastjson.JSONObject;
import com.bamboocloud.constant.LogCollect;
import com.bamboocloud.enmu.LogType;
import com.bamboocloud.entity.EcsbResult;
import com.bamboocloud.entity.ExternalResult;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import java.util.Arrays;
import java.util.List;

import static com.bamboocloud.constant.ExternalConstant.*;

/**
 * @author leojack
 * @message 实现通过注解来对方法API的正常返回, 异常返回, 进行日志模板输出和采集
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 扫描所有controller包下的请求
     * 使用路径作为切点会使得该路径下的所有接口都被拦截处理，不够灵活
     * 但是该种方式不需要额外自定义枚举和注解类
     */
/*    @Pointcut("execution(* com.bamboocloud.controller.*.*(..))")
    public void controllerAspect() {
    }*/

    /**
     * 定义拦截器的切入点，拦截注解的方法
     * 把枚举类作为切入点，可以根据业务需求调整，更灵活
     */
    @Pointcut("@annotation(com.bamboocloud.constant.LogCollect)")
    public void annotationAspect() {
    }

    /**
     * 具体实现功能，使用环绕通知获取参数和返回数据
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @AfterReturning
    @Around("annotationAspect()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        //拦截方法参数
        Object[] args = joinPoint.getArgs();
        argsFilter(args);
        String Data = JSONObject.toJSONString(args);

        //获取注解中的信息
        List<LogType> logTypeList = getLogTypeList(joinPoint);
        String methodName = joinPoint.getSignature().getName();   //拦截方法名称
        if (ifContain(logTypeList,LogType.WAITTING)) {
            log.info(LOG_BASE_SYNC, methodName, LOG_WAITTING, null, null, Data);
        }
        //执行方法
        Object obj = joinPoint.proceed();
        long end = System.currentTimeMillis();
        long executeTime = end - start;

        Boolean requestStatus = ifSuccess(obj);

        if (ifContain(logTypeList,LogType.SUCCESS) && requestStatus != null && requestStatus) {
            log.info(LOG_BASE_SYNC, methodName, LOG_SUCCESS, executeTime, null, Data);
        } else if (ifContain(logTypeList,LogType.FAILURE) && requestStatus != null) {
            log.info(LOG_BASE_SYNC, methodName, LOG_FAILURE, executeTime, null, Data);
        }

        if (ifContain(logTypeList,LogType.FINSIH)) {
            log.info(LOG_BASE_SYNC, methodName, LOG_FINISH, executeTime, null, Data);
        }

        return obj;
    }

    /**
     * 过滤带HttpServletRequest 的参数,这些没办法转成JSON的
     * @param args
     */
    private void argsFilter(Object[] args) {

        for (int i = 0; i < args.length; i++) {
            if( args[i] instanceof ServletRequest){
                args[i] = null;
            }
        }

    }

    private List<LogType> getLogTypeList(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogCollect logCollect = signature.getMethod().getAnnotation(LogCollect.class);
        LogType[] logTypes = logCollect.value();
        List<LogType> logTypeList = Arrays.asList(logTypes);
        return logTypeList;
    }

    /**
     * 判断成功与失败的返回条件在这里定义
     *
     * @param obj
     * @return
     */
    private Boolean ifSuccess(Object obj) {
        if (obj instanceof ExternalResult) {
            return ((ExternalResult) obj).getCode() == ExternalResult.SUCCESS;
        }else if(obj instanceof EcsbResult) {
            return  EcsbResult.SUCCESS.equals(((EcsbResult) obj).getRETURN_CODE());
        }
        if(obj instanceof EcsbResult){
            return (EcsbResult.SUCCESS).equals(((EcsbResult) obj).getRETURN_CODE());
        }
        return null;
    }

    private Boolean ifContain(List<LogType> logList ,LogType log) {
        if(logList.contains(log)){
            return true;
        }
        if(logList.contains(LogType.ALL)){
            return true;
        }
        return false;
    }

    @AfterThrowing(pointcut = "annotationAspect()", throwing = "ex")
    private void doAfterThrow(JoinPoint joinPoint, Throwable ex) {
        //获取注解中的信息
        List<LogType> logTypeList = getLogTypeList(joinPoint);
        //拦截方法参数
        Object[] args = joinPoint.getArgs();
        argsFilter(args);
        String JSONArgs = JSONObject.toJSONString(joinPoint.getArgs());
        //获取方法名
        String methodName = joinPoint.getSignature().getName();
        if (ifContain(logTypeList,LogType.ERROR)) {
            log.error(LOG_BASE_SYNC, methodName, SYSTEM_ERROR, null, ex.getCause(), JSONArgs);
        }
    }

}
