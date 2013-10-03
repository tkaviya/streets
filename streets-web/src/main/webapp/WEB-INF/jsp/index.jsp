
<%--<embed src="resources/streets_theme.mp3" type="audio/mpeg" autostart="true" hidden="false"/>--%>
<%--<noembed><bgsound src="resources/streets_theme.mp3" type="audio/mpeg" autostart="true" hidden="false" /></noembed>--%>

<table width="400px" style="vertical-align: top; font-weight: bold; padding: 10px; font-family: DEJAVU SANS MONO,monospace; ">
    <tr>
        <td style="text-align: left">
            <audio loop="loop" controls>
                <source src="${pageContext.request.contextPath}/resources/streets_theme.mp3" type="audio/mpeg; codecs='mp3'" onended="">
                <embed height="50px" width="100%" src="${pageContext.request.contextPath}/resources/streets_theme.mp3">
                <noembed>
                    <bgsound src="${pageContext.request.contextPath}/resources/streets_theme.mp3" type="audio/mpeg" autostart="true">
                    </bgsound>
                </noembed>
            </audio>
        </td>
        <td style="text-align: right;">
            <a href="<c:url value='/doLogout' />">LOGOUT</a>
        </td>
    </tr>
    <tr>
        <td colspan="2" style="text-align: center; text-transform: uppercase; font-size: 11px">
            Welcome ${userSession.user.name}<br />
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <table width="100%">
                <tr>
                    <td style="text-align: left; text-transform: uppercase; font-size: 10px; width: 50%">
                        Gang: <b>${userSession.user.userAttribute.gangName}</b><br />
                    </td>
                    <td style="text-align: left; text-transform: uppercase; font-size: 10px; width: 50%">
                        Location: <b>${userSession.user.userAttribute.location.locationName}</b><br />
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<table width="400px" height="80%" style="
    font-family: sans-serif; font-size: 14px; vertical-align: top;
    background: #333333; height: 80%; text-align: left;">
    <tr>
        <td colspan="2">
            <img src="${pageContext.request.contextPath}/resources/blaklizt_logo.jpg" width="100%"/>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <div id="eventDiv" name="eventDiv">data!</div>
        </td>
    </tr>
    <tr height="100%" style="padding: 20px; vertical-align: top; ">
        <td colspan="2">
            <div id="gameMenu" name="gameMenu">
                <%=
                    request.getSession().getAttribute("userSession") == null ? "" :
                            ((UserSession)request.getSession().getAttribute("userSession")).getCurrentMenu() == null ? "" :
                                    ((UserSession)request.getSession().getAttribute("userSession")).getCurrentMenu().toString()
                                            .replaceAll("\r\n", "<br />")
                %>
            </div>
        </td>
    </tr>
    <tr>
        <td style="vertical-align: bottom; width: 250px;">
            <input type="text" name="menu_response" id="menu_response" onkeypress="checkSubmit(event)"
             style="width: 100%; height:30px; box-sizing: border-box; -webkit-box-sizing:border-box; -moz-box-sizing: border-box;"/>
        </td>
        <td style="vertical-align: bottom; text-align: right;">
            <input type="button" name="sendBtn" id="sendBtn" value="Send Response" onclick="doAjaxPost()"
             style="width: 100%; height:30px; box-sizing: border-box; -webkit-box-sizing:border-box; -moz-box-sizing: border-box;"/>
        </td>
    </tr>
</table>
