package de.chefslist.plugins.systembars;

import com.getcapacitor.Logger;

public class SystemBars {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
