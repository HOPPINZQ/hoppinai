## 基于MCP Server的芋道源码演示

## 如何使用

### 我的远程服务器：

- 1、打开Cherry Studio，配置sse `http://43.142.242.237:48000/sse` ，或者在其他Client端配置

```json
{
  "mcpServers": {
    "芋道源码MCP": {
      "url": "http://43.142.242.237:48000/sse"
    }
  }
}
```

- 2、访问我部署的芋道源码地址：http://hoppinzq.com/yudao/index.html
- 3、用户名密码：admin/admin123 或者跟ai对话 ： AI，登录admin的账号

### 开始对话。示例提示词：

- AI，创建一个用户
- AI，创建一个用户，用户名是zhangqi
- AI，你有哪些组织
- AI，你有那些角色
- AI，给这个用户分配xxx组织
- AI，给这个用户分配xxx角色
- AI，帮我查一下xxx这个用户详情
- AI，登录xxx的账号

#### 最后在芋道源码页面的用户管理查看AI的操作效果

### 本地怎么运行？

#### 如果你想在本地尝试，或者想自己写一些工具

- 1、拉取并启动[芋道源码单体服务版](https://gitee.com/zhijiantianya/ruoyi-vue-pro) 和该项目
- 2、Cherry Studio里配置sse `http://localhost:48000/sse`

