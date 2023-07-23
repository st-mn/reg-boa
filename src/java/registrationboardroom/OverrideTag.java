
/*
http://rapid-framework.googlecode.com/svn/trunk/rapid-framework/src/rapid_framework_common/cn/org/rapid_framework/web/tags/
Custom JSP tag called OverrideTag, which is used for overriding content in a JSP page. 
It is typically used in combination with other custom tags or 
templates to provide template inheritance and content replacement in web applications.

The class extends BodyTagSupport, a standard base class for JSP tags that have a body content.
It implements the doStartTag() and doEndTag() methods to control the behavior of the tag when it is encountered in a JSP page.

Class Members:
name: A private string variable that holds the name of the content block to be overridden.

setName Method:
This method is a setter for the name variable, which allows setting the name of the content block to be overridden. 
The name is typically provided as an attribute when using the custom tag in a JSP page.
doStartTag() Method:

This method is executed when the custom tag is encountered as a start tag in a JSP page.
It determines whether the content should be evaluated (rendered) or skipped based on whether 
the content block with the given name is already overridden or not.
If the content block is overridden (i.e., already exists), the method returns SKIP_BODY, 
indicating that the body content of the custom tag should not be evaluated.

doEndTag() Method:
This method is executed when the custom tag is encountered as an end tag in a JSP page.
If the content block with the given name is not already overridden, 
it reads the body content of the custom tag and stores it in a request attribute with a specific variable name, 
which is determined by the name of the content block.

isOverrided() Method:
This private method checks whether the content block with the given name is already overridden.
It does so by checking whether a request attribute exists with a specific variable name derived 
from the content block name using a utility method called Utils.getOverrideVariableName(name).

The purpose of this custom tag is to allow developers to create reusable templates in JSP pages 
with placeholders for content blocks that can be overridden in child pages. 
It facilitates a form of template inheritance, where a base template can define common layout and structure, 
and child pages can override specific content blocks as needed. This pattern is commonly used in web development frameworks 
to build consistent and maintainable web applications.
 
 */

package registrationboardroom;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class OverrideTag extends BodyTagSupport{
	private static final long serialVersionUID = -8379959647039117369L;

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int doStartTag() throws JspException {
		return isOverrided() ? SKIP_BODY : EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {
		if(isOverrided()) {
			return EVAL_PAGE;
		}
		BodyContent b = getBodyContent();
//		System.out.println("Override.content:"+b.getString());
		String varName = Utils.getOverrideVariableName(name);

		pageContext.getRequest().setAttribute(varName, b.getString());
		return EVAL_PAGE;
	}

	private boolean isOverrided() {
		String varName = Utils.getOverrideVariableName(name);
		return pageContext.getRequest().getAttribute(varName) != null;
	}

}
