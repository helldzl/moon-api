package com.mifan.user.type;


import com.mifan.user.domain.AuthOperations;
import com.mifan.user.domain.Authorities;
import org.moonframework.core.support.EnumSupporter;
import org.moonframework.model.mybatis.service.Services;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/3
 */
public enum AuthType implements EnumSupporter {

    /**
     * 方法操作权限
     */
    OPERATION(0, "操作") {
        @Override
        public int save(Authorities authorities) {
            AuthOperations authOperation = authorities.getAuthOperation();
            authOperation.setAuthorityId(authorities.getId());
            return Services.save(AuthOperations.class, authOperation);
        }

        @Override
        public int update(Authorities authorities) {
            AuthOperations authOperation = authorities.getAuthOperation();
            if (authOperation != null && authOperation.getId() != null) {
                return Services.update(AuthOperations.class, authOperation);
            }
            return 0;
        }

        @Override
        public void findByAuthority(Authorities authority) {
            AuthOperations entity = new AuthOperations();
            entity.setAuthorityId(authority.getId());
            authority.setAuthOperation(Services.findOne(AuthOperations.class, entity));
        }

        @Override
        public void details(Map<Long, Authorities> map, Iterable<Long> authorityIds) {
            Services.findAll(AuthOperations.class, AuthOperations.AUTHORITY_ID, authorityIds)
                    .stream()
                    .forEach(o -> map.get(o.getAuthorityId()).setAuthOperation(o));
            // authOperationsDao.findByAuthorities(authorityIds)
        }

    },

    /**
     * SHIRO通配符限定权限, e.g: users:create,update:*
     */
    SHIRO(1, "SHIRO") {
        @Override
        public int save(Authorities authorities) {
            return 1;
        }

        @Override
        public int update(Authorities authorities) {
            return 1;
        }

        @Override
        public void findByAuthority(Authorities authority) {
        }

        @Override
        public void details(Map<Long, Authorities> map, Iterable<Long> authorityIds) {
        }
    };

    private static Map<Integer, AuthType> map = new HashMap<>();

    static {
        for (AuthType e : AuthType.values()) {
            map.put(e.getIndex(), e);
        }
    }

    public static AuthType fromIndex(Integer index) {
        return map.get(index);
    }

    private final int index;
    private final String name;

    AuthType(int value, String name) {
        this.index = value;
        this.name = name;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @param authorities authorities
     * @return int
     */
    public abstract int save(Authorities authorities);

    /**
     * @param authorities authorities
     * @return int
     */
    public abstract int update(Authorities authorities);

    /**
     * @param authority authority
     */
    public abstract void findByAuthority(Authorities authority);

    /**
     * @param map          map
     * @param authorityIds authorityIds
     */
    public abstract void details(Map<Long, Authorities> map, Iterable<Long> authorityIds);

}
