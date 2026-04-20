package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.dto.OrderSubmitDTO;
import org.example.tacoorderingsystemapi.service.C_OrderService;
import org.example.tacoorderingsystemapi.service.C_UserOrderService;
import org.example.tacoorderingsystemapi.vo.OrderDetailVO;
import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/c/order")
public class C_OrderController {

    @Autowired
    private C_OrderService orderService;

    @Autowired
    private C_UserOrderService userOrderService;

    @PostMapping("/submit")
    public Result<String> submitOrder(@RequestBody OrderSubmitDTO dto) {
        String orderNo = orderService.submitOrder(dto);
        return Result.success(orderNo);
    }

    @GetMapping("/my")
    public Result<List<OrderDetailVO>> getMyOrders() {
        return Result.success(userOrderService.getMyOrders());
    }
}
