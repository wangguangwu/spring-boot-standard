package com.wangguangwu.springbootstandard.controller;

import com.wangguangwu.springbootstandard.anno.LessLog;
import com.wangguangwu.springbootstandard.exception.ServiceException;
import com.wangguangwu.springbootstandard.log.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Api 控制器类。
 *
 * @author wangguangwu
 */
@Slf4j
@RequestMapping("/api")
@RestController
public class ApiController {

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @GetMapping("/exception")
    public void exception() {
        throw new ServiceException();
    }

    @LessLog(type = LogType.URL)
    @GetMapping("/lessUrl")
    public String lessUrl() {
        return "lessUrl";
    }

    @LessLog(type = LogType.REQUEST)
    @GetMapping("/lessRequest")
    public String lessRequest() {
        return "lessRequest";
    }

    @LessLog(type = LogType.RESPONSE)
    @GetMapping("/lessResponse")
    public String lessResponse() {
        return "lessResponse";
    }

    @LessLog(type = LogType.ALL)
    @GetMapping("/lessAll")
    public String lessAll() {
        return "lessAll";
    }

    @LessLog(type = LogType.NONE)
    @GetMapping("/lessNone")
    public String lessNone() {
        return "lessNone";
    }
}
