package com.ht.project.snsproject.service;

import com.ht.project.snsproject.model.feed.FeedVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FeedService {
    void feedUpload(MultipartFile file, FeedVO feedVO, String userId) throws IOException;
}
