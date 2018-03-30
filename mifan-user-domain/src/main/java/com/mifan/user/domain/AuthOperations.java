package com.mifan.user.domain;

import org.hibernate.validator.constraints.Range;
import org.moonframework.validation.ValidationGroups;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-04
 */
public class AuthOperations extends AuthorityConfig {

    public static final String TABLE_NAME = "auth_operations";

    public static final String AUTHORITY_ID = "authority_id";
    public static final String FUNCTION_CLASS = "function_class";
    public static final String FUNCTION_SIGNATURE = "function_signature";
    public static final String ARGS = "args";
    public static final String ARGS_LENGTH = "args_length";

    private static final long serialVersionUID = -8852251023874153533L;
    private static final String REGEXP_PACKAGE = "([a-zA-Z0-9]+(.[a-zA-Z0-9]+)*)";
    private static final String REGEXP_METHOD = "^[a-zA-Z_][a-zA-Z0-9_]*";

    @NotNull(groups = ValidationGroups.Post.class, message = "{NotNull.AuthOperations.functionClass}")
    @Size(max = 255, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Size.AuthOperations.functionClass}")
    @Pattern(regexp = REGEXP_PACKAGE, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Pattern.AuthOperations.functionClass}")
    private String functionClass;

    @NotNull(groups = ValidationGroups.Post.class, message = "{NotNull.AuthOperations.functionSignature}")
    @Size(max = 50, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Size.AuthOperations.functionSignature}")
    @Pattern(regexp = REGEXP_METHOD, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Pattern.AuthOperations.functionSignature}")
    private String functionSignature;

    @Size(max = 100, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Size.AuthOperations.args}")
    private String args;

    @Range(min = 0, max = 255, groups = {
            ValidationGroups.Post.class,
            ValidationGroups.Put.class
    }, message = "{Range.AuthOperations.argsLength}")
    private Integer argsLength;

    public AuthOperations() {
    }

    public AuthOperations(Long id) {
        super(id);
    }

    @Override
    public String value() {
        return functionClass + "." + functionSignature;
    }

    /**
     * @return package
     */
    public String getFunctionClass() {
        return functionClass;
    }

    /**
     * @param functionClass package
     */
    public void setFunctionClass(String functionClass) {
        this.functionClass = functionClass;
    }

    /**
     * @return method
     */
    public String getFunctionSignature() {
        return functionSignature;
    }

    /**
     * @param functionSignature method
     */
    public void setFunctionSignature(String functionSignature) {
        this.functionSignature = functionSignature;
    }

    /**
     * @return 参数字符串
     */
    public String getArgs() {
        return args;
    }

    /**
     * @param args 参数字符串
     */
    public void setArgs(String args) {
        this.args = args;
    }

    /**
     * @return 参数个数
     */
    public Integer getArgsLength() {
        return argsLength;
    }

    /**
     * @param argsLength 参数个数
     */
    public void setArgsLength(Integer argsLength) {
        this.argsLength = argsLength;
    }

}
