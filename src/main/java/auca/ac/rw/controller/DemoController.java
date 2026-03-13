package auca.ac.rw.controller;

import auca.ac.rw.dto.DemoResetResponse;
import auca.ac.rw.dto.DemoSeedResponse;
import auca.ac.rw.service.DemoDataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoDataService demoDataService;

    public DemoController(DemoDataService demoDataService) {
        this.demoDataService = demoDataService;
    }

    @PostMapping("/reset")
    public DemoResetResponse resetDemoData() {
        return demoDataService.reset();
    }

    @PostMapping("/reset-and-seed")
    public DemoSeedResponse resetAndSeedDemoData() {
        return demoDataService.resetAndSeed();
    }
}
