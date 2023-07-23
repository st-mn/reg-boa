<%@page contentType="text/html" pageEncoding="UTF-8"%>
<script type="text/javascript">
    function show_alert(msg) {
        alert(msg);
    }

    function showSettingsLayer() {
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('settingslayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('settingslayer').style.visibility='visible';
    }

    function hideSettingsLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('settingslayer').style.display='none';

    }

    function showChangePasswordLayer() {
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('changepasswordlayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('changepasswordlayer').style.visibility='visible';
    }

    function hideChangePasswordLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('changepasswordlayer').style.display='none';
    }

    function showNewEventLayer() {
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('neweventlayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('neweventlayer').style.visibility='visible';
    }

    function hideNewEventLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('neweventlayer').style.display='none';
    }

    function showEditEventLayer() {
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('editeventlayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('editeventlayer').style.visibility='visible';
    }

    function hideEditEventLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('editeventlayer').style.display='none';
    }

    function showFreeBoardroomsLayer() {
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('freeboardroomslayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('freeboardroomslayer').style.visibility='visible';
    }

    function hideFreeBoardroomsLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('freeboardroomslayer').style.display='none';
    }

    function showStatisticsLayer() {
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('statisticslayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('statisticslayer').style.visibility='visible';
    }

    function hideStatisticsLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('statisticslayer').style.display='none';
    }

    function showNewUserLayer() {
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('newuserlayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('newuserlayer').style.visibility='visible';
    }

    function hideNewUserLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('newuserlayer').style.display='none';
    }

    function showNewBoardroomLayer() {
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('newboardroomlayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('newboardroomlayer').style.visibility='visible';
    }

    function hideNewBoardroomLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('newboardroomlayer').style.display='none';
    }

    function showEditUserLayer() {
        object = document.forms['changeuserform'];
        user = object.elements["user"].selectedIndex;
        id = object.elements["user"].options[user].value;
        namephone = object.elements["user"].options[user].innerHTML;
        namephone = namephone.toString().split(" - ", 2);
        object = document.forms['edituserform'];
        object.elements["phone"].value = namephone[1];
        object.elements["name"].value = namephone[0];
        object.elements["id"].value=id;
        object = document.forms['resetuserform'];
        object.elements["id"].value=id;
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('edituserlayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('edituserlayer').style.visibility='visible';
    }

    function hideEditUserLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('edituserlayer').style.display='none';
    }

    function showEditBoardroomLayer() {
        object = document.forms['changeboardroomform'];
        boardroom = object.elements["boardrooms"].selectedIndex;
        id = object.elements["boardrooms"].options[boardroom].value;
        name = object.elements["boardrooms"].options[boardroom].innerHTML;
        object = document.forms['editboardroomform'];
        object.elements["name"].value = name;
        object.elements["id"].value = id;
        document.getElementById('bg_mask').style.display='inline';
        document.getElementById('editboardroomlayer').style.display='inline';
        document.getElementById('bg_mask').style.visibility='visible';
        document.getElementById('editboardroomlayer').style.visibility='visible';
    }

    function hideEditBoardroomLayer() {
        document.getElementById('bg_mask').style.display='none';
        document.getElementById('editboardroomlayer').style.display='none';
    }

    function editEvent(name,reservation_id,date,starttime,endtime) {
        object = document.forms['changeeventnameform'];
        object.elements["event_name"].value = name;
        object.elements["reservation_id"].value = reservation_id;
        object.elements["date"].value = date;
        object.elements["starttime"].value = starttime;
        object.elements["endtime"].value = endtime;
        object = document.forms['findfreeroomsform'];
        object.elements["event_name"].value = name;
        object.elements["reservation_id"].value = reservation_id;
        object.elements["date"].value = date;
        object.elements["starttime"].value = starttime;
        object.elements["endtime"].value = endtime;
        showEditEventLayer();
    }

    function deleteEventConfirmation(name,reservation_id) {
        var answer = confirm("Do you really want to remove event "+name+"?");
        if (answer){
            object = document.forms['deleteeventform'];
            object.elements["reservation_id"].value = reservation_id;
            object.submit();
        }
    }

    function deleteUserConfirmation() {
        object = document.forms['changeuserform'];
        user = object.elements["user"].selectedIndex;
        id = object.elements["user"].options[user].value;
        name = object.elements["user"].options[user].innerHTML;
        var answer = confirm("Do you really want to remove user "+name+"?");
        if (answer){
            object = document.forms['deleteuserform'];
            object.elements["user_id"].value = id;
            object.submit();
        }
    }

    function deleteBoardroomConfirmation() {
        object = document.forms['changeboardroomform'];
        boardroom = object.elements["boardrooms"].selectedIndex;
        id = object.elements["boardrooms"].options[boardroom].value;
        name = object.elements["boardrooms"].options[boardroom].innerHTML;
        var answer = confirm("Do you really want to remove boardroom "+name+"?");
        if (answer){
            object = document.forms['deleteboardroomform'];
            object.elements["boardroom_id"].value = id;
            object.submit();
        }
    }

    function submitenter(form,e) {
        var keycode;
        if (window.event)
            keycode = window.event.keyCode;
        else
            if (e)
                keycode = e.which;
            else return true;

        if (keycode == 13) {
            form.submit();
            return false;
        }
        else
           return true;
    }

    function changesection(section) {
        link='./portal?section='+section;
        location.href=link;
    }

</script>