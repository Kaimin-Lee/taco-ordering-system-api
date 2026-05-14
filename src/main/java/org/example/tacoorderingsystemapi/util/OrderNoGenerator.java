package org.example.tacoorderingsystemapi.util;

import org.example.tacoorderingsystemapi.mapper.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class OrderNoGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    public String generate() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        
        List<String> todayOrderNos = orderInfoMapper.listTodayOrderNos();
        
        String random2 = String.format("%02d", RANDOM.nextInt(100));
        int seq = 1;
        
        if (!todayOrderNos.isEmpty()) {
            String lastOrderNo = todayOrderNos.get(todayOrderNos.size() - 1);
            String lastRandom2 = lastOrderNo.substring(8, 10);
            int lastSeq = Integer.parseInt(lastOrderNo.substring(10));
            
            if (lastSeq < 999) {
                random2 = lastRandom2;
                seq = lastSeq + 1;
            } else {
                random2 = getNewRandom2(todayOrderNos, lastRandom2);
                seq = 1;
            }
        }
        
        return dateStr + random2 + String.format("%03d", seq);
    }
    
    private String getNewRandom2(List<String> todayOrderNos, String exclude) {
        for (int i = 0; i < 100; i++) {
            String r = String.format("%02d", RANDOM.nextInt(100));
            if (!r.equals(exclude) && !isRandom2Used(todayOrderNos, r)) {
                return r;
            }
        }
        return String.format("%02d", RANDOM.nextInt(100));
    }
    
    private boolean isRandom2Used(List<String> todayOrderNos, String random2) {
        String prefix = LocalDate.now().format(DATE_FORMATTER) + random2;
        return todayOrderNos.stream().anyMatch(n -> n.startsWith(prefix));
    }
}
