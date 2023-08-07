/*
custom JSP tag named BlockTag. This custom tag is used for block-level content overrides in JSP pages. 
It allows developers to define blocks of content that can be overridden and replaced in child JSP files.

The Utils class is defined, which contains a utility method named getOverrideVariableName(). 
This method is used to generate a unique name for a block override based on the provided block name.

The BlockTag class is defined, which extends TagSupport. It represents the custom JSP tag 
that handles the block-level content override functionality.

The BlockTag class has a single private field named name, which will store the 
name of the block to be overridden.

The setName(String name) method is provided to set the value of the name field.

The doStartTag() method is overridden from TagSupport. This method is called 
when the custom tag is encountered in the JSP file. It determines whether the block 
has content to override or not. If there is no content for the block, it returns EVAL_BODY_INCLUDE, 
indicating that the body content of the tag should be processed (if any).

The doEndTag() method is also overridden from TagSupport. 
This method is called after the body content of the tag (if any) has been processed. 
It writes the overridden content for the block, if available, 
to the output stream using pageContext.getOut().write(overriedContent). 
If there is no overridden content, it returns EVAL_PAGE, indicating that the rest of the JSP page should be processed.

The getOverriedContent() method is a private method used internally to retrieve the overridden content for the block. 
It generates the variable name for the block using the Utils.getOverrideVariableName(name) method 
and retrieves the content from the request attributes.

Custom JSP tag named BlockTag. This custom tag is used for block-level content overrides in JSP pages. 
It allows developers to define blocks of content that can be overridden and replaced in child JSP files.
*/
package registrationboardroom;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * http://rapid-framework.googlecode.com/svn/trunk/rapid-framework/src/rapid_framework_common/cn/org/rapid_framework/web/tags/
 *
 * 
 */
class Utils {

	public static String BLOCK = "__jsp_override__";

	static String getOverrideVariableName(String name) {
		return BLOCK + name;
	}


}

/**
 *
 * http://rapid-framework.googlecode.com/svn/trunk/rapid-framework/src/rapid_framework_common/cn/org/rapid_framework/web/tags/
 *
 * 
 *
 */
public class BlockTag extends TagSupport{

	private static final long serialVersionUID = -8246166191638588615L;

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return EVAL_BODY_INCLUDE or EVAL_BODY_BUFFERED or SKIP_BODY
	 */
	@Override
	public int doStartTag() throws JspException {
		return getOverriedContent() == null ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}

	/**
	 * @return EVAL_PAGE or SKIP_PAGE
	 */
	@Override
	public int doEndTag() throws JspException {
		String overriedContent = getOverriedContent();
		if(overriedContent == null) {
			return EVAL_PAGE;
		}

		try {
			pageContext.getOut().write(overriedContent);
		} catch (IOException e) {
			throw new JspException("write overridedContent occer IOException,block name:"+name,e);
		}
		return EVAL_PAGE;
	}

	private String getOverriedContent() {
		String varName = Utils.getOverrideVariableName(name);
		return (String)pageContext.getRequest().getAttribute(varName);
	}
}
