# 第七周作业

## 内容

### 使用 Spring Boot 来实现一个整合 Gitee 或者 Github OAuth2 认证
- Servlet

## 完成情况

- [x] 使用 Servlet 来实现一个整合 Gitee OAuth2认证

> 首先需要明确的便是`OAuth2`的定义以及表达内容，这里参考极客时间课程`OAuth 2.0实战课`进行实现。
> 
> 其次需要明确的是本次整合过程在`OAuth2`中是处于哪一部分，根据`OAuth2`角色定义，本次我方应用作为第三方应用、用户为资源拥有者、Gitee认证中心为授权服务。我方需提供一个登陆界面，便于将用户引导至Gitee授权界面，而后需要在服务端提供获取access_token以及受保护资源的代理服务。
> 
> 本次验证使用授权码许可进行。

### 测试

项目启动后，访问：http://localhost:9090/user/oauth

1. 登陆引导页

```html
<head>
<jsp:directive.include
        file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
<div class="container-lg">
<!-- Content here -->
        This is Owner Login site.
<a id="login">Login with Gitee</a>
</div>
<script type="application/javascript">
        // fill in your cliend_id
        const client_id = '86c8fdbbf968eefac50d34fbaa9e2465adb3dbb641170e1298f291bf782e6179';

        const authorize_uri = 'https://gitee.com/oauth/authorize';
        const redirect_uri = 'http://localhost:9090/user/oauth/redirect';

        const link = document.getElementById('login');
        link.href = authorize_uri + "?client_id=" + client_id + "&redirect_uri=" + redirect_uri + "&response_type=code";
</script>
</body>

```

路由端点(UserController)

```java
    @GET
    @Path("/oauth")
    public String oauth(HttpServletRequest request, HttpServletResponse response){
        return "oauth-test.jsp";
    }
```

2. 重定向页面（用于将用户由授权服务带回我方应用，并获取授权码）

```html
<head>
<jsp:directive.include
        file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
<div class="container-lg">
<!-- Content here -->
        This is Owner Login site.
<a id="login">Login with Gitee</a>
</div>
<script type="application/javascript">
        // fill in your cliend_id
        const client_id = '86c8fdbbf968eefac50d34fbaa9e2465adb3dbb641170e1298f291bf782e6179';

        const authorize_uri = 'http://localhost:9090/user/oauth/token';

        console.log(authorize_uri)
        document.location = authorize_uri + "?code=" + getQueryStringByName("code");

        function getQueryStringByName(name){

        var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));

        if(result == null || result.length < 1){

        return "";

        }

        return result[1];

        }
</script>
</body>

```

路由端点(UserController)

```java
    @GET
    @Path("/oauth/redirect")
    public String oauthRedirect(HttpServletRequest request, HttpServletResponse response){
        return "oauth_redirect.jsp";
    }
```

3. access_token获取与用户信息获取

```java
    private final String oAuthTokenUrl = "https://gitee.com/oauth/token?grant_type=authorization_code";
    private final String clientId = "86c8fdbbf968eefac50d34fbaa9e2465adb3dbb641170e1298f291bf782e6179";
    private final String redirectUrl = "http://localhost:9090/user/oauth/redirect";
    private final String clientSecret = "10558e1272c997827a51c374274834d67078e7f8cc94562afa7ba10eba3bd9fc";
    
    @GET
    @Path("/oauth/token")
    public String oauthToken(HttpServletRequest request, HttpServletResponse response){
        String code = request.getParameter("code");
        System.out.println(code);
        StringBuffer buffer = new StringBuffer(oAuthTokenUrl);
        buffer.append("&code=").append(code)
            .append("&client_id=").append(clientId)
            .append("&redirect_uri=").append(redirectUrl)
            .append("&client_secret=").append(clientSecret);
        String targetUrl = buffer.toString();
        System.out.println(targetUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(targetUrl);
        CloseableHttpResponse resp = null;
        try {
            resp = httpClient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
                System.out.println(content);
                AccessToken accessToken = JSONObject.parseObject(content, AccessToken.class);
                System.out.println(accessToken);
                getUserInfo(accessToken);
                return "success.jsp";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return code;
    }

    private void getUserInfo(AccessToken accessToken) throws IOException {
        String targetUrl = "https://gitee.com/api/v5/user?access_token=" + accessToken.getAccess_token();
        System.out.println(targetUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(targetUrl);
        CloseableHttpResponse resp = null;
        try {
            resp = httpClient.execute(httpGet);
            if (resp.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
                System.out.println(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (resp != null){
                resp.close();
            }
            if (httpClient != null){
                httpClient.close();
            }
        }
    }

```

## 参考内容

OAuth 2.0是行业标准的授权协议。OAuth 2.0侧重于客户端开发者的简单性，同时为 web 应用程序、桌面应用程序、移动电话和起居室设备提供特定的授权流程。

### 角色

#### 资源拥有者（Resource Owner）

顾名思义，资源的所有者，很多时候其就是我们普通的自然人（但不限于自然人，如某些应用程序也会创建资源），拥有资源的所有权。一般情况下，可以将其当做用户看待。

#### 客户端（Client）

准备访问用户资源的应用程序，其可能是一个web应用，或是一个后端web服务应用，或是一个移动端应用，也或是一个桌面可执行程序。一般情况下，均指代第三方软件。

#### 授权服务（Authorization Server —> 授权+鉴权）

一句话概括，授权服务就是负责颁发访问令牌的服务。更进一步地讲，OAuth 2.0 的核心是授权服务，而授权服务的核心就是令牌。

而在授权码许可类型中，授权服务的工作，可以划分为两大部分，一个是颁发授权码 code，一个是颁发访问令牌 access_token。

#### 受保护资源
受保护资源是什么？它可以是任何的资源，无论是直接的资源还是间接的资源。但在我们讨论的范畴里（Web），受保护资源指代的仍然是资源，不过期最终呈现的方式是Web API（在Web 交互中，任何的资源都可以被抽象为API的方式供外部访问，因为此时便可以做到资源无关性。比如说，访问头像的 API、访问昵称的 API）。也就是说，Web API是受保护资源对外访问能力的表现，具体的载体；而受保护资源从未发生过性质的转移，它还是它。