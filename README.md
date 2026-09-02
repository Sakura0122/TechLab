# TechLab

个人技术实验仓库，用于快速验证、记录并复盘常见技术方案。每个实验以可运行、可观察、可回退为目标，避免为了实验而引入不必要的工程复杂度。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 前端 | Vite、Vue 3、Vue Router、Pinia、Tailwind CSS、Element Plus、`@lucide/vue` |
| 后端 | Spring Boot 3、MyBatis-Plus、Hutool |
| 数据存储 | MySQL；按实验需要接入 Redis、MongoDB 等 |

## 实验主题

- 接口加密：AES-256-GCM 双向加密载荷，RSA-OAEP-256 加密每请求会话密钥。
- 数据字典：Spring Boot、MyBatis-Plus 与 Vue 3 实现字典及字典项的整体增删改查。
- Redis：缓存、数据结构、过期策略与分布式场景验证。
- MongoDB：文档建模、索引、查询与 Spring Boot 集成。
- 前后端协作：鉴权、接口约定、异常响应与调试体验。

实验代码会随验证进展逐步加入；请为每项新增实验保留清晰的入口、必要配置和复现步骤。

### 接口双向加密

后端启动时生成临时 RSA-3072 密钥对，前端通过
`GET /api-encryption/public-key` 获取公钥。调用带有 `@ApiEncrypted` 的接口时，
前端请求配置 `apiEncrypted: true`，即可为每次请求生成 AES-256 密钥并自动完成
请求加密和响应解密。

示例入口：

- 页面：启动前后端后访问前端首页。
- 接口：`POST /test/encryption`。
- 配置：`tech-lab.api-encryption.enabled`，也可通过
  `API_ENCRYPTION_ENABLED=false` 关闭；关闭后示例接口使用普通 JSON。

公钥协商接口保持明文，其余加密接口使用 96-bit IV、128-bit GCM
认证标签，并通过时间戳和内存中的 `requestId` 记录拒绝过期或重复请求。

### 数据字典

执行 [`sql/data_dictionary.sql`](sql/data_dictionary.sql) 初始化数据表，启动前后端后访问
`/dictionary`。列表支持按名称、编号和类型搜索；新增与编辑通过同一抽屉整体维护字典信息及其字典项。

接口入口：

- `GET /dictionaries`：分页查询。
- `GET /dictionaries/{id}`：查询字典及字典项详情。
- `POST /dictionaries`：新增字典及字典项。
- `PUT /dictionaries/{id}`：整体编辑字典及字典项。
- `DELETE /dictionaries/{id}`：删除字典及其字典项。

## 目录结构

```text
.
├── frontend/    # Vue 3 前端
├── backend/     # Spring Boot 后端
├── AGENTS.md    # 协作与开发约定
└── README.md    # 项目说明
```

## 本地运行

### 前端

前置条件：Node.js `^22.18.0 || >=24.12.0`、pnpm。

```bash
cd frontend
pnpm install
pnpm dev
```

常用命令：

```bash
pnpm type-check
pnpm build
pnpm lint
```

### 后端

前置条件：JDK 25、MySQL。默认连接地址为 `jdbc:mysql://localhost:3306/tech_lab`，服务端口为 `8123`。

可通过以下环境变量覆盖数据库和端口配置：

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/tech_lab'
export SPRING_DATASOURCE_USERNAME='root'
export SPRING_DATASOURCE_PASSWORD='你的数据库密码'
export SERVER_PORT='8123'
```

```bash
cd backend
mvn spring-boot:run
```

## 贡献约定

这是个人实验仓库。提交改动时，请保持范围聚焦，为新增实验补充必要说明，并避免提交密钥、密码或其他本地敏感配置。详见 [AGENTS.md](AGENTS.md)。
