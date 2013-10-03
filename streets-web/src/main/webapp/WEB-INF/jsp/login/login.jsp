<c:if test="${not empty error}">
    <div style="text-align: center; color: #FF0000">
        Your login attempt was not successful, try again.<br /> Reason :
        ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
    </div>
</c:if>


<form name='f' action="<c:url value='/doLogin' />" method='POST'>

    <table>
        <tr><td>Username:</td><td><input type='text' name='j_username'></td></tr>
        <tr><td>Password:</td><td><input type='password' name='j_password' /></td></tr>
        <tr><td colspan='2'><input name="submit" type="submit" value="submit" /></td></tr>
    </table>

</form>