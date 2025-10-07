package com.studyParty.AI.controller;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSON;
import com.studyParty.AI.common.Result;
import io.reactivex.Flowable;
import io.swagger.v3.core.util.Json;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class StudentAIController {
    private final String apikey = "sk-e1075cda4ca34c5d989977a2628bfe34";
    @PostMapping("/getSkillTree")
    public Result<?> getSkillTree(@RequestBody String  prompt) throws NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(apikey)
                .appId("f7179ca9fc8b42f1a275d00d28deff4e")
                .prompt(prompt)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);
        System.out.printf("text: %s\n",
                result.getOutput().getText());
        return Result.success(JSON.parse(result.getOutput().getText()));
    }
    @PostMapping("/teachingPlan")
    public Result<?> teachingPlan(@RequestBody String prompt) throws NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(apikey)
                .appId("f88ca1f92ab4456d877c3430666b1e8d")
                .prompt(prompt)
                .build();
                Application application = new Application();
                ApplicationResult result = application.call(param);
                System.out.printf("text: %s\n",
                        result.getOutput().getText());
                return Result.success(JSON.parse(result.getOutput().getText()));
    }
    @PostMapping("/taskAnalyst")
    public Result<?> taskAnalyst(@RequestBody String prompt) throws NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(apikey)
                .appId("0e0d0a99d6324d5d90fdaee23586ec10")
                .prompt(prompt)
                .build();
        Application application = new Application();
        ApplicationResult result = application.call(param);
        System.out.printf("text: %s\n",
                result.getOutput().getText());
        return Result.success(JSON.parse(result.getOutput().getText()));
    }
    @PostMapping("/articleAnalyst")
    public Result<?> articleAnalyst(@RequestBody String prompt) throws NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(apikey)
                .appId("6103a07ff32a4d0ca3063e1dc07ce7ac")
                .prompt(prompt)
                .build();
        Application application = new Application();
        ApplicationResult result = application.call(param);
        System.out.printf("text: %s\n",
                result.getOutput().getText());
        return Result.success(JSON.parse(result.getOutput().getText()));
    }
    @PostMapping("/AI")
    public SseEmitter AI(@RequestBody String prompt) throws NoApiKeyException, InputRequiredException {
        // 创建 SSE 发射器，设置超时时间
        SseEmitter emitter = new SseEmitter(0L);

        ApplicationParam param = ApplicationParam.builder()
                .apiKey(apikey)
                .appId("a9aeb75b22d844ac87985dae43a5f4e1")
                .prompt(prompt)
                .incrementalOutput(true)
                .build();

        Application application = new Application();
        Flowable<ApplicationResult> result = application.streamCall(param);
        // 订阅流式结果
        // 流完成时关闭连接
        // 发生错误时完成流
        result.subscribe(
                data -> {
                    try {
                        // 发送文本内容到前端
                        System.out.println(data);
                        emitter.send(data);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError,
                emitter::complete
        );
        return emitter;
}

}
