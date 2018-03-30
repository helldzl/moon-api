package com.mifan.user.domain;

/**
 * @author quzile
 * @version 1.0
 * @since 2015-12-04
 */
public class AuthElements extends AuthorityConfig {

    public static final String TABLE_NAME = "auth_elements";

    public static final String AUTHORITY_ID = "authority_id";
    public static final String ICON = "icon";
    public static final String ELEMENT_NAME = "element_name";
    public static final String ELEMENT_CLASS = "element_class";
    public static final String ELEMENT_STYLE = "element_style";
    public static final String SCRIPT = "script";

    private static final long serialVersionUID = 1845292432715636824L;

    private String icon;
    private String elementName;
    private String elementClass;
    private String elementStyle;
    private String script;

    public AuthElements() {
    }

    public AuthElements(Long id) {
        super(id);
    }

    @Override
    public String value() {
        return elementName;
    }

    /**
     * @return
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * @param elementName
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * @return
     */
    public String getElementClass() {
        return elementClass;
    }

    /**
     * @param elementClass
     */
    public void setElementClass(String elementClass) {
        this.elementClass = elementClass;
    }

    /**
     * @return
     */
    public String getElementStyle() {
        return elementStyle;
    }

    /**
     * @param elementStyle
     */
    public void setElementStyle(String elementStyle) {
        this.elementStyle = elementStyle;
    }

    /**
     * @return
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script
     */
    public void setScript(String script) {
        this.script = script;
    }

}
