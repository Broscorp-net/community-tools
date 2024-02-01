package com.community.tools.service;

import com.community.tools.model.ServiceUser;
import java.util.Map;
import java.util.Set;

public interface UserService {

  String getUserById(String id);

  String getIdByUser(String id);

  String getIdByUsername(String username);

  Set<ServiceUser> getAllUsers();

  Map<String, String> getIdWithName();
}
