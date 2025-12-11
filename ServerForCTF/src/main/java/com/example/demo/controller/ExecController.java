package com.example.demo.controller;

import com.example.demo.service.ExecService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/run")
@RequiredArgsConstructor
public class ExecController {

    private final ExecService execService;

    private static final List<String> BINARIES = List.of(
        "challenge",
        "bin_2",
        "bin_3",
        "bin_4",
        "bin_5",
        "bin_6",
        "bin_7",
        "bin_8",
        "bin_9",
        "bin_10",
        "bin_11",
        "bin_12",
        "bin_13",
        "bin_14",
        "bin_15",
        "bin_16",
        "bin_17",
        "bin_18",
        "bin_19",
        "bin_20"
    );

    // Главный эндпоинт — передаёт данные в STDIN бинарника
    @PostMapping("/{binary}")
    public String runProcess(
        @PathVariable String binary,
        @RequestBody String body
    ) throws Exception {
        if (!BINARIES.contains(binary)) {
            throw new IllegalArgumentException("Binary not allowed");
        }

        String path = "/app/binaries/" + binary;
        System.out.println(execService.runBinaryWithStdin(path, body));
        return execService.runBinaryWithStdin(path, body);
    }
}