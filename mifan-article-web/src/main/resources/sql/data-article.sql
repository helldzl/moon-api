-- -----------------------------------------------------
-- vote 表初始化数据
-- -----------------------------------------------------
INSERT INTO `mifan_article`.`votes` (`id`, `vote_type`, `vote_text`, `vote_start`, `vote_length`, `enabled`, `creator`, `modifier`, `created`, `modified`) VALUES ('1', 'CHECKBOX', '举报原因', '2017-09-05 11:37:48', '0', '1', '0', '0', '2017-09-05 11:37:48', '2017-09-05 11:37:48');
INSERT INTO `mifan_article`.`votes_option` (`id`, `vote_id`, `vote_option_text`, `display_order`) VALUES ('1', '1', '反动言论', '1');
INSERT INTO `mifan_article`.`votes_option` (`id`, `vote_id`, `vote_option_text`, `display_order`) VALUES ('2', '1', '文题不符', '2');
INSERT INTO `mifan_article`.`votes_option` (`id`, `vote_id`, `vote_option_text`, `display_order`) VALUES ('3', '1', '错别字', '3');
INSERT INTO `mifan_article`.`votes_option` (`id`, `vote_id`, `vote_option_text`, `display_order`) VALUES ('4', '1', '其他', '4');