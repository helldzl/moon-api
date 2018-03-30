/**
 * Copyright (C) 2017 Budee, Inc. All Rights Reserved.
 *
 * @className:com.mifan.article.web.AttachementsController
 *
 * @description:TODO
 *
 * @version:v0.0.1
 * @author:ZYW
 *
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------
 * 2017年8月23日 ZYW v0.0.1 create
 *
 *
 */
package com.mifan.article.web.admin;

import com.mifan.article.domain.Attachments;
import com.mifan.article.service.AttachmentsService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.web.core.RestfulController;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/Attachments")
public class AdminAttachmentsController extends RestfulController<Attachments>{

    @Autowired
    private AttachmentsService attachmentsService;

    @RequiresAuthentication
    @RequiresRoles(ROLE_ADMIN)
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public ResponseEntity<Response> doPost(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "file") MultipartFile file) throws IOException {

        Long currentUserId = getCurrentUserId();

        Attachments attachment = new Attachments();
        attachment.setTitle(title);
        attachment.setId(id);
        attachment.setBytes(file.getBytes());
        SetModifyData(currentUserId, attachment);
        if(attachment.getId() == null){
            SetCreateData(currentUserId, attachment);
        }

        if(attachmentsService.add(attachment) == 0){
            throw new IllegalStateException();
        }

        Map<String, Object> result = new HashMap<>(16);
        result.put("type", "attachments");
        result.put("id", attachment.getId());
        result.put("filename", attachment.getFilename());

        return ResponseEntity.created(URI.create("attachments" + "/" + attachment.getId())).body(Responses.builder().data(result));
    }

    private void SetCreateData(Long currentUserId, BaseEntity entitiy) {
        entitiy.setCreator(currentUserId);
        entitiy.setCreated(new Date());
    }

    private void SetModifyData(Long currentUserId, BaseEntity att) {
        att.setModifier(currentUserId);
        att.setModified(new Date());
    }
}
