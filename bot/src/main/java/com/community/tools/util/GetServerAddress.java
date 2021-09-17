package com.community.tools.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class GetServerAddress {

  /**
   * Get url with endpoint leaderboard from HttpServletRequest.
   * @return url
   */
  public static String getAddress() {
    HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
    StringBuilder url = new StringBuilder();
    url.append(request.getScheme()).append("://").append(request.getServerName());
    Integer serverPort = request.getServerPort();
    if ((serverPort != 80) && (serverPort != 443)) {
      url.append(":").append(serverPort);
    }
    url.append(request.getContextPath()).append("/leaderboard/");
    return url.toString();
  }
}
