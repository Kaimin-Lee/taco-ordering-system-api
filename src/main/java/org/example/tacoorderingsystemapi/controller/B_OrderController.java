package org.example.tacoorderingsystemapi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tacoorderingsystemapi.entity.OrderInfo;
import org.example.tacoorderingsystemapi.service.B_OrderService;
import org.example.tacoorderingsystemapi.vo.OrderDetailVO;
import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b/order")
public class B_OrderController {

    @Autowired
    private B_OrderService orderService;

    @GetMapping("/today")
    public Result<List<OrderInfo>> getTodayOrders() {
        return Result.success(orderService.getTodayOrders());
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        orderService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/search")
    public Result<Page<OrderInfo>> search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String tableNo) {
        return Result.success(orderService.search(page, size, orderNo, tableNo));
    }

    @GetMapping("/detail/{id}")
    public Result<OrderDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(orderService.getDetail(id));
    }
}
