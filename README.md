# 最小 RPC

## consumer: 消费者
调用 provider 暴露的 api， 从 注册中心 获取 provider 连接信息，通过 rpc生成的代理 对 provider 发起调用请求


## provider：服务提供者
实现了 api，并 向注册中心注册自己的服务，监听请求，根据请求调用服务和方法 在 注册中心找到对应的服务 -> invoke


## rpc: 提供代理获取，抽象传输对象，抽象调用者
for consumer：扫描 consumer 声明使用的 RPC 接口，生成其代理类

for provider：扫描 provider 声明实现的 RPC 服务，注册到注册中心


### 2020-12-04 update
native socket -> netty