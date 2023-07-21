## 项目简介
基于SpringBoot的任务流处理系统，通过解析json配置文件为任务流，提供插件的形式开发任务组件。

## 开发环境
- maven
- jdk17

## 项目部署
```sh
java --add-opens=java.base/java.net=ALL-UNNAMED -jar runner-1.0-SNAPSHOT.jar --spring.profiles.active=dev 
```

## 代办
- 任务调度
- 任务管理
- 配置管理
- 变量管理
  - [x] 任务变量 
  - [ ] 全局变量 
- 前端图形化（react）
  - [ ] 操作任务启停 
  - [ ] 查看任务状态和日志 
  - [ ] 图形化编辑任务流 

