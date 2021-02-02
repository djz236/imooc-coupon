package com.imooc.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <h1>角色(Role)与路径(Path) 映射关系实体类</h1>
 * @Author: crowsjian
 * @Date: 2020/6/26 18:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_role_path_mapping")
public class RolePathMapping {
    /*主键*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Integer id;

    /*Role 表主键*/
    @Basic
    @Column(name = "role_id",nullable = false)
    private Integer roleId;

    /*Path 表主键*/
    @Basic
    @Column(name = "path_id",nullable = false)
    private Integer pathId;
}
