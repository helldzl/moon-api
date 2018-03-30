package com.mifan.batch.spider.parser;

import com.mifan.batch.spider.attachment.AttachmentClient;
import com.mifan.batch.spider.attachment.downloader.DownloaderClient;
import com.mifan.batch.spider.domain.Attachments;
import com.mifan.batch.spider.service.AttachmentsService;
import org.jsoup.select.Elements;
import org.moonframework.crawler.filter.LinkFilter;
import org.moonframework.crawler.parse.Parser;
import org.moonframework.crawler.storage.Field;
import org.moonframework.crawler.storage.Model;
import org.moonframework.crawler.storage.WebPage;
import org.moonframework.crawler.util.ElementUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/16
 */

public class WebParser extends Parser {

    private AttachmentsService attachmentsService;
    private AttachmentClient attachmentClient;
    private DownloaderClient downloaderClient;
    private LinkFilter linkFilter;

    public void setLinkFilter(LinkFilter linkFilter) {
        this.linkFilter = linkFilter;
    }

    public WebParser() {
    }

    public WebParser(int size) {
        super(size);
    }

    public WebParser(int size, int permits) {
        super(size, permits);
    }

    public WebParser(int size, int permits, boolean fair) {
        super(size, permits, fair);
    }

    @Override
    protected void discard(WebPage webPage) {
        if (logger.isErrorEnabled()) {
            logger.error("discard page : " + webPage.getUri().toString());
        }
    }

    @Override
    protected void error(WebPage webPage) {
        if (logger.isErrorEnabled()) {
            logger.error("error page : " + webPage.getUri().toString());
        }
    }

    /**
     * <p>迭代树形结构</p>
     *
     * @param elements elements
     */
    @Override
    protected List<Object> replacement(Elements elements, WebPage webPage) {
        List<Object> attachmentsList = new ArrayList<>();
        Map<String, Attachments> map = new HashMap<>(16);
        ElementUtils.iterate(elements.iterator(), null, (parent, node) -> {
            String key = null;
            if (node.hasAttr("src")) {
                key = "src";
            } else if (node.hasAttr("href")) {
                key = "href";
            }

            if (key == null) {
                return;
            }

            String origin = node.attr(key);
            if (!map.containsKey(origin)) {
                Attachments attachment = new Attachments();
                attachment.setAgencyIp(webPage.getAgencyIp());
                attachment.setAgencyIpPort(webPage.getAgencyIpPort());
                attachment.setOrigin(origin);
                attachment.setTitle(node.attr("title"));
                if ("".equals(attachment.getTitle())) {
                    attachment.setTitle(node.attr("alt"));
                }
                attachment.setEnabled(0);
                Model model = Model.from(node.attr(Field.DATA_MODEL));
                if (model != null) {
                    attachment.setGroupId(model.getId());
                }

                attachmentsService.saveOrUpdate(attachment);
                if (attachment.getId() != null) {
                    map.put(origin, attachment);
                    // 加入到图片下载队列
                    if (attachment.getEnabled() != null && attachment.getEnabled() == 0) {
                        //attachmentClient.add(attachment);
                        //在这里进行判断是否已经下载过了，是否已经存在了redis中，如果已经存在不在放入这个list中。
                        if (!linkFilter.exist("image", attachment.getOrigin())) {
                            attachmentsList.add(attachment);
                        }
                    }
                }
            }

            Attachments attachments = map.get(origin);
            node.attr(Field.DATA_ID, attachments.getId().toString());
            node.attr(Field.DATA_ORIGINAL, attachments.getOrigin());
            node.attr(key, attachments.getFilename());
        });
        return attachmentsList;
    }

    public void setAttachmentsService(AttachmentsService attachmentsService) {
        this.attachmentsService = attachmentsService;
    }

    public void setAttachmentsClient(AttachmentClient attachmentClient) {
        this.attachmentClient = attachmentClient;
    }

    public void setDownloaderClient(DownloaderClient downloaderClient) {
        this.downloaderClient = downloaderClient;
    }

    @Override
    protected void addObjectMap(Map<Long, Object> objectMap) {
        if (objectMap.size() > 0) {
            // System.out.println("进入到了WebParser01");
            objectMap.keySet().stream().filter(l -> objectMap.get(l) instanceof List).forEach(l -> {
                downloaderClient.add(objectMap);
                // System.out.println("进入到了WebParser01");
            });
        }
    }
}
