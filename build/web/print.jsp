<%@page language="java" session="true" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="registrationboardroom.Portal"%>
<%@page import="registrationboardroom.Browser"%>
<%@ taglib uri="/OverrideTag.tld" prefix="newsection" %>
<%

    String sessionId=(String)session.getAttribute("sessionId");
    Browser browser=Portal.getCache().getBrowser(sessionId);

%>
<newsection:over name="printcontent">
<%
    out.println(browser.getWeekTable(browser.deltaweeks,browser.tableSetting,browser.boardroom_id));
%>
</newsection:over>
<newsection:over name="extrahead">
</newsection:over>
<%@ include file="printbase.jsp" %>
<script type="text/javascript">

function printConfirmation() {
    var answer = confirm("Do you want to print this timeline?");
    if (answer){
        window.print();
    }
}

window.onload=new Function(printConfirmation());
</script>