package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.service.B_DashboardService;
import org.example.tacoorderingsystemapi.service.B_OrderService;
import org.example.tacoorderingsystemapi.vo.DashboardVO;
import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/b/dashboard")
public class B_DashboardController {

    @Autowired
    private B_DashboardService dashboardService;

    @Autowired
    private B_OrderService orderService;

    @GetMapping("/today")
    public Result<DashboardVO> getToday() {
        return Result.success(dashboardService.getToday());
    }

    @GetMapping("/week")
    public Result<DashboardVO> getThisWeek() {
        return Result.success(dashboardService.getThisWeek());
    }

    @GetMapping("/month")
    public Result<DashboardVO> getThisMonth() {
        return Result.success(dashboardService.getThisMonth());
    }

    @GetMapping("/year")
    public Result<DashboardVO> getThisYear() {
        return Result.success(dashboardService.getThisYear());
    }

    @PutMapping("/complete/{id}")
    public Result<Void> completeOrder(@PathVariable Long id) {
        orderService.updateStatus(id, 2);
        return Result.success();
    }
}
