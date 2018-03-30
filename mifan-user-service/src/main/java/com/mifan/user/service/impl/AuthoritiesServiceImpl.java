package com.mifan.user.service.impl;

import com.mifan.user.dao.AuthoritiesDao;
import com.mifan.user.domain.Authorities;
import com.mifan.user.service.AuthoritiesService;
import com.mifan.user.type.AuthType;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.service.AbstractBaseService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/3
 */
@Service
public class AuthoritiesServiceImpl extends AbstractBaseService<Authorities, AuthoritiesDao> implements AuthoritiesService {

    @Override
    public <S extends Authorities> int save(S entity) {
        // save parent
        Date date = new Date();
        entity.setCreated(date);
        entity.setModified(date);
        isTrue(baseDao.save(entity) > 0, "Error.Authorities.save");

        // save child
        AuthType authType = AuthType.fromIndex(entity.getAuthType());
        isTrue(authType.save(entity) > 0, "Error.Authorities.Resource.save");
        return 1;
    }

    @Override
    public <S extends Authorities> int update(S entity) {
        // update parent
        Date date = new Date();
        entity.setModified(date);
        isTrue(baseDao.update(entity) > 0, "Error.Authorities.update");

        // update child
        if (entity.getAuthType() != null) {
            AuthType authType = AuthType.fromIndex(entity.getAuthType());
            isTrue(authType.update(entity) > 0, "Error.Authorities.Resource.update");
        }
        return 1;
    }

    @Override
    public Authorities queryForObject(Long id) {
        Authorities authority = baseDao.findOne(id);
        if (authority == null) {
            return null;
        }
        AuthType.fromIndex(authority.getAuthType()).findByAuthority(authority);
        return authority;
    }

    @Override
    public Authorities queryForObject(Long id, Iterable<? extends Field> fields) {
        Authorities authority = super.queryForObject(id, fields);
        if (authority == null) {
            return null;
        }
        AuthType.fromIndex(authority.getAuthType()).findByAuthority(authority);
        return authority;
    }

    @Override
    public List<Authorities> findAuthorities(Long roleId) {
        return baseDao.findAuthorities(roleId);
    }

    @Override
    public List<Authorities> findAuthorities(Long roleId, Iterable<? extends Field> fields) {
        return baseDao.findAuthorities(roleId, fields);
    }

    @Override
    public Set<Authorities> findAuthoritiesByRoles(Set<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }

        List<Authorities> list = baseDao.findAuthoritiesByRoles(roleIds);

        Map<Long, Authorities> map = new HashMap<>(list.size());
        Map<AuthType, Set<Long>> setMap = new EnumMap<>(AuthType.class);
        for (AuthType authType : AuthType.values()) {
            setMap.put(authType, new HashSet<>());
        }

        list.forEach(authority -> {
            map.put(authority.getId(), authority);
            AuthType authType = AuthType.fromIndex(authority.getAuthType());
            if (authType != null) {
                setMap.get(authType).add(authority.getId());
            }
        });

        setMap.forEach((authType, array) -> {
            if (!array.isEmpty()) {
                authType.details(map, array);
            }
        });

        return new HashSet<>(list);
    }

}
