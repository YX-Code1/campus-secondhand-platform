package com.campus.trade.util;

import com.campus.trade.common.BusinessException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
public class SensitiveWordFilter {

    private List<String> sensitiveWords = new ArrayList<>();

    public List<String> getSensitiveWords() {
        return sensitiveWords;
    }

    public void setSensitiveWords(List<String> sensitiveWords) {
        this.sensitiveWords = sensitiveWords != null ? sensitiveWords : new ArrayList<>();
    }

    public void check(String... texts) {
        if (texts == null) return;
        for (String text : texts) {
            if (text == null) continue;
            for (String word : sensitiveWords) {
                if (text.contains(word)) {
                    throw new BusinessException("内容包含敏感词，请修改后重试");
                }
            }
        }
    }
}
