package com.mifan.article.domain.support;

/**
 * @author ZYW
 * @version 1.0
 * @since 2017年8月31日 10:04:07
 */
public class ValidationGroups {

    /**
     * 品牌提交校验
     * @author ZYW
     *
     */
    public interface BrandPost {
    }

    /**
     * 产品提交校验
     * @author ZYW
     *
     */
    public interface ProductPost {
    }

    /**
     * 新闻提交校验
     * @author ZYW
     *
     */
    public interface NewsPost {

    }

    /**
     * 视频提交校验
     * @author ZYW
     *
     */
    public interface VideosPost{

    }

    /**
     * 精翻添加校验
     * @author ZYW
     *
     */
    public interface TranslationPost{

    }
    /**
     * 精翻修改校验
     * @author ZYW
     *
     */
    public interface TranslationPatch{

    }

    /**
     * 翻译任务-翻译用户操作
     * @author ZYW
     *
     */
    public interface TranslateTaskPatch{

    }

    /**
     * 翻译任务-审核员操作
     * @author ZYW
     *
     */
    public interface AuditorPatch{

    }
    /**
     * 翻译任务-管理员添加
     * @author ZYW
     *
     */
    public interface AdminPost{
        
    }
    /**
     * 美频数据保存
     * @author ZYW
     *
     */
    public interface MpPost{
    	
    }
}
