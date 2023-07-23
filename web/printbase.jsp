<%@ taglib uri="/BlockTag.tld" prefix="section" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Timeline</title>
<link rel=StyleSheet href="printstyle.css" type="text/css"/>
<!--[if lte IE 7]>
<style>
.content { margin-right: -1px; } /* this 1px negative margin can be placed on any of the columns in this layout with the same corrective effect. */
ul.nav a { zoom: 1; }  /* the zoom property gives IE the hasLayout trigger it needs to correct extra whiltespace between the links */
</style>
<![endif]-->
<section:block name="extrahead"></section:block>
</head>
<body>
<div id="content" class="page">
<section:block name="printcontent"></section:block>
</div>
</body>
</html>
