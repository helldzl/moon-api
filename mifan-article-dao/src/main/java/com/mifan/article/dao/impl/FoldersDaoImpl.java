package com.mifan.article.dao.impl;

import com.mifan.article.dao.FoldersDao;
import com.mifan.article.domain.Folders;
import org.moonframework.model.mybatis.repository.AbstractBaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-12-12
 */
@Repository
public class FoldersDaoImpl extends AbstractBaseDao<Folders> implements FoldersDao {
}
