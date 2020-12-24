package com.ht.project.snsproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.project.snsproject.exception.DuplicateRequestException;
import com.ht.project.snsproject.model.feed.ProfileImage;
import com.ht.project.snsproject.model.user.*;
import com.ht.project.snsproject.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


  private final UserRepository userRepository;

  private final ObjectMapper cacheObjectMapper;

  private final FileService fileService;

  @Value("${file.local.path}")
  private String localPath;

  public UserServiceImpl(UserRepository userRepository,
                         @Qualifier("awsFileService") FileService fileService,
                         @Qualifier("cacheObjectMapper") ObjectMapper cacheObjectMapper) {
    this.userRepository = userRepository;
    this.fileService = fileService;
    this.cacheObjectMapper = cacheObjectMapper;
  }

  @Override
  public void joinUser(UserJoinRequest userJoinRequest) {
    userRepository.insertUser(userJoinRequest);
  }

  @Override
  public boolean isDuplicateUserId(String userId) {
    return userRepository.isDuplicateUserId(userId);
  }

  @Override
  public void updateUserProfile(UserProfileParam userProfileParam, String userId, MultipartFile profile) {

    ProfileImage profileImage = userRepository.findUserProfileImage(userId);
    String dirPath = userId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    List<MultipartFile> files = new ArrayList<>();
    files.add(profile);

    if (profileImage != null) {
      fileService.deleteFile(profileImage.getFilePath(), profileImage.getFileName());
    }

    fileService.uploadFiles(files, dirPath, new File(localPath + dirPath));

    userRepository.updateUserProfile(UserProfile.from(userProfileParam, userId,
            new ProfileImage(dirPath, profile.getOriginalFilename())));
  }

  @Override
  public void exists(UserLogin userLogin, HttpSession httpSession) {

    if (httpSession.getAttribute("userId") != null) {
      throw new DuplicateRequestException("이미 로그인된 상태입니다.");
    }

    if (!userRepository.isAuthenticatedUser(userLogin)) {
      throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
    }

    httpSession.setAttribute("userId", userLogin.getUserId());
  }

  @Override
  public User findUserByUserId(String userId) {

    return cacheObjectMapper.convertValue(userRepository.findUserByUserId(userId), User.class);
  }

  @Override
  @CacheEvict(value = "userInfo", key = "'userInfo:' + #userId")
  public void logout(String userId, HttpSession httpSession) {

    httpSession.invalidate();
  }

  @Override
  public void deleteUser(String userId, String password, HttpSession httpSession) {

    if (!userRepository.findPasswordByUserId(userId).equals(password)) {
      throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
    }
    userRepository.deleteUser(userId);
    httpSession.invalidate();

  }

  @Override
  public void updateUserPassword(String userId, UserPassword userPassword) {

    userRepository.updateUserPassword(userId, userPassword);
  }
}
