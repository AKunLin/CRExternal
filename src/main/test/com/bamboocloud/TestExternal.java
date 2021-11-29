package com.bamboocloud;


import com.alibaba.fastjson.JSONObject;
import com.bamboocloud.enmu.LogType;
import com.bamboocloud.im.entity.Organization;
import com.bamboocloud.im.entity.User;
import com.bamboocloud.utils.RequestCheckUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = ExternalSpringBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class TestExternal {


    public static void main(String[] args) {
        int[] nums = new int[]{4,1,2,1,2,4,5};
        int i = ifTwice(nums);
        System.out.println(i);
        // rotate(nums,500);



    //    System.out.println(containsDuplicate(nums));
    }

    private static int ifTwice(int[] nums) {
        int reduce = 0;
        for (int num : nums) {
            reduce =  reduce ^ num;
        }
        return reduce;

    }

    public static boolean containsDuplicate(int[] nums) {
        if (nums.length == 0) { //[1,2,3,4,5]
            return false;
        }
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if(!set.add(nums[i])){
                return true;
            }
        }

        return false;

    }

    public static void rotate(int[] nums, int k) {
        int length = nums.length;

        int index = (length - k % length) % length;
        if (index == 0) {
            return;
        } else {
            int[] temp_before = Arrays.copyOfRange(nums, index, length);
            int[] temp_after = Arrays.copyOfRange(nums, 0, index);
            for (int i = 0; i < temp_before.length; i++) {
                nums[i] = temp_before[i];
            }
            for (int j = temp_before.length; j < nums.length; j++) {
                nums[j] = temp_after[j - temp_before.length];
            }
        }
    }
}
