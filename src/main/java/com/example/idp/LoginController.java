package com.example.idp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;

/**
 * Presents a very basic login form which will post back to
 * the CloudEntity "accept" url and (eventually) redirect
 * the user to the "redirect_to" url returned by CloudEntity.
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    private final CloudEntityClient cloudEntityClient;

    @Autowired
    public LoginController(CloudEntityClient cloudEntityClient) {
        this.cloudEntityClient = cloudEntityClient;
    }

    /**
     * Shows a login form. We expect this form to be a 302 from CloudEntity which
     * has login_state and login_id params. We stash login_state and login_id into
     * a form-backing bean so we can POST them to CE's "/accept" url.
     */
    @GetMapping
    public ModelAndView onGet(@RequestParam("login_state") String loginState, @RequestParam("login_id") String loginId) {
        // login_state and login_id come from CloudEntity and need to be passed along,
        // so just stuff them into a bean
        log.info("Received login_id={}",  loginId);
        log.info("Received login_state={}", loginState);
        var loginCommand = new LoginCommand();
        loginCommand.setUsername("demo");
        loginCommand.setLoginState(loginState);
        loginCommand.setLoginId(loginId);

        var map = new HashMap<String, Object>();
        map.put("loginCommand", loginCommand);
        return new ModelAndView("login", map);
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView onPost(LoginCommand loginCommand) {
        log.warn("ignoring password, we don't care");
        cloudEntityClient.accept(loginCommand.getUsername(), loginCommand);
        return new RedirectView("");
    }

}
