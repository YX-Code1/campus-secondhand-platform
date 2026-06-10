# 校园二手物品交易管理系统

**项目代号：** DB2026-RA-V1.0  
**技术栈：** Vue 3 + Element Plus | Spring Boot 3 + MyBatis Plus（无 JPA）| MySQL 8

**项目路径示例：** `D:\usually\JAVA\campus-secondhand`

## 功能模块

| 模块 | 功能 |
|------|------|
| 用户管理 | 注册、登录、个人信息、管理员禁用/删除用户 |
| 物品管理 | 发布、查询、修改、逻辑下架、图片上传、敏感词过滤 |
| 交易管理 | 发起购买、交易状态流转（待处理/进行中/已完成/已取消） |
| 数据统计 | 交易总量、热门物品图表、待审核物品 |
| 后台管理 | 用户管理、物品审核、强制下架、Excel 导出 |

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0

## 快速启动

### 1. 数据库

```powershell
cd D:\usually\JAVA\campus-secondhand
mysql -u root -p < sql\init.sql
```

若已初始化过数据库，需额外执行私聊表脚本：

```powershell
mysql -u root -p < sql\chat_message.sql
```

修改 `backend/src/main/resources/application.yml` 中的数据库账号密码。

### 2. 后端

```powershell
cd D:\usually\JAVA\campus-secondhand\backend
mvn spring-boot:run
```

API 地址：`http://localhost:8080/api`

### 3. 前端

```powershell
cd D:\usually\JAVA\campus-secondhand\frontend
npm install
npm run dev
```

访问：`http://localhost:5173`

### 默认管理员

- 账号：`admin`
- 密码：`Admin@123`

## 需求覆盖 (PRS)

| 编号 | 实现要点 |
|------|----------|
| PRS001 | 表单校验、BCrypt 密码、JWT 登录 |
| PRS002 | 物品发布、图片上传、敏感词过滤 |
| PRS003 | 关键词/分类/价格筛选、分页、数据库索引 |
| PRS004 | 仅发布者可编辑 |
| PRS005 | 逻辑删除、管理员强制下架 |
| PRS006 | 交易记录、状态流转 |
| PRS007 | 统计报表、ECharts 可视化、物品审核 |
| PRS008 | 手机号 AES 加密、全局异常处理、操作日志 |
| PRS009 | 数据库索引 |
| PRS010 | HikariCP 连接池（最大 50 连接） |

## 项目结构

```
campus-secondhand/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
├── sql/              # 数据库脚本
├── docs/             # 项目文档
└── README.md
```

详细说明（各包职责、每个类的作用）见：[docs/项目结构说明.md](docs/项目结构说明.md)

## 接口说明

- `POST /api/auth/register` 注册
- `POST /api/auth/login` 登录
- `GET /api/items` 物品查询（公开）
- `POST /api/items` 发布物品（需登录）
- `POST /api/trades` 发起交易
- `GET /api/admin/stats` 统计数据（管理员）
