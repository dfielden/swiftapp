package com.example.wormsite;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication
@Controller
public class WormsiteApplication {
    private boolean showWorm = false;

    public static void main(String[] args) {
        SpringApplication.run(WormsiteApplication.class, args);
    }

    @GetMapping("/")
    public String index() throws Exception {
        return "index";
    }

    @ResponseBody
    @PostMapping("/activateworm")
    public String activateworm(@RequestBody String code) throws Exception {
        try {
            String json = java.net.URLDecoder.decode(code, StandardCharsets.UTF_8.name());
            String trimmedJson = json.substring(0, json.length() - 1); // because post from swift has an = at and
            Gson gson = new Gson();
            Code passcode = gson.fromJson(trimmedJson, Code.class);
            if (passcode.getCode().equals("1234")) {
                System.out.println("correct code");
                showWorm = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "worm shown";
    }

    @GetMapping("/emitter")
    public SseEmitter eventEmitter() {
        SseEmitter emitter = new SseEmitter();
        //create a single thread for sending messages asynchronously
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                if (showWorm) {
                    emitter.send("showWorm");
                    System.out.println("worm!!");
                    showWorm = false;
                } else {
                    emitter.send("data");
                }
            } catch(Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitter.complete();
            }
        });
        executor.shutdown();
        return emitter;
    }

}
