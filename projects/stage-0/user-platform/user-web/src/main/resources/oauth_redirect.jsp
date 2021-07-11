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