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