package com.arizon.client.command.commands;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.command.Command;

public class ConfigCommand extends Command {
    
    public ConfigCommand() {
        super("config", "Manage configs", ".config <save|load|reset>", "cfg");
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            sendError("Usage: " + usage);
            return;
        }
        
        switch (args[0].toLowerCase()) {
            case "save":
                ArizonClientMod.getInstance().getModuleManager().saveModules();
                ArizonClientMod.getInstance().getConfigManager().saveConfig();
                sendMessage("§aConfig saved!");
                break;
            case "load":
                ArizonClientMod.getInstance().getModuleManager().loadModules();
                ArizonClientMod.getInstance().getConfigManager().loadConfig();
                sendMessage("§aConfig loaded!");
                break;
            case "reset":
                sendMessage("§cConfig reset coming soon!");
                break;
            default:
                sendError("Usage: " + usage);
        }
    }
}
