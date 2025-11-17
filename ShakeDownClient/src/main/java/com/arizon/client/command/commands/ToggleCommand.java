package com.arizon.client.command.commands;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.command.Command;
import com.arizon.client.module.Module;

public class ToggleCommand extends Command {
    
    public ToggleCommand() {
        super("toggle", "Toggle a module", ".toggle <module>", "t");
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            sendError("Usage: " + usage);
            return;
        }
        
        String moduleName = String.join(" ", args);
        Module module = ArizonClientMod.getInstance().getModuleManager().getModuleByName(moduleName);
        
        if (module == null) {
            sendError("Module not found: " + moduleName);
            return;
        }
        
        module.toggle();
        sendMessage("§3" + module.getName() + " §7is now " + 
                   (module.isEnabled() ? "§aenabled" : "§cdisabled"));
    }
}
