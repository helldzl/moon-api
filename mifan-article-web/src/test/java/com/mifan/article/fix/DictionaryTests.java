package com.mifan.article.fix;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.WordDictionary;
import com.mifan.article.service.util.EntityUtils;
import org.junit.Test;
import org.moonframework.model.mybatis.service.Services;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/10/31
 */
public class DictionaryTests extends AbstractTests {

    @Test
    public void testDic() {
        CategoriesTests.read("C:\\Quzile\\dictionary.txt", list -> {
            if (list == null) {
                return;
            }
            for (Map<String, String> map : list) {
                String en = map.get("en");
                String cn = map.get("cn");
                if (en != null && cn != null && !en.equals(cn)) {
                    en = en.toLowerCase();
                    WordDictionary dictionary = new WordDictionary();
                    dictionary.setEnabled(1);
                    dictionary.setWordEn(en);
                    dictionary.setWordEnHash(EntityUtils.asLong(en));
                    if (!Services.exists(WordDictionary.class, dictionary)) {
                        dictionary.setWordCn(cn);
                        dictionary.setWordCnHash(EntityUtils.asLong(cn));
                        Services.save(WordDictionary.class, dictionary);
                        System.out.println(String.format("EN: %s, CH : %s", en, cn));
                    }
                }
            }
        });
    }

}
