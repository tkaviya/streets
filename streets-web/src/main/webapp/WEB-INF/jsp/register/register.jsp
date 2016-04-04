<c:if test="${not empty error}">
    <div style="text-align: center; color: #FF0000">
        Your registration attempt was not successful, try again.<br /> Reason : ${error}
    </div>
</c:if>


<form name='registerForm' action="<c:url value='/doRegister' />" method='POST'>

    <table>
        <tr><td>Name:</td><td><input type='text' name='r_name'></td></tr>
        <tr><td>Username:</td><td><input type='text' name='r_username'></td></tr>
        <tr><td>Type Password:</td><td><input type='password' name='r_password1' /></td></tr>
        <tr><td>Repeat Password:</td><td><input type='password' name='r_password2' /></td></tr>
        <tr><td>Email:</td><td><input type='email' name='r_email' /></td></tr>
        <tr><td colspan='2'><input name="submit" type="submit" value="submit" /></td></tr>
    </table>

</form>