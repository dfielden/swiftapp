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
    private volatile long showWormStartTimeMillis = 0;

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

            // Apple iOS appends an '=' for unknown reason.
            if (json.endsWith("=")) {
                json = json.substring(0, json.length() - 1);
            }

            Gson gson = new Gson();
            Code passcode = gson.fromJson(json, Code.class);
            if (passcode.getCode().equals("1234")) {
                showWormStartTimeMillis = System.currentTimeMillis();
                System.out.println("hit /activateworm");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "worm shown";
    }

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping("/emitter")
    public SseEmitter eventEmitter()  {
        System.out.println("Start of /emitter EventSource");

        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            try {
                boolean lastShowStateSent = isWormShownRightNow();
                while (true) {
                    boolean showNow = isWormShownRightNow();
                    if (showNow != lastShowStateSent) {
                        if (showNow) {
                            emitter.send("show_worm_now");
                            lastShowStateSent = true;
                        } else {
                            emitter.send("no_show_it");
                            lastShowStateSent = false;
                        }
                    }
                    Thread.sleep(100);
                }
            } catch(Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    private boolean isWormShownRightNow() {
        return System.currentTimeMillis() <= showWormStartTimeMillis + 1000;
//        return Math.random() > 0.5;
    }
}
