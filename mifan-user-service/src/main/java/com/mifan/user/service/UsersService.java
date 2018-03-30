package com.mifan.user.service;

import com.mifan.user.domain.Roles;
import com.mifan.user.domain.Users;
import com.mifan.user.domain.support.PermissionContext;

import org.moonframework.model.mybatis.service.BaseService;
import org.moonframework.security.authentication.PermissionControl;

import java.util.List;
import java.util.Set;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/26
 */
public interface UsersService extends BaseService<Users> {

    /**
     * <p>添加用户到群组</p>
     *
     * @param siteId   站点ID
     * @param userId   用户ID
     * @param groupIds 用户组ID集合
     * @return 
     */
    int addGroups(Long siteId, Long userId, List<Long> groupIds);

    /**
     * <p>添加用户与角色关联关系</p>
     *
     * @param siteId  站点ID
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     * @return 
     */
    int addRoles(Long siteId, Long userId, List<Long> roleIds);

    /**
     * <p>查找用户角色集合</p>
     *
     * @param username 用户名称
     * @return Set
     */
    Set<String> findRoles(String username);

    /**
     * <p>查找用户角色集合</p>
     *
     * @param userId 用户ID
     * @return Set
     */
    Set<String> findRoles(Long userId);

    /**
     * <p>查找用户角色集合</p>
     *
     * @param siteId   站点ID
     * @param username 用户名称
     * @return Set
     */
    Set<Roles> findRoles(Long siteId, String username);

    /**
     * <p>查找用户角色集合</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @return Set
     */
    Set<Roles> findRoles(Long siteId, Long userId);

    /**
     * <p>查找用户角色集合</p>
     *
     * @param appKey   APP KEY
     * @param username 用户账号
     * @return Roles
     */
    Set<Roles> findRoles(String appKey, String username);

    /**
     * <p>查找用户角色集合</p>
     *
     * @param appKey APP KEY
     * @param userId 用户ID
     * @return Set
     */
    Set<Roles> findRoles(String appKey, Long userId);

    /**
     * <p>查找用户权限集合</p>
     *
     * @param siteId       站点ID
     * @param username     用户名称
     * @param defaultRoles 默认角色
     * @return Set
     */
    PermissionContext findPermissions(Long siteId, String username, Set<String> defaultRoles);

    /**
     * <p>查找用户权限集合</p>
     *
     * @param siteId       站点ID
     * @param userId       用户ID
     * @param defaultRoles 默认角色
     * @return Set
     */
    PermissionContext findPermissions(Long siteId, Long userId, Set<String> defaultRoles);

    /**
     * @param appKey       appKey
     * @param username     用户名称
     * @param defaultRoles 默认角色
     * @return PermissionContext
     */
    PermissionContext findPermissions(String appKey, String username, Set<String> defaultRoles);

    /**
     * @param appKey       appKey
     * @param userId       用户ID
     * @param defaultRoles 默认角色
     * @return PermissionContext
     */
    PermissionContext findPermissions(String appKey, Long userId, Set<String> defaultRoles);

    /**
     * <p>将用户权限上下文转换成可以用于使用的方式</p>
     *
     * @param context context
     * @return PermissionControl
     */
    PermissionControl doPermission(PermissionContext context);

    /**
     * <p>根据用户名查找用户</p>
     *
     * @param username 用户账户
     * @return Users
     */
    Users findUserByUsername(String username);

    /**
     * <p>激活用户账号</p>
     *
     * @param username 用户账号
     * @return int
     */
    int activation(String username);

    /**
     * <p>用户登录时修改用户的基本信息</p>
     *
     * @param id 用户ID
     * @return int
     */
    int updateOnLogin(Long id);

    /**
     * <p>根据用户账号, 查找用户相关信息</p>
     * <ol>
     * <li>id:</li>
     * <li>created:</li>
     * <li>enabled:</li>
     * </ol>
     *
     * @param username 用户账号
     * @return 用户信息
     */
    Users findUserInfo(String username);

    /**
     * 根据unionid查找用户相关信息
     *
     * @param unionid
     * @return
     */
    /*Users findUserInfoByUnionid(String unionid);*/

    /*boolean sendVerifyCode(String phoneNum, SpecType sp);*/
    

    /**
     * 主账号与第三方帐号绑定
     *
     * @param username   绑定帐号用户名(主账号)
     * @param wxusername 被绑定帐号用户名(微信号)
     * @return 主账号id
     */
    Long userBind(String username, String wxusername);
    
    /**
     * 根据用户名判断用户是否存在
     * @param username
     * @return
     */
    boolean exists(String username);
    /**
     * 修改密码
     * @param username
     * @param password
     * @return
     */
    int updatePassword(String username,String password);

}
