package org.imooc.coupon.service;

import org.imooc.coupon.constant.RoleEnum;
import org.imooc.coupon.dao.PathRepository;
import org.imooc.coupon.dao.RolePathMappingRepository;
import org.imooc.coupon.dao.RoleRepository;
import org.imooc.coupon.dao.UserRoleMappingRepository;
import org.imooc.coupon.entity.Path;
import org.imooc.coupon.entity.Role;
import org.imooc.coupon.entity.RolePathMapping;
import org.imooc.coupon.entity.UserRoleMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <h1>权限校验功能服务接口实现</h1>
 * @Author: crowsjian
 * @Date: 2020/6/27 17:13
 */
@Slf4j
@Service
public class PermissionService {
    private final PathRepository pathRepository;
    private final RoleRepository roleRepository;
    private final RolePathMappingRepository rolePathMappingRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;

    public PermissionService(PathRepository pathRepository,
                             RoleRepository roleRepository,
                             RolePathMappingRepository rolePathMappingRepository,
                             UserRoleMappingRepository userRoleMappingRepository) {
        this.pathRepository = pathRepository;
        this.roleRepository = roleRepository;
        this.rolePathMappingRepository = rolePathMappingRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
    }

    /**
     * <h1>用户访问接口权限校验</h1>
     * @param userId 用户id
     * @param uri 访问 uri
     * @param httpMethod 请求类型
     * @return true/false
     */
    public Boolean checkPermission(Long userId,String uri,String httpMethod){
        UserRoleMapping userRoleMapping = userRoleMappingRepository.findByUserId(userId);
        // 如果用户角色映射表找不到记录，直接返回false
        if(null ==  userRoleMapping){
            log.error("UserId is not exist UserRoleMapping:{}",userId);
            return false;
        }
        // 如果找不到对应的 Role 记录，直接返回false
        Optional<Role> role = roleRepository.findById(userRoleMapping.getRoleId());
        if(!role.isPresent()){
            log.error("RoleId is not exist in Role:{}",userRoleMapping.getRoleId());
            return false;
        }
        // 如果用户角色是超级管理员，直接返回true
        if(role.get().getRoleTag().equals(RoleEnum.SUPER_ADMIN.name())){
            return true;
        }
        // 如果路径没有注册（忽略了），直接返回true
        Path path = pathRepository.findByPathPartternAndHttpMethod(
                uri,httpMethod
        );
        if(null == path){
            return true;
        }
        RolePathMapping rolePathMapping = rolePathMappingRepository.findByRoleIdAndPathId(
                role.get().getId(),path.getId()
        );
        return rolePathMapping!=null;
    }
}
