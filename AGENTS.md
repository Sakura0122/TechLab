# TechLab 开发约定

## 项目定位

TechLab 是个人技术实验仓库，用于验证和沉淀可复现的技术方案，例如接口加密、Redis、MongoDB 及相关前后端集成。这里的代码以实验目标清晰、最小可运行和便于复盘为先，不将实验代码提前抽象为通用框架。

## 目录与技术栈

- `frontend/`：Vite、Vue 3、Vue Router、Pinia、Tailwind CSS、Element Plus、`@lucide/vue`。
- `backend/`：Spring Boot 3、MyBatis-Plus、Hutool，默认使用 MySQL。

## 开发原则

- 每次改动应直接服务于一个明确实验或问题，避免无关重构、格式化和依赖升级。
- 新增实验时，在对应模块内按现有结构组织代码，并在根目录 README 的“实验主题”中补充入口或说明。
- 配置应优先通过环境变量覆盖；不要提交真实密码、令牌或本地环境配置。
- 前端沿用 Composition API 与 TypeScript，优先复用项目已配置的自动导入、Element Plus 和 Lucide 图标。
- 后端统一使用既有的响应、异常处理和分页模型；持久层优先使用 MyBatis-Plus。
- 除非需求明确要求，不创建或修改测试文件。

## 验证

- 前端改动：在 `frontend/` 执行 `pnpm type-check`；涉及构建配置或生产产物时执行 `pnpm build`。
- 后端改动：在 `backend/` 执行 `mvn test`；仅在需求允许新增或调整测试时才修改测试源码。
- 无法执行验证时，说明原因和未验证范围。
