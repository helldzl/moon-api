# 米饭网

## 2017年4月15日

+ 数据库改动
    + 用户地址表改动
        + 删除 user_shop_addresses 表
        + 新增 user_addresses 表
        + 线上需要执行SQL导数据

    + 用户信息表改动(执行该SQL)
        + ALTER TABLE `ucenter`.`user_profiles` CHANGE COLUMN `sign_count` `sign_count` INT(11) NOT NULL DEFAULT '0' COMMENT '签到次数', CHANGE COLUMN `sign_count_continuos` `sign_count_continuos` INT(11) NOT NULL DEFAULT '0' COMMENT '连续签到次数';

    + 用户邀请记录表改动
        + 删除 user_inviter_record 表
        + 新增 user_invitations 表
        + 线上需要执行SQL导数据

    + user_karma_logs 结构变化
        + 删除 user_credit_record 表
        + 线上需要执行SQL导数据

    + products 表增加索引 seed_id 字段, 用来增加推送分析数据分页查询的速度
    
    
## 开发环境HOST配置

+ 图片服务器:
+ 翻译/图片水印服务: