<%@include file="../jspf/header.jspf" %>

<h2>Contact Manager</h2>
<form id="newContact" action="addContact.html" method="post">
 
    <table>
    <tr>
        <td><label for="firstname">First Name</label></td>
        <td><input id="firstname" name="firstname" type="text" value=""/></td> 
    </tr>
    <tr>
        <td><label for="lastname">Last Name</label></td>
        <td><input id="lastname" name="lastname" type="text" value=""/></td>
    </tr>
    <tr>
        <td><label for="lastname">Email</label></td>
        <td><input id="email" name="email" type="text" value=""/></td>
    </tr>
    <tr>
        <td><label for="lastname">Telephone</label></td>
        <td><input id="telephone" name="telephone" type="text" value=""/></td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="Add Contact"/>
        </td>
    </tr>
</table>  
     
</form>
<%@include file="../jspf/footer.jspf" %>
