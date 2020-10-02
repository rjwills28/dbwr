/*******************************************************************************
 * Copyright (c) 2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the LICENSE
 * which accompanies this distribution
 ******************************************************************************/
package dbwr.widgets;

import static dbwr.WebDisplayRepresentation.logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import dbwr.macros.MacroUtil;
import dbwr.parser.HTMLUtil;
import dbwr.parser.Resolver;
import dbwr.parser.WidgetFactory;
import dbwr.parser.XMLUtil;

/** Navigation Tabs Widget
 *  @author Kay Kasemir
 */
public class NavTabsWidget extends BaseMacroWidget
{
    static
    {
        WidgetFactory.addCSS("navtabs.css");
        WidgetFactory.addJavaScript("navtabs.js");
    }

    private final List<String> labels = new ArrayList<>();
    private final List<String> files = new ArrayList<>();
    private final List<Map<String, String>> macros = new ArrayList<>();
    private final boolean horizontal;
    private final int tab_width, tab_height, tab_spacing;

    public NavTabsWidget(final ParentWidget parent, final Element xml) throws Exception
    {
        super(parent, xml, "navtabs");

        classes.add("Debug");

        // Locate labels and content links of tabs
        final Element tbs = XMLUtil.getChildElement(xml, "tabs");
        if (tbs != null)
        {
            int i=0;
            for (final Element tb : XMLUtil.getChildElements(tbs, "tab"))
            {
                final String label = XMLUtil.getChildString(parent, tb, "name").orElse("Tab " + (i+1));

                final String file = XMLUtil.getChildString(parent, tb, "file").orElse("");
                final String resolved = Resolver.resolve(this, file);

                final Map<String, String> m = MacroUtil.fromXML(tb);
                MacroUtil.expand(parent, m);

                labels.add(label);
                files.add(resolved);
                macros.add(m);

                ++i;
            }
        }

        horizontal = XMLUtil.getChildInteger(xml, "direction").orElse(1) == 0;
        tab_width = XMLUtil.getChildInteger(xml, "tab_width").orElse(100);
        tab_height = XMLUtil.getChildInteger(xml, "tab_height").orElse(30);
        tab_spacing = XMLUtil.getChildInteger(xml, "tab_spacing").orElse(2);
        // active_tab
    }

    @Override
    protected void fillHTML(final PrintWriter html, final int indent)
    {
        int i = 0, bx = 0, by = 0;
        for (String label : labels)
        {
            if (! label.isEmpty())
            {
                final StringBuilder style = new StringBuilder();
                style.append("left: ").append(bx).append("px;");
                style.append("top: ").append(by).append("px;");
                style.append("width: ").append(tab_width).append("px;");
                style.append("height: ").append(tab_height).append("px;");

                html.append("<button class=\"NavTabsButton\" style=\"" + style.toString() + "\"");

                html.append(" data-linked-file=\"" + HTMLUtil.escape(files.get(i)) + "\"");
                if (! macros.get(i).isEmpty())
                    try
                    {
                        html.append(" data-linked-macros=\"" + HTMLUtil.escape(MacroUtil.toJSON(macros.get(i))) + "\"");
                    }
                    catch (Exception ex)
                    {
                        logger.log(Level.WARNING, "Cannot add navtab button macros", ex);
                    }

                html.append(">");
                HTMLUtil.escape(html, label);
                html.append("</button>");
            }

            if (horizontal)
                bx += tab_width + tab_spacing;
            else
                by += tab_height + tab_spacing;

            ++i;
        }
    }
}
