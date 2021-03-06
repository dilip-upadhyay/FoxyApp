/*
 * LabelRenderer.java
 *
 * Created on August 22, 2006, 11:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.util;

/**
 *
 * @author hcting
 */

import java.util.Iterator;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

// Renderer for the Label components

public class LabelRenderer extends Renderer {
    
    @Override
    public boolean getRendersChildren() {
        return false;
    }
    
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws java.io.IOException {
        try {
            
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("label", component);
            
            String styleClass = (String)component.getAttributes().get("styleClass");
            if (styleClass != null)
                writer.writeAttribute("class", styleClass, null);
            
            Map attrs = component.getAttributes();
            writer.writeAttribute("for", component.getClientId(context), null);
            
            UIInput input = (UIInput)component.findComponent((String)attrs.get("for"));
            writer.write(attrs.get("value").toString());
            if(input.isRequired()) {
                boolean msgs = hasMessages(context, input);
                if(msgs) { //validation error message found
                    writer.startElement("font", null);
                    writer.writeAttribute("color", "red", null);
                } else { //no error condition
                    writer.startElement("font", null);
                    writer.writeAttribute("color", "blue", null);
                }
                writer.write("*");
                if(msgs) {
                    writer.endElement("font");
                } else {
                    writer.endElement("font");
                }
            }
            //writer.write(attrs.get("value").toString());
        } catch (Exception e) {
            Map attrs = component.getAttributes();
            System.err.println("LabelRenderer: Error encountered at label for [" + (String)attrs.get("for") + "] field");
            e.printStackTrace();
        }
        
    }
    
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws java.io.IOException {
        try {
            
            ResponseWriter writer = context.getResponseWriter();
            writer.endElement("label");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    private boolean hasMessages(FacesContext context, UIComponent component) {
        Iterator it = context.getClientIdsWithMessages();
        boolean found = false;
        
        while(it.hasNext()) {
            String id = (String)it.next();
            if(component.getClientId(context).equals(id))
                found = true;
        }
        return found;
    }
    
}