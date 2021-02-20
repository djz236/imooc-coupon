package org.imooc.coupon.dao;

import org.imooc.coupon.entity.Path;
import org.imooc.coupon.entity.UserRoleMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Author: crowsjian
 * @Date: 2020/7/1 22:53
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PathRepositoryTest {
    @Autowired
    private PathRepository pathRepository;

    @Autowired
    private UserRoleMappingRepository userRoleMappingRepository;

    @Test
    public void test(){
        List<Path> pathList = pathRepository.findAllByServiceName("1111");
    }

    @Test
    public void test2(){
        UserRoleMapping userRoleMapping = userRoleMappingRepository.findByUserId(16L);
    }
}