# 塔可点餐系统 API

基于 Spring Boot 3.x + MyBatis-Plus 的街头塔可点餐系统后端API。

## 技术栈

- Spring Boot 3.x
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JWT 认证
- Hutool 工具库

## 功能模块

### 用户端 (C端)
- 微信静默登录
- 菜品浏览
- 购物车下单
- 订单查询

### 管理端 (B端)
- 管理员登录
- 菜品分类管理
- 菜品管理
- 订单管理
- 数据统计看板

## 快速开始

### 1. 数据库初始化
执行 `初始化sql.txt` 中的SQL语句创建数据库和表。

### 2. 配置文件
修改 `application.yml` 中的数据库连接信息和微信小程序配置。

### 3. 运行项目
```bash
./mvnw spring-boot:run
```

## API接口

### 用户端接口
- POST `/api/c/auth/login` - 微信登录
- GET `/api/c/product/menu` - 获取菜单
- POST `/api/c/order/submit` - 提交订单
- GET `/api/c/order/my` - 我的订单

### 管理端接口
- POST `/api/b/auth/login` - 管理员登录
- GET `/api/b/dashboard/today` - 今日数据
- GET `/api/b/order/today` - 今日订单
- GET `/api/b/category/list` - 分类列表
- GET `/api/b/product/list` - 菜品列表
