package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-10-31
 */
public class WordDictionary extends BaseEntity {

    public static Iterable<? extends Field> DEFAULT_FIELDS = Fields.builder()
            .add(WordDictionary.ID)
            .add(WordDictionary.WORD_EN)
            .add(WordDictionary.WORD_CN)
            .add(WordDictionary.ENABLED)
            .add(WordDictionary.CREATOR)
            .add(WordDictionary.MODIFIER)
            .add(WordDictionary.CREATED)
            .add(WordDictionary.MODIFIED)
            .build();

    public static final String TABLE_NAME = "word_dictionary";

    public static final String WORD_EN = "word_en";
    public static final String WORD_EN_HASH = "word_en_hash";
    public static final String WORD_CN = "word_cn";
    public static final String WORD_CN_HASH = "word_cn_hash";

    private static final long serialVersionUID = -1078506466472388171L;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.WordDictionary.wordEn}")
    @Size(groups = {Post.class, Patch.class}, message = "{Size.WordDictionary.wordEn}", min = 1, max = 200)
    private String wordEn;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.WordDictionary.wordEnHash}")
    private Long wordEnHash;

    @NotNull(groups = {Post.class, Patch.class}, message = "{NotNull.WordDictionary.wordCn}")
    @Size(groups = {Post.class, Patch.class}, message = "{Size.WordDictionary.wordCn}", min = 1, max = 200)
    private String wordCn;

    @Null(groups = {Post.class, Patch.class}, message = "{Null.WordDictionary.wordCnHash}")
    private Long wordCnHash;

    public WordDictionary() {
    }

    public WordDictionary(Long id) {
        super(id);
    }

    /**
     * @return
     */
    public String getWordEn() {
        return wordEn;
    }

    /**
     * @param wordEn
     */
    public void setWordEn(String wordEn) {
        this.wordEn = wordEn;
    }

    /**
     * @return
     */
    public Long getWordEnHash() {
        return wordEnHash;
    }

    /**
     * @param wordEnHash
     */
    public void setWordEnHash(Long wordEnHash) {
        this.wordEnHash = wordEnHash;
    }

    /**
     * @return
     */
    public String getWordCn() {
        return wordCn;
    }

    /**
     * @param wordCn
     */
    public void setWordCn(String wordCn) {
        this.wordCn = wordCn;
    }

    /**
     * @return
     */
    public Long getWordCnHash() {
        return wordCnHash;
    }

    /**
     * @param wordCnHash
     */
    public void setWordCnHash(Long wordCnHash) {
        this.wordCnHash = wordCnHash;
    }

}
