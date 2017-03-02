/*
 * Decompiled with CFR 0_118.
 */
package com.seifmostafa.cchat.acoustic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

public class PropertiesDumper {
    private Properties props;

    public static void main(String[] argv) {
        try {
            PropertiesDumper dumper = new PropertiesDumper("model.props");
            System.out.println();
            System.out.println(dumper.toString());
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public PropertiesDumper(String propsFile) throws IOException {
        this.props = new Properties();
        this.props.load(this.getClass().getResource(propsFile).openStream());
    }

    public PropertiesDumper(Properties properties) throws IOException {
        this.props = properties;
    }

    public String toString() {
        String result = (String)this.props.get("description") + "\n";
        ArrayList list = Collections.list(this.props.propertyNames());
        Collections.sort(list);
        Iterator i = list.iterator();
        while (i.hasNext()) {
            String key = (String)i.next();
            String value = (String)this.props.get(key);
            result = result + "\n\t" + key + ": " + value;
        }
        result = result + "\n";
        return result;
    }

    private String getReadableForm(String original) {
        if (original.length() > 0) {
            StringBuffer sb = new StringBuffer(original.length() * 2);
            int i = 0;
            sb.append(Character.toUpperCase(original.charAt(i++)));
            while (i < original.length()) {
                char c = original.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(" ");
                }
                sb.append(c);
                ++i;
            }
            return sb.toString();
        }
        return original;
    }
}

