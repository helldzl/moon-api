package com.mifan.user.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-24
 */
public class UserProfiles extends BaseEntity {

    public static Iterable<? extends Field> AVATAR_FIELDS = Fields.builder()
            .add(UserProfiles.ID)
            .add(UserProfiles.NICKNAME)
            .add(UserProfiles.USER_AVATAR)
            .add(UserProfiles.USER_SIG)
            .build();

    public static final String TABLE_NAME = "user_profiles";

    public static final String BIT_ROLES = "bit_roles";
    public static final String GENDER = "gender";
    public static final String BIRTHDAY = "birthday";
    public static final String NICKNAME = "nickname";
    public static final String FULLNAME = "fullname";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String POSTAL_CODE = "postal_code";
    public static final String COUNTRY = "country";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String DISTRICT = "district";
    public static final String MOBILE = "mobile";
    public static final String EMAIL = "email";
    public static final String FAX = "fax";
    public static final String QQ = "qq";
    public static final String WEIBO = "weibo";
    public static final String WECHAT = "wechat";
    public static final String THEME_ID = "theme_id";
    public static final String USER_LEVEL = "user_level";
    public static final String USER_KARMA = "user_karma";
    public static final String USER_POSTS = "user_posts";
    public static final String USER_NEW_PRIVMSG = "user_new_privmsg";
    public static final String USER_UNREAD_PRIVMSG = "user_unread_privmsg";
    public static final String USER_LAST_PRIVMSG = "user_last_privmsg";
    public static final String USER_LASTVISIT = "user_lastvisit";
    public static final String USER_ALLOW_PM = "user_allow_pm";
    public static final String USER_ALLOW_VIEWONLINE = "user_allow_viewonline";
    public static final String USER_ALLOW_AVATAR = "user_allow_avatar";
    public static final String USER_POPUP_PM = "user_popup_pm";
    public static final String USER_NOTIFY = "user_notify";
    public static final String USER_NOTIFY_ALWAYS = "user_notify_always";
    public static final String USER_NOTIFY_TEXT = "user_notify_text";
    public static final String USER_VIEW_EMAIL = "user_view_email";
    public static final String USER_FROM = "user_from";
    public static final String USER_EDUCATION = "user_education";
    public static final String USER_BIOGRAPHY = "user_biography";
    public static final String USER_SIG = "user_sig";
    public static final String USER_AVATAR = "user_avatar";
    public static final String USER_SKILLS = "user_skills";
    public static final String USER_AWARDS = "user_awards";
    public static final String USER_INTERESTS = "user_interests";
    public static final String USER_WEBSITE = "user_website";
    public static final String USER_WEBSITE_IMAGE = "user_website_image";
    public static final String SECURITY_HASH = "security_hash";
    public static final String SECURITY_AUTH = "security_auth";
    public static final String CATEGORIES = "categories";
    public static final String SERVICES = "services";
    public static final String SERVICE_REGIONS = "service_regions";
    public static final String CONTACT_PHONE = "contact_phone";
    public static final String COUNTRY_ID = "country_id";
    public static final String CITY_ID = "city_id";
    public static final String LOCATION = "location";
    public static final String COMPANY = "company";
    public static final String PROFESSION = "profession";
    public static final String SIGN_COUNT = "sign_count";
    public static final String SIGN_COUNT_CONTINUOS = "sign_count_continuos";
    public static final String SIGN_TIME = "sign_time";

    private static final long serialVersionUID = -6223908016977070539L;

    private Integer bitRoles;

    @Range(min = 0, max = 2, groups = {Post.class, Patch.class}, message = "{Range.UserProfiles.gender}")
    private Integer gender;

    @Past(groups = {Post.class, Patch.class}, message = "{Past.UserProfiles.birthday}")
    @NotNull(message = "{NotNull.UserProfiles.birthday}", groups = {Patch.class})
    private Date birthday;

    @NotNull(message = "{NotNull.UserProfiles.birthday}", groups = {Patch.class})
    private String nickname;
    private String fullname;
    private String firstname;
    private String lastname;
    private String postalCode;
    private String country;
    private String province;
    private String city;
    private String district;
    private String mobile;
    @NotBlank(message = "{NotNull.UserProfiles.email}", groups = {
            Patch.class
    })
    private String email;
    private String fax;
    private String qq;
    private String weibo;
    private String wechat;
    private Long themeId;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UserProfiles.userLevel}")
    private Integer userLevel;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UserProfiles.userKarma}")
    private Integer userKarma;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UserProfiles.userPosts}")
    private Integer userPosts;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UserProfiles.userNewPrivmsg}")
    private Integer userNewPrivmsg;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UserProfiles.userUnreadPrivmsg}")
    private Integer userUnreadPrivmsg;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UserProfiles.userLastPrivmsg}")
    private Date userLastPrivmsg;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.UserProfiles.userLastvisit}")
    private Date userLastvisit;

    @Range(min = 0, max = 1)
    private Integer userAllowPm;
    @Range(min = 0, max = 1)
    private Integer userAllowViewonline;
    @Range(min = 0, max = 1)
    private Integer userAllowAvatar;
    @Range(min = 0, max = 1)
    private Integer userPopupPm;
    @Range(min = 0, max = 1)
    private Integer userNotify;
    @Range(min = 0, max = 1)
    private Integer userNotifyAlways;
    @Range(min = 0, max = 1)
    private Integer userNotifyText;
    @Range(min = 0, max = 1)
    private Integer userViewEmail;

    private String userFrom;
    private String userEducation;
    private String userBiography;
    private String userSig;
    /*@NotNull(message = "{NotNull.UserProfiles.userAvatar}", groups = {
            Post.class
    })*/
    private String userAvatar;
    private String userSkills;
    private String userAwards;
    private String userInterests;
    private String userWebsite;
    private String userWebsiteImage;
    private String securityHash;
    private String securityAuth;
    private String categories;
    private String services;
    private String serviceRegions;
    private String contactPhone;
    private Long countryId;

    @NotNull(message = "{NotNull.UserProfiles.cityId}", groups = {Patch.class})
    private Long cityId;
    private String location;

    @NotBlank(message = "{NotNull.UserProfiles.company}", groups = {Patch.class})
    private String company;

    @NotBlank(message = "{NotNull.UserProfiles.profession}", groups = {Patch.class})
    private String profession;

    @Null(message = "Null.UserProfiles.signCount", groups = {Post.class, Patch.class})
    private Integer signCount;

    @Null(message = "Null.UserProfiles.signCountContinuos", groups = {Post.class, Patch.class})
    private Integer signCountContinuos;

    @Null(message = "Null.UserProfiles.signTime", groups = {Post.class, Patch.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signTime;

    public UserProfiles() {
    }

    public UserProfiles(Long id) {
        super(id);
    }

    // domain method

    /**
     * <p>是否完善资料</p>
     *
     * @return
     */
    public boolean isCompleted() {
        return cityId != null && cityId != 0 &&
                !StringUtils.isEmpty(userAvatar) &&
                !StringUtils.isEmpty(fullname) &&
                !StringUtils.isEmpty(nickname) &&
                !StringUtils.isEmpty(birthday) &&
                !StringUtils.isEmpty(company) &&
                !StringUtils.isEmpty(profession) &&
                !StringUtils.isEmpty(email);
    }

    // get and set method

    /**
     * <p>位角色, 1,2,4,8,16,32,64...</p>
     * <p>1:普通用户, 2:设计师</p>
     *
     * @return
     * @deprecated
     */
    @Deprecated
    @JsonIgnore
    // @JsonView(Users.WithoutPasswordView.class)
    public Integer getBitRoles() {
        return bitRoles;
    }

    /**
     * <p>位角色, 1,2,4,8,16,32,64...</p>
     * <p>1:普通用户, 2:设计师</p>
     *
     * @param bitRoles
     */
    public void setBitRoles(Integer bitRoles) {
        this.bitRoles = bitRoles;
    }

    /**
     * @return 性别 0:保密 1:男 2:女
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getGender() {
        return gender;
    }

    /**
     * @param gender 性别 0:保密 1:男 2:女
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * @return 出生日期
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Date getBirthday() {
        return birthday;
    }

    /**
     * @param birthday 出生日期
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * @return 用户昵称
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname 用户昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return 真实姓名
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getFullname() {
        return fullname;
    }

    /**
     * @param fullname 真实姓名
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * @return 名
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname 名
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return 姓
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname 姓
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return 邮编
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode 邮编
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return 国家
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getCountry() {
        return country;
    }

    /**
     * @param country 国家
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return 省
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getProvince() {
        return province;
    }

    /**
     * @param province 省
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * @return 市
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getCity() {
        return city;
    }

    /**
     * @param city 市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return 区
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getDistrict() {
        return district;
    }

    /**
     * @param district 区
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * @return 手机
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile 手机
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return 联系邮箱
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getEmail() {
        return email;
    }

    /**
     * @param email 联系邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return 传真
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getFax() {
        return fax;
    }

    /**
     * @param fax 传真
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return QQ
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getQq() {
        return qq;
    }

    /**
     * @param qq QQ
     */
    public void setQq(String qq) {
        this.qq = qq;
    }

    /**
     * @return 微博
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getWeibo() {
        return weibo;
    }

    /**
     * @param weibo 微博
     */
    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    /**
     * @return 微信
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getWechat() {
        return wechat;
    }

    /**
     * @param wechat 微信
     */
    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    /**
     * @return 个人空间主题ID
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Long getThemeId() {
        return themeId;
    }

    /**
     * @param themeId 个人空间主题ID
     */
    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    /**
     * @return 用户等级
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserLevel() {
        return userLevel;
    }

    /**
     * @param userLevel 用户等级
     */
    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    /**
     * @return 用户积分
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserKarma() {
        return userKarma;
    }

    /**
     * @param userKarma 用户积分
     */
    public void setUserKarma(Integer userKarma) {
        this.userKarma = userKarma;
    }

    /**
     * @return
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserPosts() {
        return userPosts;
    }

    /**
     * @param userPosts
     */
    public void setUserPosts(Integer userPosts) {
        this.userPosts = userPosts;
    }

    /**
     * @return 最新私信数
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserNewPrivmsg() {
        return userNewPrivmsg;
    }

    /**
     * @param userNewPrivmsg 最新私信数
     */
    public void setUserNewPrivmsg(Integer userNewPrivmsg) {
        this.userNewPrivmsg = userNewPrivmsg;
    }

    /**
     * @return 未读私信数
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserUnreadPrivmsg() {
        return userUnreadPrivmsg;
    }

    /**
     * @param userUnreadPrivmsg 未读私信数
     */
    public void setUserUnreadPrivmsg(Integer userUnreadPrivmsg) {
        this.userUnreadPrivmsg = userUnreadPrivmsg;
    }

    /**
     * @return 最后发送私信时间
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Date getUserLastPrivmsg() {
        return userLastPrivmsg;
    }

    /**
     * @param userLastPrivmsg 最后发送私信时间
     */
    public void setUserLastPrivmsg(Date userLastPrivmsg) {
        this.userLastPrivmsg = userLastPrivmsg;
    }

    /**
     * @return 最后访问时间
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Date getUserLastvisit() {
        return userLastvisit;
    }

    /**
     * @param userLastvisit 最后访问时间
     */
    public void setUserLastvisit(Date userLastvisit) {
        this.userLastvisit = userLastvisit;
    }

    /**
     * @return 是否允许发短信息 0:否 1:是
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserAllowPm() {
        return userAllowPm;
    }

    /**
     * @param userAllowPm 是否允许发短信息 0:否 1:是
     */
    public void setUserAllowPm(Integer userAllowPm) {
        this.userAllowPm = userAllowPm;
    }

    /**
     * @return 是否允许显示上线 0:否 1:是
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserAllowViewonline() {
        return userAllowViewonline;
    }

    /**
     * @param userAllowViewonline 是否允许显示上线 0:否 1:是
     */
    public void setUserAllowViewonline(Integer userAllowViewonline) {
        this.userAllowViewonline = userAllowViewonline;
    }

    /**
     * @return 是否显示头像 0:否 1:是
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserAllowAvatar() {
        return userAllowAvatar;
    }

    /**
     * @param userAllowAvatar 是否显示头像 0:否 1:是
     */
    public void setUserAllowAvatar(Integer userAllowAvatar) {
        this.userAllowAvatar = userAllowAvatar;
    }

    /**
     * @return 是否提示有新短消息 0:否 1:是
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserPopupPm() {
        return userPopupPm;
    }

    /**
     * @param userPopupPm 是否提示有新短消息 0:否 1:是
     */
    public void setUserPopupPm(Integer userPopupPm) {
        this.userPopupPm = userPopupPm;
    }

    /**
     * @return 是否设置自动回复通知 0:否 1:是
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserNotify() {
        return userNotify;
    }

    /**
     * @param userNotify 是否设置自动回复通知 0:否 1:是
     */
    public void setUserNotify(Integer userNotify) {
        this.userNotify = userNotify;
    }

    /**
     * @return 是否发送所有新文章的提示邮件 0:否 1:是
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserNotifyAlways() {
        return userNotifyAlways;
    }

    /**
     * @param userNotifyAlways 是否发送所有新文章的提示邮件 0:否 1:是
     */
    public void setUserNotifyAlways(Integer userNotifyAlways) {
        this.userNotifyAlways = userNotifyAlways;
    }

    /**
     * @return 是否在提示邮件中包含发布内容 0:否 1:是
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserNotifyText() {
        return userNotifyText;
    }

    /**
     * @param userNotifyText 是否在提示邮件中包含发布内容 0:否 1:是
     */
    public void setUserNotifyText(Integer userNotifyText) {
        this.userNotifyText = userNotifyText;
    }

    /**
     * @return 是否显示电子邮箱 0:否 1:是
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Integer getUserViewEmail() {
        return userViewEmail;
    }

    /**
     * @param userViewEmail 是否显示电子邮箱 0:否 1:是
     */
    public void setUserViewEmail(Integer userViewEmail) {
        this.userViewEmail = userViewEmail;
    }

    /**
     * @return 来自
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserFrom() {
        return userFrom;
    }

    /**
     * @param userFrom 来自
     */
    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    /**
     * @return 教育背景
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserEducation() {
        return userEducation;
    }

    /**
     * @param userEducation 教育背景
     */
    public void setUserEducation(String userEducation) {
        this.userEducation = userEducation;
    }

    /**
     * @return 自我介绍
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserBiography() {
        return userBiography;
    }

    /**
     * @param userBiography 自我介绍
     */
    public void setUserBiography(String userBiography) {
        this.userBiography = userBiography;
    }

    /**
     * @return 个性签名
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserSig() {
        return userSig;
    }

    /**
     * @param userSig 个性签名
     */
    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    /**
     * @return 用户头像
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserAvatar() {
        return userAvatar;
    }

    /**
     * @param userAvatar 用户头像
     */
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    /**
     * @return 专业技能
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserSkills() {
        return userSkills;
    }

    /**
     * @param userSkills 专业技能
     */
    public void setUserSkills(String userSkills) {
        this.userSkills = userSkills;
    }

    /**
     * @return 获奖情况
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserAwards() {
        return userAwards;
    }

    /**
     * @param userAwards 获奖情况
     */
    public void setUserAwards(String userAwards) {
        this.userAwards = userAwards;
    }

    /**
     * @return 兴趣爱好
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserInterests() {
        return userInterests;
    }

    /**
     * @param userInterests 兴趣爱好
     */
    public void setUserInterests(String userInterests) {
        this.userInterests = userInterests;
    }

    /**
     * @return 个人网站
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserWebsite() {
        return userWebsite;
    }

    /**
     * @param userWebsite 个人网站
     */
    public void setUserWebsite(String userWebsite) {
        this.userWebsite = userWebsite;
    }

    /**
     * @return 个人主页默认图片
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getUserWebsiteImage() {
        return userWebsiteImage;
    }

    /**
     * @param userWebsiteImage 个人主页默认图片
     */
    public void setUserWebsiteImage(String userWebsiteImage) {
        this.userWebsiteImage = userWebsiteImage;
    }

    /**
     * @return 安全问题
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getSecurityHash() {
        return securityHash;
    }

    /**
     * @param securityHash 安全问题
     */
    public void setSecurityHash(String securityHash) {
        this.securityHash = securityHash;
    }

    /**
     * @return 安全答案
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getSecurityAuth() {
        return securityAuth;
    }

    /**
     * @param securityAuth 安全答案
     */
    public void setSecurityAuth(String securityAuth) {
        this.securityAuth = securityAuth;
    }

    /**
     * @return 分类
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getCategories() {
        return categories;
    }

    /**
     * @param categories 分类
     */
    public void setCategories(String categories) {
        this.categories = categories;
    }

    /**
     * @return 提供服务
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getServices() {
        return services;
    }

    /**
     * @param services 提供服务
     */
    public void setServices(String services) {
        this.services = services;
    }

    /**
     * @return 提供服务区域
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getServiceRegions() {
        return serviceRegions;
    }

    /**
     * @param serviceRegions 提供服务区域
     */
    public void setServiceRegions(String serviceRegions) {
        this.serviceRegions = serviceRegions;
    }

    /**
     * @return 联系电话
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * @param contactPhone 联系电话
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * @return 所在国家
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Long getCountryId() {
        return countryId;
    }

    /**
     * @param countryId 所在国家
     */
    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    /**
     * @return 所在城市ID
     */
    @JsonView(Users.WithoutPasswordView.class)
    public Long getCityId() {
        return cityId;
    }

    /**
     * @param cityId 所在城市ID
     */
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    /**
     * @return 详细位置
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getLocation() {
        return location;
    }

    /**
     * @param location 详细位置
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return 公司名称
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getCompany() {
        return company;
    }

    /**
     * @param company 公司名称
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return 职业
     */
    @JsonView(Users.WithoutPasswordView.class)
    public String getProfession() {
        return profession;
    }

    /**
     * @param profession 职业
     */
    public void setProfession(String profession) {
        this.profession = profession;
    }

    /**
     * @return 签到次数
     */
    public Integer getSignCount() {
        return signCount;
    }

    /**
     * @param signCount 签到次数
     */
    public void setSignCount(Integer signCount) {
        this.signCount = signCount;
    }

    /**
     * @return 连续签到次数
     */
    public Integer getSignCountContinuos() {
        return signCountContinuos;
    }

    /**
     * @param signCountContinuos 连续签到次数
     */
    public void setSignCountContinuos(Integer signCountContinuos) {
        this.signCountContinuos = signCountContinuos;
    }

    /**
     * @return 签到时间
     */
    public Date getSignTime() {
        return signTime;
    }

    /**
     * @param signTime 签到时间
     */
    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    public List<Integer> roleValues() {
        if (bitRoles == null) {
            return Collections.emptyList();
        }
        int n = 1;
        List<Integer> list = new ArrayList<>();
        IntStream.range(0, 32).forEach(value -> {
            int s = n << value;
            if ((s & bitRoles) != 0) {
                list.add(s);
            }
        });
        return list;
    }

    public static void main(String[] args) {
        UserProfiles o = new UserProfiles();
        o.setBitRoles(-1);
        System.out.println(Integer.toBinaryString(o.getBitRoles()));
        System.out.println(o.roleValues());
    }

}
