package com.mifan.award.service;

import java.util.List;

public interface CodeService {
    boolean reload(String prizeId, List<String> existingCodeList);

    boolean add(String prizeId);

    boolean remove(String prizeId);

    String getOne(String prizeId);

    int length(String prizeId);
}
