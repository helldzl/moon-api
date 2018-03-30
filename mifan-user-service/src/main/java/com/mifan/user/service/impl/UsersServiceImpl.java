package com.mifan.user.service.impl;

import com.mifan.user.dao.*;
import com.mifan.user.domain.*;
import com.mifan.user.domain.support.PermissionContext;
import com.mifan.user.service.AuthoritiesService;
import com.mifan.user.service.SitesService;
import com.mifan.user.service.UsersService;
import com.mifan.user.type.AccountType;
import com.mifan.user.type.AuthType;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.model.mybatis.util.EntityClassifier;
import org.moonframework.security.authentication.Authority;
import org.moonframework.security.authentication.PermissionControl;
import org.moonframework.security.authentication.Role;
import org.moonframework.security.core.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/26
 */
@Service
public class UsersServiceImpl extends AbstractBaseService<Users, UsersDao> implements UsersService, AuthorizationService {

    @Autowired
    private SitesService sitesService;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Autowired
    private UserAccountsDao userAccountsDao;

    @Autowired
    private UserAliasesDao userAliasesDao;

    @Autowired
    private RolesDao rolesDao;
    
    @Autowired
    private UsersGroupsDao usersGroupsDao;
    
    @Autowired
    private UsersRolesDao usersRolesDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public final static String WX_USERNAME_SUFFIX = "@budeeweixin.com";//根据之前的设计，username必须是手机或者邮箱，现在用openid当作username，需要加上这个后缀

    /**
     * <p>新增用户: 用户表(users), 用户账户表(accounts), 用户信息表(user_profiles)</p>
     *
     * @param entity user entity
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends Users> int save(S entity) {
        // 账户格式检查
        String username = entity.getUsername().trim();
        int accountType = -1;
        for (AccountType type : AccountType.values()) {
            if (matches(type.getRegexp(), username)) {
                accountType = type.getIndex();
                break;
            }
        }
        isTrue(accountType > -1, "Error.Users.save.username");

        // 检查账号表是否存在
        UserAccounts userAccounts = new UserAccounts();
        userAccounts.setAccount(username); // this method also generate a CRC32 value for fast searching result in db.
        isTrue(!userAccountsDao.exists(userAccounts), "Error.Accounts.save.account");

        // 保存用户表
        Date date = new Date();
        entity.setPassword(passwordEncoder.encode(entity.getPassword().trim()));
        entity.setCreated(date);
        entity.setModified(date);
        isTrue(baseDao.save(entity) > 0, "Error.Users.save");

        // 保存账户表
        userAccounts.setUserId(entity.getId());
        userAccounts.setAccountType(accountType);
        userAccounts.setCreated(date);
        userAccounts.setModified(date);
        if (accountType == AccountType.WE_CHAT.getIndex()) {
            userAccounts.setOpenid(entity.getUserAccount().getOpenid());
        }
        isTrue(userAccountsDao.save(userAccounts) > 0, "Error.Accounts.save");

        // 保存用户信息表, 一对一关系, 如果不存在此表预先生成
        UserProfiles userProfile = entity.getUserProfile();
        if (userProfile == null) {
            userProfile = new UserProfiles(entity.getId());
        }
        if (userProfile.getNickname() == null) {
            userProfile.setNickname("米饭星" + entity.getId());
        }
        userProfile.setId(entity.getId());
        userProfile.setCreated(date);
        userProfile.setModified(date);
        if(accountType == AccountType.MOBILE.getIndex()) {
            userProfile.setMobile(username);
        }
        // TODO 其他属性设置
        isTrue(Services.save(UserProfiles.class, userProfile) > 0, "Error.UserProfiles.save");

        UserKarmaLogs userKarmaLogs = new UserKarmaLogs();
        userKarmaLogs.setUserId(entity.getId());
        userKarmaLogs.setAction(UserKarmaLogs.Event.REGISTRATION);
        Services.save(UserKarmaLogs.class, userKarmaLogs);

        return 1;
    }

    @Override
    public Long userBind(String username, String wxusername) {
        username = username.trim();
        wxusername = wxusername.trim();
        int accountType = 100;
        for (AccountType type : AccountType.values()) {
            if (matches(type.getRegexp(), username)) {
                accountType = type.getIndex();
                break;
            }
        }
        isTrue(accountType == AccountType.MOBILE.getIndex() && matches(AccountType.WE_CHAT.getRegexp(), wxusername), "Error.Accounts.type");//判断帐号类型是否正确

        Users user = findUserByUsername(username);
        Users wxuser = findUserByUsername(wxusername);
        isTrue(user != null && wxuser != null, "Account.not.exist");//验证两个帐号是否都存在

        UserAccounts wxuserAccount = new UserAccounts();
        wxuserAccount.setAccount(wxusername);
        wxuserAccount = userAccountsDao.findOne(wxuserAccount);
        isTrue(wxuserAccount.getIsbind() == 0, "Other.account.have.been.bound");//判断第三方帐号是否已经被绑定

        Date date = new Date();
        wxuserAccount.setUserId(user.getId());
        wxuserAccount.setModified(date);
        wxuserAccount.setIsbind(1);
        userAccountsDao.update(wxuserAccount);

        UserAliases userAliases = new UserAliases();
        userAliases.setUserId(user.getId());
        userAliases.setAliasUserId(wxuser.getId());
//        isTrue(Services.save(UserAliases.class, userAliases) > 0, "Error.useraliases.save");
        isTrue(userAliasesDao.save(userAliases) > 0, "Error.useraliases.save");

        return user.getId();
    }


    /**
     * @param entity user entity
     * @param <S>    S
     * @return int
     */
    @Override
    public <S extends Users> int update(S entity) {
        String newPassword = entity.getPassword();
        String oldPassword = entity.getOldPassword();
        int n = 0;
        if (newPassword != null && oldPassword != null) {
            // 修改密码
            Users user = findOne(entity.getId(), Fields.builder().add(Users.ID).add(Users.PASSWORD).build());
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new IllegalStateException("原始密码错误!");
            }

            user.setPassword(passwordEncoder.encode(newPassword.trim()));
            n = super.update(user);
        } else {
            // 修改其他信息
            entity.setUsername(null);
            entity.setPassword(null);
            n += super.update(entity);
        }

        UserProfiles userProfile = entity.getUserProfile();
        if (userProfile != null) {
            userProfile.setId(entity.getId());
            n += Services.update(UserProfiles.class, userProfile);
        }
        return n;
    }

    @Override
    public Set<String> findRoles(String username) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).build());

        notNull(userAccount);
        return findRoles(userAccount.getUserId());
    }

    @Override
    public int addGroups(Long siteId, Long userId, List<Long> groupIds) {
        try {
            // 根据siteId查找用户的所有角色
            List<UsersGroups> primary = usersGroupsDao.findAll(siteId, userId);

            //
            List<UsersGroups> secondary = new ArrayList<>();
            groupIds.forEach(groupId -> secondary.add(new UsersGroups(userId, groupId)));

            // execute
            EntityClassifier<UsersGroups> classifier = new EntityClassifier<>(primary, secondary, "groupId");
            int[] n1 = usersGroupsDao.delete(classifier.getDeletes());
            int[] n2 = usersGroupsDao.save(classifier.getInserts());
            return result(n1) | result(n2);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public int addRoles(Long siteId, Long userId, List<Long> roleIds) {
        try {
            // 根据siteId查找用户的所有角色
            List<UsersRoles> primary = usersRolesDao.findAll(siteId, userId);

            //
            List<UsersRoles> secondary = new ArrayList<>();
            roleIds.forEach(roleId -> secondary.add(new UsersRoles(userId, roleId)));

            // execute
            EntityClassifier<UsersRoles> classifier = new EntityClassifier<>(primary, secondary, "roleId");
            int[] n1 = usersRolesDao.delete(classifier.getDeletes());
            int[] n2 = usersRolesDao.save(classifier.getInserts());
            return result(n1) | result(n2);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Set<String> findRoles(Long userId) {
        // TODO
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Roles> findRoles(Long siteId, String username) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).build());

        if (userAccount == null || userAccount.getUserId() == null) {
            return Collections.emptySet();
        }
        return roles(siteId, userAccount.getUserId());
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Roles> findRoles(Long siteId, Long userId) {
        return roles(siteId, userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Roles> findRoles(String appKey, String username) {
        Long siteId = sitesService.findSiteIdByAppKey(appKey);
        notNull(siteId, "Error.sites.findSiteIdByAppKey");

        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).build());

        if (userAccount == null || userAccount.getUserId() == null) {
            return Collections.emptySet();
        }
        return roles(siteId, userAccount.getUserId());
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Roles> findRoles(String appKey, Long userId) {
        Long siteId = sitesService.findSiteIdByAppKey(appKey);
        notNull(siteId, "Error.sites.findSiteIdByAppKey");
        return roles(siteId, userId);
    }

    @Transactional(readOnly = true)
    @Override
    public PermissionContext findPermissions(Long siteId, String username, Set<String> defaultRoles) {
        UserAccounts account = account(username);
        if (account == null) {
            return null;
        }
        return findPermissions(siteId, account.getUserId(), defaultRoles);

    }

    @Transactional(readOnly = true)
    @Override
    public PermissionContext findPermissions(Long siteId, Long userId, Set<String> defaultRoles) {
        // user roles
        Set<Roles> roles = roles(siteId, userId);

        // default roles
        if (!CollectionUtils.isEmpty(defaultRoles)) {
            Criterion criterion = Restrictions.and(
                    Restrictions.eq(Roles.SITE_ID, siteId),
                    Restrictions.eq(Roles.ENABLED, 1),
                    Restrictions.in(Roles.NAME, defaultRoles.toArray())
            );
            roles.addAll(rolesDao.findAll(criterion));
        }

        // all authorities
        Set<Long> roleIds = roles.stream().map(Roles::getId).collect(Collectors.toSet());
        Set<Authorities> authorities = authoritiesService.findAuthoritiesByRoles(roleIds);

        // security context
        return new PermissionContext(siteId, userId, roles, authorities);
    }

    @Transactional(readOnly = true)
    @Override
    public PermissionContext findPermissions(String appKey, String username, Set<String> defaultRoles) {
        UserAccounts account = account(username);
        if (account == null) {
            return null;
        }
        return findPermissions(appKey, account.getUserId(), defaultRoles);
    }

    @Transactional(readOnly = true)
    @Override
    public PermissionContext findPermissions(String appKey, Long userId, Set<String> defaultRoles) {
        Long siteId = sitesService.findSiteIdByAppKey(appKey);
        notNull(siteId, "Error.sites.findSiteIdByAppKey");
        return findPermissions(siteId, userId, defaultRoles);
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public PermissionControl doPermission(PermissionContext context) {
        if (context == null) {
            return null;
        }
        PermissionControl control = new PermissionControl(context.getSiteId(), context.getUserId());
        context.getRoles().forEach(role -> control.add(new Role(role.getId(), role.getName())));
        context.getAuthorities().forEach(authority -> {
            AuthType authType = AuthType.fromIndex(authority.getAuthType());
            if (authType != null) {
                Authority auth = new Authority(authority.getId(), authority.getName(), authority.getPermission());
                auth.setPriority(authority.getPriority());
                control.add(auth);
            }
        });
        return control;
    }

    @Transactional(readOnly = true)
    @Override
    public Users queryForObject(Long userId) {
        Users user = baseDao.findOne(userId);
        if (user == null) {
            return null;
        }
        user.setUserProfile(Services.findOne(UserProfiles.class, userId));
        UserAccounts userAccount = new UserAccounts();
        userAccount.setUserId(userId);
        user.setAccounts(userAccountsDao.findAll(userAccount));
        return user;
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public PermissionControl doGetAuthorizationInfo(String appKey, Long userId) {
        return doPermission(findPermissions(appKey, userId, null));
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public PermissionControl doGetAuthorizationInfo(String appKey, String username) {
        return doGetAuthorizationInfo(appKey, username, null);
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public PermissionControl doGetAuthorizationInfo(String appKey, Long userId, Set<String> defaultRoles) {
        return doPermission(findPermissions(appKey, userId, defaultRoles));
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public PermissionControl doGetAuthorizationInfo(String appKey, String username, Set<String> defaultRoles) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).build());

        if (userAccount == null || userAccount.getUserId() == null) {
            return null;
        }
        return doGetAuthorizationInfo(appKey, userAccount.getUserId(), defaultRoles);
    }

    @Override
    public Users findUserByUsername(String username) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).build());

        if (userAccount == null || userAccount.getUserId() == null) {
            return null;
        }

        Users user = new Users();
        user.setId(userAccount.getUserId());
        return baseDao.findOne(user, Fields.builder().add(Users.ID).add(Users.PASSWORD).add(Users.PWD_TYPE).add(Users.ENABLED).add(Users.CREATED).build());
    }

    @Override
    public int activation(String username) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).build());

        if (userAccount == null || userAccount.getUserId() == null) {
            return 0;
        }

        Users user = new Users();
        user.setId(userAccount.getUserId());
        user.setEnabled(1);
        return baseDao.update(user);
    }

    @Override
    public int updateOnLogin(Long id) {
        baseDao.increase(id);
        UserProfiles userProfile = new UserProfiles(id);
        userProfile.setUserLastvisit(new Date());
        return Services.update(UserProfiles.class, userProfile);
    }

    @Override
    public Users findUserInfo(String username) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).add(UserAccounts.CREATED).build());

        if (userAccount == null || userAccount.getUserId() == null) {
            return null;
        }

        Users user = baseDao.findOne(userAccount.getUserId(), Fields.builder().add(Users.ID).add(Users.CREATED).add(Users.ENABLED).build());
        if (user == null) {
            return null;
        }

        user.setUserAccount(userAccount);
        // find profile
        return user;
    }

    /*@Override
    public Users findUserInfoByUnionid(String unionid) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setUnionid(unionid);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).add(UserAccounts.CREATED).build());
        if (userAccount == null || userAccount.getUserId() == null)
            return null;

        Users user = baseDao.findOne(userAccount.getUserId(), Fields.builder().add(Users.ID).add(Users.USERNAME).add(Users.CREATED).add(Users.ENABLED).build());
        if (user == null)
            return null;

        user.setUserAccount(userAccount);
        return user;
    }*/

    // TODO move to SmsService
    /*@Override
    public boolean sendVerifyCode(String phoneNum, SpecType sp) {
        String smsCode = genVerifyCode();
        // 发给客户手机
        if (!sendForMobile(phoneNum, smsCode, sp)) {
            logger.error("发送验证码短信错误：" + phoneNum + ", smsCode:" + smsCode + ", sp:" + sp);
            return false;
        }
        this.redisTemplate.opsForValue().set(getKey(phoneNum,sp), smsCode, 600, TimeUnit.SECONDS);
        return true;
    }*/

    /**
     * <p>查找用户账户信息</p>
     *
     * @param username username
     * @return account
     */
    private UserAccounts account(String username) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        userAccount = userAccountsDao.findOne(userAccount, Fields.builder().add(UserAccounts.ID).add(UserAccounts.USER_ID).build());

        if (userAccount == null || userAccount.getUserId() == null) {
            return null;
        }
        return userAccount;
    }

    /**
     * <p>个人角色 =  用户角色 + 用户组角色</p>
     *
     * @param siteId 站点ID
     * @param userId 用户ID
     * @return 排重后的角色集合
     */
    private Set<Roles> roles(Long siteId, Long userId) {
        List<Roles> userRoles = rolesDao.findRolesByUser(siteId, userId);
        List<Roles> groupRoles = rolesDao.findRolesByGroup(siteId, userId);
        Set<Roles> roles = new HashSet<>();
        roles.addAll(userRoles);
        roles.addAll(groupRoles);
        return roles;
    }

    @Override
    public boolean exists(String username) {
        UserAccounts userAccount = new UserAccounts();
        userAccount.setAccount(username);
        return userAccountsDao.exists(userAccount);
    }

    @Override
    public int updatePassword(String username, String password) {
        Users user = this.findUserInfo(username);
        user.setPassword(passwordEncoder.encode(password.trim()));
        return super.update(user);
    }

}
