# 初始AI版本 — 私人管家后端服务

私人管家安卓 APP 后端服务，基于 DDD（Domain-Driven Design）四层架构，提供用户体系、日程提醒、记账功能三大核心模块的 RESTful API。

---

## 技术栈

| 层面 | 选型 |
|------|------|
| 语言 | Java 17+ |
| 框架 | Spring Boot 3.2.4 |
| ORM | Spring Data JPA (Hibernate) |
| 数据库 | MySQL 8.0+ |
| 缓存 | Redis (短信验证码 / Token 存储) |
| 认证 | JWT (JJWT 0.12.5) |
| 构建 | Maven |
| 工具 | Lombok, Hutool, EasyExcel |

---

## 项目结构（DDD 四层架构）

```
private-butler-server/
├── pom.xml
├── sql/
│   └── init_schema.sql                          # 6张表 DDL + 9条预设分类
│
└── src/
    ├── main/java/com/privatebutler/
    │   ├── PrivateButlerApplication.java        # 启动入口
    │   │
    │   ├── interfaces/                          # ===== 接口层 =====
    │   │   ├── user/
    │   │   │   ├── UserController.java          # 用户 API (注册/登录/资料)
    │   │   │   ├── UserRegisterRequest.java     # 注册请求 DTO
    │   │   │   ├── UserLoginRequest.java        # 登录请求 DTO
    │   │   │   └── UserProfileVO.java           # 资料视图对象
    │   │   ├── schedule/
    │   │   │   ├── ScheduleController.java      # 日程 API (CRUD/搜索/提醒)
    │   │   │   ├── ScheduleCreateRequest.java   # 日程请求 DTO
    │   │   │   └── ScheduleVO.java              # 日程视图对象
    │   │   └── bookkeeping/
    │   │       ├── BookkeepingController.java   # 记账 API (CRUD/统计)
    │   │       ├── RecordCreateRequest.java     # 记账请求 DTO
    │   │       └── RecordVO.java                # 记账视图对象
    │   │
    │   ├── application/                         # ===== 应用层 =====
    │   │   ├── user/
    │   │   │   ├── UserRegisterService.java     # 注册用例 (验证码/密码强度/协议)
    │   │   │   ├── UserLoginService.java        # 登录用例 (5次锁定/记住密码/找回密码)
    │   │   │   └── UserProfileService.java      # 资料编辑用例
    │   │   ├── schedule/
    │   │   │   ├── ScheduleService.java         # 日程 CRUD 用例
    │   │   │   └── ReminderService.java         # 提醒触发用例
    │   │   ├── bookkeeping/
    │   │   │   ├── RecordService.java           # 记账 CRUD 用例
    │   │   │   └── StatisticsService.java       # 收支统计用例
    │   │   └── common/
    │   │       └── AuthApplicationService.java  # 认证辅助服务
    │   │
    │   ├── domain/                              # ===== 领域层 =====
    │   │   ├── common/
    │   │   │   ├── BaseEntity.java              # 基础实体 (ID/时间/状态)
    │   │   │   └── BaseRepository.java          # 基础仓库接口
    │   │   ├── user/                            # 用户上下文
    │   │   │   ├── entity/User.java             # 用户实体 (登录状态/锁定逻辑)
    │   │   │   ├── aggregate/UserAggregate.java # 用户聚合根
    │   │   │   ├── valueobject/
    │   │   │   │   ├── PhoneNumber.java         # 手机号值对象 (格式校验)
    │   │   │   │   ├── Password.java            # 密码值对象 (强度评估)
    │   │   │   │   └── UserProfile.java         # 资料值对象
    │   │   │   ├── service/
    │   │   │   │   └── PasswordStrengthService.java
    │   │   │   └── repository/
    │   │   │       └── UserRepository.java
    │   │   ├── schedule/                        # 日程上下文
    │   │   │   ├── entity/
    │   │   │   │   ├── Schedule.java            # 日程实体 (提醒时间/重复规则)
    │   │   │   │   └── RepeatRule.java          # 重复规则 (每日/每周/每月)
    │   │   │   ├── aggregate/ScheduleAggregate.java
    │   │   │   ├── valueobject/
    │   │   │   │   ├── ScheduleTime.java        # 日程时间 (过去时间校验)
    │   │   │   │   ├── ReminderTime.java        # 提醒时间 (5m/10m/30m/1h/1d)
    │   │   │   │   └── ScheduleCategory.java    # 日程分类枚举
    │   │   │   ├── service/
    │   │   │   │   └── ReminderTriggerService.java
    │   │   │   └── repository/
    │   │   │       └── ScheduleRepository.java
    │   │   └── bookkeeping/                     # 记账上下文
    │   │       ├── entity/
    │   │       │   ├── Record.java              # 记账记录 (收支/金额/分类)
    │   │       │   └── CustomCategory.java      # 自定义分类
    │   │       ├── aggregate/BookkeepingAggregate.java
    │   │       ├── valueobject/
    │   │       │   ├── Amount.java              # 金额值对象 (范围校验/精度)
    │   │       │   ├── RecordType.java          # 收支类型枚举
    │   │       │   └── Category.java            # 分类值对象
    │   │       ├── service/
    │   │       │   └── StatisticsDomainService.java
    │   │       └── repository/
    │   │           ├── RecordRepository.java
    │   │           └── CustomCategoryRepository.java
    │   │
    │   └── infrastructure/                      # ===== 基础设施层 =====
    │       ├── persistence/                     # JPA 实现
    │       │   ├── user/ (UserJpaMapper, UserRepositoryImpl)
    │       │   ├── schedule/ (ScheduleJpaMapper, ScheduleRepositoryImpl)
    │       │   └── bookkeeping/ (RecordJpaMapper, CategoryJpaMapper, *Impl)
    │       ├── security/
    │       │   ├── JwtTokenProvider.java        # JWT 令牌生成/验证
    │       │   ├── PasswordEncoder.java         # MD5 密码加密
    │       │   └── SmsCodeManager.java          # 短信验证码管理 (Redis)
    │       └── common/
    │           ├── ApiResponse.java             # 统一响应封装
    │           └── GlobalExceptionHandler.java  # 全局异常处理
    │
    └── test/java/com/privatebutler/
        ├── PrivateButlerTestApplication.java    # 测试启动类
        ├── config/
        │   └── TestSecurityConfig.java          # 测试安全配置 (绕过认证)
        └── interfaces/
            ├── user/UserControllerTest.java     # 用户接口集成测试 (10 用例)
            ├── schedule/ScheduleControllerTest.java  # 日程接口集成测试 (10 用例)
            └── bookkeeping/BookkeepingControllerTest.java  # 记账接口集成测试 (10 用例)
```

---

## 数据库设计（6 张表）

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `sys_user` | 用户账号 | phone, password, login_fail_count, lock_time |
| `sys_user_profile` | 用户资料 | nickname, avatar, gender, birthday, 提醒设置 |
| `sch_schedule` | 日程 | title, schedule_date/time, remind_before, category, repeat_* |
| `bk_record` | 记账记录 | type(收支), amount, category_id, category_name, record_date |
| `bk_category` | 自定义分类 | name, type, color, sort_order (含9条系统预设) |
| `sys_sms_code` | 短信验证码 | phone, code, type, expires_at |

---

## 核心功能

### 用户体系
- **注册** — 手机号 + 短信验证码 + 密码强度校验 + 协议勾选
- **登录** — 密码错误 5 次锁定 1 小时 + 记住密码
- **找回密码** — 短信验证码验证后重置
- **资料编辑** — 昵称/头像/性别/生日
- **JWT 认证** — Token 存储于 Redis，3 天有效期

### 日程提醒
- **CRUD** — 创建/编辑/删除日程，过去时间校验
- **提醒时间** — 5m / 10m / 30m / 1h / 1d
- **分类筛选** — 工作 / 生活 / 重要事项 / 其他
- **关键词搜索** — 按标题模糊匹配
- **重复提醒** — 每日 / 每周(选星期) / 每月(选日期) / 无限期或自定义结束
- **日期筛选** — 全部 / 今日 / 按日期

### 记账功能
- **收支录入** — 金额精度 2 位小数，范围 0.01 ~ 999999.99
- **类型区分** — 支出(红色 `-`) / 收入(绿色 `+`)
- **自定义分类** — 1-8 字符，支持颜色，上限 10 个
- **分类筛选** — 按收支类型 + 具体分类
- **日期范围筛选** — 自定义起止日期
- **收支统计** — 收入/支出总额 + 各分类占比

---

## API 端点

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/user/sms-code` | 发送短信验证码 | 否 |
| POST | `/api/user/register` | 用户注册 | 否 |
| POST | `/api/user/login` | 用户登录 | 否 |
| POST | `/api/user/logout` | 退出登录 | 是 |
| POST | `/api/user/reset-password` | 重置密码 | 否 |
| GET | `/api/user/profile` | 获取个人资料 | 是 |
| PUT | `/api/user/profile` | 更新个人资料 | 是 |
| POST | `/api/schedule` | 创建日程 | 是 |
| PUT | `/api/schedule/{id}` | 更新日程 | 是 |
| DELETE | `/api/schedule/{id}` | 删除日程 | 是 |
| GET | `/api/schedule/{id}` | 查询日程详情 | 是 |
| GET | `/api/schedule/list` | 日程列表(筛选) | 是 |
| GET | `/api/schedule/reminders` | 检查待提醒日程 | 是 |
| POST | `/api/bookkeeping/record` | 创建记账记录 | 是 |
| PUT | `/api/bookkeeping/record/{id}` | 更新记账记录 | 是 |
| DELETE | `/api/bookkeeping/record/{id}` | 删除记账记录 | 是 |
| GET | `/api/bookkeeping/record/{id}` | 查询记账记录 | 是 |
| GET | `/api/bookkeeping/records` | 记账记录列表(筛选) | 是 |
| GET | `/api/bookkeeping/statistics` | 收支统计 | 是 |

---

## 测试覆盖（30 个集成测试）

| 测试类 | 用例数 | 验证方式 |
|--------|--------|---------|
| `UserControllerTest` | 10 | HTTP状态码 + JSON响应 + Repository查DB |
| `ScheduleControllerTest` | 10 | HTTP状态码 + JSON响应 + Repository查DB |
| `BookkeepingControllerTest` | 10 | HTTP状态码 + JSON响应 + Repository查DB |

所有测试连接真实 MySQL 数据库，执行业务逻辑后通过 Repository 验证数据持久化结果，`@Transactional` 自动回滚。

---

## 快速启动

```bash
# 1. 创建数据库
mysql -u root -p < sql/init_schema.sql

# 2. 修改配置
vim src/main/resources/application.yml   # 数据库密码、Redis

# 3. 启动
mvn spring-boot:run

# 4. 运行测试
mvn test
```
