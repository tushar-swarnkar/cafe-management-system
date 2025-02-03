package inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import inn.cafe.JWT.CustomerUserDetailsService;
import inn.cafe.JWT.JwtFilter;
import inn.cafe.JWT.JwtUtil;
import inn.cafe.POJO.User;
import inn.cafe.constants.CafeConstants;
import inn.cafe.dao.UserDao;
import inn.cafe.service.UserService;
import inn.cafe.utils.CafeUtils;
import inn.cafe.utils.EmailUtils;
import inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside singUp {} ", requestMap);

        try {
            if (validateSignUp(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered!", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUp(Map<String, String> requestMap) {
        return requestMap.containsKey("name")
                && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email")
                && requestMap.containsKey("password");
    }

    public User getUserFromMap(Map<String, String> reuestMap) {
        User user = new User();
        user.setName(reuestMap.get("name"));
        user.setContactNumber(reuestMap.get("contactNumber"));
        user.setEmail(reuestMap.get("email"));
        user.setPassword(reuestMap.get("password"));
        user.setStatus("false");
        user.setRole("User");

        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login {} ", requestMap);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );

            if (authentication.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetails().getEmail(),
                                    customerUserDetailsService.getUserDetails().getRole()) + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("{\"message\"" + "Wait for Admin approval." + "\"}", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("{\"message\"" + "Invalid Credentials." + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        log.info("Inside getAllUsers");

        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestmap) {
        log.info("Inside update {} ", requestmap);

        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDao.findById(Integer.parseInt(requestmap.get("id")));

                if (!optional.isEmpty()) {
                    userDao.updateStatus(requestmap.get("status"), Integer.parseInt(requestmap.get("id")));

                    sendMailToAllAdmin(requestmap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());

                    return CafeUtils.getResponseEntity("Status Updated Successfully", HttpStatus.OK);
                } else {
                    CafeUtils.getResponseEntity("User id does not exist", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void sendMailToAllAdmin(String status, String user, List<String> adminList) {
        adminList.remove(jwtFilter.getCurrentUser());

        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(
                    jwtFilter.getCurrentUser(),
                    "Account Approved",
                    "USER: " + user + "\n is approved by \nADMIN: " + jwtFilter.getCurrentUser() + ")",
                    adminList
            );
        } else {
            emailUtils.sendSimpleMessage(
                    jwtFilter.getCurrentUser(),
                    "Account Disabled",
                    "USER: " + user + "\n is disabled by \nADMIN" + jwtFilter.getCurrentUser() + ")",
                    adminList
            );
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        log.info("Inside checkToken");

        return CafeUtils.getResponseEntity("Token is valid", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        log.info("Inside changePassword {} ", requestMap);

        try {
            User user = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (!user.equals(null)) {
                if (user.getPassword().equals(requestMap.get("oldPassword"))) {
                    user.setPassword(requestMap.get("newPassword"));
                    userDao.save(user);
                    return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Incorrect Old Password", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        log.info("Inside forgetPassword {} ", requestMap);

        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
                emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management system", user.getPassword());
                return CafeUtils.getResponseEntity("Check you mail for credentials", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity("Email Not Found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
