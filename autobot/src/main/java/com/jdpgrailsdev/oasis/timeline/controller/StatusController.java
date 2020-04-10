package com.jdpgrailsdev.oasis.timeline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/status")
public class StatusController {

    @RequestMapping("check")
    @ResponseBody
    public String statusCheck() {
        return "OK";
    }
}
