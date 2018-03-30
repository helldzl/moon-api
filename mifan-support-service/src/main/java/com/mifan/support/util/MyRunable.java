/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.user.utils.MyRunable
 *
 * @description:TODO
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年6月7日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.support.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZYW
 *
 */
public abstract class MyRunable implements Runnable{
    protected Map<String,String[]> params;
    
    public MyRunable(Map<String, String[]> map) {
        this.params = new HashMap<String,String[]>();
        if(!map.isEmpty()) {
            map.forEach((k,v) -> {this.params.put(k,v);});
        }
    }
    
    
}
