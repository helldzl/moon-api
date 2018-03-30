package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-09-02
 */
public class Messages extends BaseEntity {

    public static final String TABLE_NAME = "messages";

    public static final String MESSAGE = "message";

    private static final long serialVersionUID = 2257662442978805730L;

    @Null(groups = Post.class)
    private Long id;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.Messages.message}")
    @Size(min = 2, max = 140, groups = {Post.class, Patch.class}, message = "{Size.Messages.message}")
    private String message;

    public Messages() {
    }

    public Messages(Long id) {
        super(id);
    }

    /**
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
