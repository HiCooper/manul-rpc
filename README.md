# 最小 RPC

## consumer: 消费者
责任：服务调用方，通过 RPC 发起调用

RPC 在 这里的责任：扫描 consumer 声明使用的 RPC 接口， 生成其代理类

## provider：服务提供者
责任：服务提供方， 向注册中心注册自己，实现暴露的 RPC 接口

PRC 在 这里的责任：扫描 provider 声明实现的 RPC 服务，注册到注册中心

consumer 调用 provider 暴露的 api， 从 注册中心 获取 provider 连接信息，通过 rpc生成的代理 对 provider 发起调用请求

provider 实现了 api，并 向注册中心注册自己的服务
