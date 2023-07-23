<%@page language="java" session="true" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="registrationboardroom.Table"%>
<%@page import="registrationboardroom.Portal"%>
<%@page import="registrationboardroom.Browser"%>
<%@page import="registrationboardroom.TableSetting"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib uri="/OverrideTag.tld" prefix="newsection" %>
<newsection:over name="title"> - Administration</newsection:over>
<%

    Portal portal=(Portal) request.getAttribute("portal");  // Get main HttpServlet instance
    String section=(String) request.getParameter("section"); // Get section parameter from url
    String subsection=(String) request.getParameter("subsection"); // Get subsection parameter from url
    session.setMaxInactiveInterval((60*Portal.getCache().getSessionTimeoutMinutes()));  // Set session expiration interval from Cache settings
    String myname =  (String)session.getAttribute("username"); // Get session atribude
    String sessionId=(String)session.getAttribute("sessionId");
    Enumeration ids;
    Browser browser=null;
    if (sessionId==null) {
       sessionId=(String)session.getId();
       session.setAttribute("sessionId",sessionId);
       browser=Portal.getCache().addBrowser(session);
    }
    else {
       browser=Portal.getCache().getBrowser(sessionId);
    }

%>

<newsection:over name="extrahead">
<%@ include file="javascript.jsp" %>
<%

if (browser!=null)
    if (!browser.getAction().equals("")) {
        if (browser.getAction().equals("checklogin")) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            if (username.equals("")) {
                %>
                <script type="text/javascript">
                    window.onload=new Function(show_alert("You didn't enter phone number!"));
                </script>
                <%
            } else if (password.equals("")) {
                %>
                <script type="text/javascript">
                    window.onload=new Function(show_alert("You didn't enter password!"));
                </script>
                <%
            }
            else
                switch (browser.login(username, password)) {
                    case Browser.LOGIN_OK:
                        if (username.equals("admin")) {
                            session.setAttribute("username", "Admin");
                            myname="Admin";
                        }
                        else {
                            session.setAttribute("username", browser.getUsername());
                            myname=browser.getUsername();
                        }
                        break;
                    case Browser.WRONG_PASSWORD:
                        %>
                            <script type="text/javascript">
                                window.onload=new Function(show_alert("Wrong password!"));
                            </script>
                        <%
                        break;
                    case Browser.WRONG_USERNAME:
                        %>
                            <script type="text/javascript">
                                window.onload=new Function(show_alert("Wrong phone number!"));
                            </script>
                        <%
                        break;
                    case Browser.ALREADY_LOGGED:
                        %>
                            <script type="text/javascript">
                                window.onload=new Function(show_alert("You are already logged in!"));
                            </script>
                        <%
                        break;
                }
            browser.cancelAction();
            }
        

        else if (browser.getAction().equals("newusername")) {
            session.removeAttribute("username");
            session.setAttribute("username", browser.getNewUsername());
            myname=browser.getNewUsername();
            browser.cancelAction();
        }
        else if (browser.getAction().equals("error")) {
                %>
                <script type="text/javascript">
                    window.onload=new Function(show_alert('<% out.print(browser.getError()); %>'));
                </script>
                <%
            browser.cancelAction();
        }

        else if (browser.getAction().equals("logout")) {
            String username=(String)session.getAttribute("username");

            if(username!=null) {
                myname=null;
                session.removeAttribute("username");
                Portal.getCache().removeUsername(username);
            }
        }
    }
%>
</newsection:over>
<newsection:over name="bar">
<div class="bar">
<%

if(myname!=null) 
    if (myname.equals("Admin")) {

%>
<form class="zeros menu right" action="./portal" method="post">
    <a class="leftseparator" href="#" onclick="showNewBoardroomLayer();"><img src="plus.gif" alt="plus" /></a>
    <a href="#" onclick="showEditBoardroomLayer();"><img src="edit.gif" alt="edit" /></a>
    <a class="rightseparator" href="#" onclick="deleteBoardroomConfirmation();"><img src="delete.gif" alt="delete" /></a>
</form>
    <%
    }
    %>
<form class="zeros menu right" id="changeboardroomform"action="./portal" method="post">
    <b class="zeros menutitle">Boardroom:</b>
    <select class="zeros boardrooms" name="boardrooms" onchange="document.getElementById('changeboardroomform').submit()">
<%

ids = browser.boardrooms.keys();
while (ids.hasMoreElements()) {
    Integer id = (Integer)ids.nextElement();
    String name = (String)browser.boardrooms.get(id);
    out.println("<option value=\""+id+"\"");
    if (browser.boardroom_id==id.intValue())
        out.println("selected");
    out.println(">"+name+"</option>");
}

%>
    </select>
    <input type="hidden" name="command" value="changeboardroom" />
</form>
</div>
<%

if(myname!=null) {
    if (myname.equals("Admin")) {

%>
<div class="bar">
<form class="zeros menu right" action="./portal" method="post">
    <a class="leftseparator" href="#" onclick="showNewUserLayer();"><img src="plus.gif" alt="plus" class="menubarimage" /></a>
    <a href="#" onclick="showEditUserLayer();"><img src="edit.gif" alt="edit" class="menubarimage"/></a>
    <a class="rightseparator" href="#" onclick="deleteUserConfirmation();"><img src="delete.gif" alt="delete" class="menubarimage"/></a>
</form>
<form class="zeros menu right" id="changeuserform" action="./portal" method="post">
<b class="zeros menutitle">User:</b>
<select class="zeros boardrooms" name="user" onchange="this.form.submit();">
<%

browser.loadUsers();
ids = browser.getUsers().keys();
while (ids.hasMoreElements()) {
    Integer id = (Integer)ids.nextElement();
    String name = (String)browser.getUsers().get(id);
    out.println("<option value=\""+id+"\"");
    if (browser.getHistoryUserId()==id.intValue())
        out.println("selected");
    out.println(">"+name+"</option>");
}

%>
</select>
<input type="hidden" name="command" value="history" />
</form>
</div>
<%

    }
    else {

%>
<div class="bar">
<form id="showhistoryform" class="zeros menu right" action="./portal" method="post">
    <input type="hidden" name="command" value="history" />
    <input type="hidden" name="user" value="<% out.print(browser.getUserId()); %>" />
    <a href="#" onclick="document.forms['showhistoryform'].submit();">History</a>
</form>
</div>
<%
    }
}
%>
</newsection:over>
<newsection:over name="mainbar">
<%

if(myname!=null) {
    if (myname.equals("Admin")) {

%>
<form class="zeros mainmenubar right" id="logoutform" action="./portal" method="post">
    <input type="hidden" name="command" value="logout" />
    <a href="#" onclick="showNewEventLayer();">New Event</a>
    <a href="#" onclick="showSettingsLayer();">Table Settings</a>
    <a href="#" onclick="showChangePasswordLayer();">Account Settings</a>
    <a href="#" onclick="showStatisticsLayer();">Statistics</a>
    <a href="#" onclick="document.forms['logoutform'].submit();"><%out.println("Logout "+myname);%></a>
</form>
<%

    }
    else {

%>
<form id="logoutform" class="zeros mainmenubar right" action="./portal" method="post">
    <input type="hidden" name="command" value="logout" />
    <a href="#" onclick="showNewEventLayer();">New Event</a>
    <a href="#" onclick="showSettingsLayer();">Table Settings</a>
    <a href="#" onclick="showChangePasswordLayer();">Account Settings</a>
    <a href="#" onclick="document.forms['logoutform'].submit();"><%out.println("Logout "+myname);%></a>
</form>
<%

    }
}
else {
    if (!browser.getAction().equals("")) {
        if (browser.getAction().equals("logout")) {

%>
<a href="#" onclick="location.href='./portal'">Login again</a>
<%

        }
    }
    else {

%>
<form id="loginform" class="zeros" action="./portal" method="post">
    <input type="hidden" name="command" value="checklogin" />
    <b style="color:black;font-size:11px;"class="zeros left">Phone number:</b>
    <input class="zeros" name="username" size=15 />
    <b style="color:black;font-size:11px;" class="zeros left">Password:</b>
    <input class="zeros" name="password" size=15 type="password" onkeypress="return submitenter(document.forms['loginform'],event)"/>
    <a href="#" onclick="document.forms['loginform'].submit();">Login</a>
</form>  
    <%

    }
}

%>
</newsection:over>
<newsection:over name="content">
<div class="zeros">
<%

if (section==null) {
    %>
            <center class="zeros">
    <%

    switch(browser.getActiveCategory()){
        case Browser.BOARDROOMS:
            if (browser.boardroom_id!=0)
            out.println(browser.getWeekTable(browser.deltaweeks,browser.tableSetting,browser.boardroom_id));

            %>
            <br>
            <br>
            <%

            if(myname!=null) {
                if (myname.equals("Admin")) {
                   out.println(browser.getWeekReservations(true,browser.user_id, browser.deltaweeks, browser.tableSetting, browser.boardroom_id));
                }
                else {
                   out.println(browser.getWeekReservations(false,browser.user_id, browser.deltaweeks, browser.tableSetting, browser.boardroom_id));
                }
            }
            break;
        case Browser.USERS:
            if (!browser.getAction().equals("")) {
                if (browser.getAction().equals("showhistory")) {
                    out.println(browser.getHistory());
                    browser.cancelAction();
                }
            }

            %>
            <br>
            <br>
            <%
            break;
        case Browser.NONE:
            %>
            <%@ include file="/sections/main.jsp" %>
            <%
            break;
    }
    %>
            </center>
    <%
}
else {
    if (section.equals("overview")) {
    %>
    <newsection:over name="sidebar">
    <div class="sidebar1">
        <ul class="nav">
            <li><a href="./portal?section=overview&subsection=registrationboardroom">Registration Boardroom</a></li>
            <li><a href="./portal?section=overview&subsection=openstage">Openstage</a></li>
        </ul>
        <p></p>
    </div>
    <div class="sidebar1content">
       <% if (subsection==null) { %>
        <%@ include file="/sections/registrationboardroom.jsp" %>
        <%} else if (subsection.equals("openstage")) { %>
            <%@ include file="/sections/openstage.jsp" %>
        <%} else if (subsection.equals("registrationboardroom")) { %>
            <%@ include file="/sections/registrationboardroom.jsp" %>
        <%} %>
    </div>
    </newsection:over>
    <%
    }
    else if (section.equals("about")) {
    %>
        <newsection:over name="sidebar">
    <div class="sidebar1">
        <ul class="nav">
            <li><a href="./portal?section=about&subsection=platform">Platform</a></li>
            <li><a href="./portal?section=about&subsection=authors">Authors</a></li>
        </ul>
        <p></p>
    </div>
    <div class="sidebar1content">
       <% if (subsection==null) { %>
        <%@ include file="/sections/authors.jsp" %>
        <%} else if (subsection.equals("platform")) { %>
            <%@ include file="/sections/platform.jsp" %>
        <%} else if (subsection.equals("authors")) { %>
            <%@ include file="/sections/authors.jsp" %>
        <%} %>
    </div>
    </newsection:over>
        
    <%
    }
    if (section.equals("administration")) {
    %>
        <%@ include file="/sections/main.jsp" %>
    <%
    }
}

%>
<div id="bg_mask">
        <div id="changepasswordlayer" class="layer changepassword">
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideChangePasswordLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>Account Settings</h3>
                        </center>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                        <h4>Change Password</h4>
                        </center>
                    </td>
                </tr>
                <form id="changepassword" action="./portal" method="post">
                <input type="hidden" name="command" value="changepassword" />
                <tr>
                    <td style="vertical-align:middle;">
                        Current Password:
                    </td>
                    <th>
                       <input style="float:left;" type="password" name="current_password" value="" />
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        New Password:
                    </td>
                    <th>
                       <input style="float:left;" type="password" name="new_password1" value="" />
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Again:
                    </td>
                    <th>
                       <input style="float:left;" type="password" name="new_password2" value="" />
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="submit" value="Change"/>
                        </center>
                    </td>
                </tr>
                </form>
                <form id="changename" action="./portal" method="post">
                <input type="hidden" name="command" value="changename" />
                <tr>
                    <td colspan="2" >
                    </td>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                        <h4>Change Name</h4>
                        </center>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Name:
                    </td>
                    <th>
                       <input style="float:left;" type="text" name="new_name" value="<% out.println(myname); %>"/>
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="submit" value="Change"/>
                        </center>
                    </td>
                </tr>
                </form>
            </table>
        </div>

        <div id="statisticslayer" class="layer" style="height:<%out.print(Integer.valueOf(550+(Portal.getCache().getSessions()-1)*36)); %>px;">
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideStatisticsLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>Statistics</h3>
                        </center>
                    </td>
                </tr>
            </table>
            <br>
            <center><% out.println(Portal.getCache().getStats()); %></center>
        </div>

        <div id="settingslayer" class="layer settings">
            <form action="./portal" method="post">
            <input type="hidden" name="command" value="settings" />
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideSettingsLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>Table Settings</h3>
                        </center>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Timezone:
                    </td>
                    <th>
                    <select name="timezone" style="width:130px;float:left;">
                    <%
                    for (int i=0;i<portal.timezones.length;i++) {
                        out.println("<option value=\""+portal.timezones[i]+"\"");
                        if (portal.timezones[i].equals(browser.tableSetting.getTimezone()))
                            out.println("selected");
                        out.println(">"+portal.timezones[i]+"</option>");
                    }
                    %>
                    </select>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Start Hour:
                    </td>
                    <th>
                       <select name="starthour" style="width:50px;float:left;">
                           <%
                           for (int i=0;i<24;i++) {
                               out.println("<option value=\""+String.valueOf(i)+"\"");
                               if (browser.tableSetting.getStartHour()==i)
                                   out.println(" selected ");
                               out.println(">"+String.valueOf(i)+"</option>");
                           }
                           %>
                       </select>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        End Hour:
                    </td>
                    <th>
                       <select name="endhour" style="width:50px;float:left;">
                           <%
                           for (int i=0;i<24;i++) {
                               out.println("<option value=\""+String.valueOf(i)+"\"");
                               if (browser.tableSetting.getEndHour()==i)
                                   out.println(" selected ");
                               out.println(">"+String.valueOf(i)+"</option>");
                           }
                           %>
                       </select>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Step (Minutes):
                    </td>
                    <th>
                       <select name="precision" style="width:50px;float:left;">
                           <option value="0" <% if (browser.tableSetting.getPrecision()==0) { out.println("selected"); } %>>60</option>
                           <option value="1" <% if (browser.tableSetting.getPrecision()==1) { out.println("selected"); } %>>30</option>
                           <option value="2"<% if (browser.tableSetting.getPrecision()==2) { out.println("selected"); } %>>15</option>
                       </select>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        First day of a Week:
                    </td>
                    <th>
                       <select name="firstday" style="width:75px;float:left;">
                           <option value="0" <% if (browser.tableSetting.getFirstDay()==0) { out.println("selected"); } %>>Sunday</option>
                           <option value="1" <% if (browser.tableSetting.getFirstDay()==1) { out.println("selected"); } %>>Monday</option>
                       </select>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Show Weekend:
                    </td>
                    <th>
                       <input name="weekend" style="width:50px;float:left;" value="true" type="checkbox" <% if (browser.tableSetting.getWeekend()) { out.println("checked=\"yes\""); } %>/>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        AM/PM:
                    </td>
                    <th>
                       <input name="ampm" style="width:50px;float:left;" value="true" type="checkbox" <% if (browser.tableSetting.getAMPM()) { out.println("checked=\"yes\""); } %>/>
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="button" onclick="this.form.submit();" value="Save"/>
                        </center>
                    </td>
                </tr>
            </table>
            </form>
        </div>

        <div id="neweventlayer" class="layer newevent">
            <form action="./portal" method="post">
            <input type="hidden" name="command" value="findfreeroomsfornewreservation" />
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideNewEventLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <%
            Date today=new Date();
            DateFormat dfdate = new SimpleDateFormat("dd.MM.yyyy");
            DateFormat dftime = new SimpleDateFormat("HH:mm");
            %>

            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>New Event</h3>
                        </center>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Name:
                    </td>
                    <th>
                        <input style="float:left;" type="text" name="name" value="">
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Date:
                    </td>
                    <th>
                        <input style="float:left;" type="text" name="date" value="<% out.println(dfdate.format(today).toString()); %>">
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Start Time:
                    </td>
                    <th>
                        <input style="float:left;" type="text" name="starttime" value="<% out.println(dftime.format(today).toString()); %>">
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        End Time:
                    </td>
                    <th>
                        <input style="float:left;"type="text" name="endtime" value="<% out.println(dftime.format(today).toString()); %>">
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="button" onclick="this.form.submit();" value="Find free rooms"/>
                        </center>
                    </td>
                </tr>
            </table>
            </form>
        </div>

        <div id="editeventlayer" class="layer editevent">
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideEditEventLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>Edit Event</h3>
                        </center>
                    </td>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Name:
                    </td>
                    <th>
                        <form name="changeeventnameform" action="./portal" method="post">
                        <input type="hidden" name="command" value="changeeventname" />
                        <input type="hidden" name="reservation_id" />
                        <input name="date"  type="hidden"/>
                        <input name="starttime" type="hidden"/>
                        <input name="endtime" type="hidden" />
                        <input style="float:left;" type="text" name="event_name"/>
                        <input style="float:left;" type="button" onclick="this.form.submit();" value="Save"/>
                        </form>
                    </th>
                </tr>
                <form name="findfreeroomsform" action="./portal" method="post">
                <input type="hidden" name="command" value="findfreerooms" />
                <input type="hidden" name="reservation_id" />
                <input type="hidden" name="event_name" />
                <tr>
                    <td style="vertical-align:middle;">
                        Date:
                    </td>
                    <th>
                        <input name="date" style="float:left;" type="text" />
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Start Time:
                    </td>
                    <th>
                        <input name="starttime"style="float:left;" type="text"/>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        End Time:
                    </td>
                    <th>
                        <input name="endtime" style="float:left;"type="text" />
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="button" onclick="this.form.submit();" value="Find free rooms"/>
                        </center>
                    </td>
                </tr>
                </form>
            </table>
        </div>

        <div id="freeboardroomslayer" class="layer freeboardrooms">
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideFreeBoardroomsLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>Choose Free Boardroom:</h3>
                        </center>
                    </td>
                </tr>
                <form name="selectfreeboardroom" action="./portal" method="post">
                <tr>
                    <td style="vertical-align:middle;">
                        Name:
                    </td>
                    <th>
                        <input type="hidden" name="command" value="selectfreeboardroom" />
                        <select name="boardrooms" style="width:100px;float:left;">
                        <%
                        if (browser.getFreeBoardrooms() != null) {
                            Enumeration idss = browser.getFreeBoardrooms().keys();
                            while (idss.hasMoreElements()) {
                                Integer id = (Integer)idss.nextElement();
                                String name = (String)browser.getFreeBoardrooms().get(id);
                                out.println("<option value=\""+id+"\"");
                                if (browser.boardroom_id==id.intValue())
                                    out.println("selected");
                                out.println(">"+name+"</option>");
                            }
                        }
                        %>
                        </select>
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="button" onclick="this.form.submit();" value="Reserve"/>
                        </center>
                    </td>
                </tr>
                </form>
            </table>
        </div>

        <div id="newuserlayer" class="layer newuser">
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideNewUserLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>New User</h3>
                        </center>
                    </td>
                </tr>
                <form name="newuser" action="./portal" method="post">
                <tr>
                    <td style="vertical-align:middle;">
                        Phone Number:
                    </td>
                    <th>
                        <input type="hidden" name="command" value="newuser" />
                        <input style="float:left;" type="text" name="phone" value=""/>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Name:
                    </td>
                    <th>
                        <input style="float:left;" type="text" name="name" value=""/>
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="button" onclick="this.form.submit();" value="Create"/>
                        </center>
                    </td>
                </tr>
                </form>
            </table>
        </div>

        <div id="newboardroomlayer" class="layer newboardroom">
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideNewBoardroomLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>New Boardroom</h3>
                        </center>
                    </td>
                </tr>
                <form name="newboardroom" action="./portal" method="post">
                <tr>
                    <td style="vertical-align:middle;">
                        Name:
                    </td>
                    <th>
                        <input type="hidden" name="command" value="newboardroom" />
                        <input style="float:left;" type="text" name="name" value=""/>
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="button" onclick="this.form.submit();" value="Create"/>
                        </center>
                    </td>
                </tr>
                </form>
            </table>
        </div>

        <div id="edituserlayer" class="layer edituser">
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideEditUserLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>Edit User</h3>
                        </center>
                    </td>
                </tr>
                <form name="edituserform" action="./portal" method="post">
                <tr>
                    <td style="vertical-align:middle;">
                        Name:
                    </td>
                    <th>
                        <input type="hidden" name="command" value="edituser" />
                        <input type="hidden" name="id" />
                        <input style="float:left;" type="text" name="name" value=""/>
                    </th>
                </tr>
                <tr>
                    <td style="vertical-align:middle;">
                        Phone:
                    </td>
                    <th>
                        <input style="float:left;" type="text" name="phone" value=""/>
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="button" onclick="this.form.submit();" value="Save"/>
                        </center>
                    </td>
                </tr>
                </form>
                <form name="resetuserform" action="./portal" method="post">
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="hidden" name="command" value="resetuser" />
                            <input type="hidden" name="id" />
                            <input type="button" onclick="this.form.submit();" value="Reset password"/>
                        </center>
                    </td>
                </tr>
                </form>
            </table>
        </div>

        <div id="editboardroomlayer" class="layer newboardroom">
            <div class="zeros containterheader">
                <div class="zeros right"><a href="#" onclick="hideEditBoardroomLayer();"><img src="close.gif" alt="close"/></a></div>
            </div>
            <br>
            <table width="100%" >
                <tr>
                    <td colspan="2" >
                        <center>
                        <h3>Edit Boardroom</h3>
                        </center>
                    </td>
                </tr>
                <form name="editboardroomform" action="./portal" method="post">
                <tr>
                    <td style="vertical-align:middle;">
                        Name:
                    </td>
                    <th>
                        <input type="hidden" name="command" value="editboardroom" />
                        <input type="hidden" name="id"/>
                        <input style="float:left;" type="text" name="name" value=""/>
                    </th>
                </tr>
                <tr>
                    <td colspan="2" >
                        <center>
                            <input type="button" onclick="this.form.submit();" value="Save"/>
                        </center>
                    </td>
                </tr>
                </form>
            </table>
        </div>
    </div>
</div>
<form id="previous" action="./portal" method="post">
    <input type="hidden" name="command" value="previous_week" />
</form>
<form id="next" action="./portal" method="post">
    <input type="hidden" name="command" value="next_week" />
</form>
<form id="deleteeventform" action="./portal" method="post">
    <input type="hidden" name="command" value="deleteevent" />
    <input type="hidden" name="reservation_id" />
</form>
<form id="deleteuserform" action="./portal" method="post">
    <input type="hidden" name="command" value="deleteuser" />
    <input type="hidden" name="user_id" />
</form>
<form id="deleteboardroomform" action="./portal" method="post">
    <input type="hidden" name="command" value="deleteboardroom" />
    <input type="hidden" name="boardroom_id" />
</form>
<%

if (!browser.getAction().equals("")) {
    if (browser.getAction().equals("freeboardroomsfinded")) {
            %>
            <script type="text/javascript">
                window.onload=new Function(showFreeBoardroomsLayer());
            </script>
            <%
        browser.cancelAction();
    }
    if (browser.getAction().equals("logout")) {
        Portal.getCache().removeBrowser(session.getId());
        session.invalidate();
        browser.cancelAction();
    }
}

%>
</newsection:over>
<%@ include file="base.jsp" %>