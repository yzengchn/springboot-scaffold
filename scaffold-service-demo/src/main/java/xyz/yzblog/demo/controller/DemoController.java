package xyz.yzblog.demo.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.yzblog.core.web.response.R;
import xyz.yzblog.demo.service.DemoService;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: DemoController
 */
@Slf4j
@RestController
@RequestMapping("/demo")
@AllArgsConstructor
public class DemoController {

    private DemoService demoService;


    @GetMapping("test1")
    public R test(){

        return R.success("OK");
    }

}
