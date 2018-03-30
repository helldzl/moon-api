package com.mifan.reward.service;

import java.util.List;

public interface RedisCodesService {
    boolean reload(Long prizeId, List<String> existingCodeList);

    boolean add(Long prizeId);

    boolean remove(Long prizeId);

    Long getOne(Long prizeId);

    int length(Long prizeId);
}
