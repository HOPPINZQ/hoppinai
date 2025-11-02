### 须知

#### 1、如果你想要启动AI模块，先启动注册中心，再启动该模块，然后你就不用管了

#### 2、短信服务接的腾讯云Sms

#### 3、单点登录支持gitee、weibo、qq、github、网易云音乐，其他除了网易云音乐，你都要去不同 的开放平台申请一些Oauth2协议要求的参数。

网易云音乐采用CSRF，需要保证我的服务稳定：http://hoppin.cn:3000/

#### 4、其他模块需要配置本服务的地址，引入网关模块就可以自动鉴权了（接口上通过注解@ApiMapping，通过声明RoleType来配置权限）

```yaml
auth:
  ssoUrl: http://127.0.0.1:8812/service
```

#### 5、网关通过api模块来调用auth服务来鉴权，你不用网关的话，自己引api鉴权，示例代码如下：

```java
UserPrincipal upp=new UserPrincipal("用户名","密码"); // 用之前配置的zhangqi\123456 就行
        AuthService authService=ServiceProxyFactory.createProxy(AuthService.class,"auth服务地址，就是上面配置的ssoUrl",upp);
        Boolean ok=authService.isToken("用户ID");
        if(!ok){
        // 鉴权失败
        }
// 鉴权成功
```

其中用户ID需要通过请求头获取，需要解析JWT的载荷部分。示例代码如下：

```java
String token=request.getHeader("Authorization");
        String user=null;
        if(token!=null){
        try{
        if(token.indexOf("\"")!=-1){
        token=token.replace("\"","");
        }
        JwtParser jwtParser=Jwts.parser();
        //传入签名,对jwtToken解密
        Jws<Claims> claimsJws=jwtParser.setSigningKey(UserSignature.signature).parseClaimsJws(token);
        Claims claims=claimsJws.getBody();
        user=claims.get("user").toString();
        }catch(Exception e){
        user=null;
        }
        }
// 取user里的id，取不到或者报错表示jwt没传、格式不对、过期、签名错误等，都视为未登录
```
