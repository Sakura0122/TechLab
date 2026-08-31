# TechLab

个人技术实验仓库，用于快速验证、记录并复盘常见技术方案。每个实验以可运行、可观察、可回退为目标，避免为了实验而引入不必要的工程复杂度。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 前端 | Vite、Vue 3、Vue Router、Pinia、Tailwind CSS、Element Plus、`@lucide/vue` |
| 后端 | Spring Boot 3、MyBatis-Plus、Hutool |
| 数据存储 | MySQL；按实验需要接入 Redis、MongoDB 等 |

## 实验主题

- 接口加密：请求签名、载荷加密、解密与错误处理。
- Redis：缓存、数据结构、过期策略与分布式场景验证。
- MongoDB：文档建模、索引、查询与 Spring Boot 集成。
- 前后端协作：鉴权、接口约定、异常响应与调试体验。

实验代码会随验证进展逐步加入；请为每项新增实验保留清晰的入口、必要配置和复现步骤。

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
