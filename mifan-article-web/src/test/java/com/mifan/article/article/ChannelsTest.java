/**
 * 
 */
package com.mifan.article.article;

import com.mifan.article.AbstractTests;
import com.mifan.article.domain.Channels;
import com.mifan.article.service.ChannelsService;
import io.jsonwebtoken.lang.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZYW
 *
 */
public class ChannelsTest extends AbstractTests {

    @Autowired
    private ChannelsService channelsService;
    
    @Test
    public void addTest() {
        Channels channel = new Channels();
        channel.setChannelType(4);
        channel.setChannelName("武林大会");
        List<Long> targets = new ArrayList<Long>();
        targets.add(1l);
        channel.setTargets(targets);
        channel.setModifier(1031l);
        channel.setCreator(1031l);
        Assert.isTrue(channelsService.add(channel) > 0);
        
    }
}
