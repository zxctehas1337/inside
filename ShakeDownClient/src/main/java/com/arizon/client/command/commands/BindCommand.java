package com.arizon.client.command.commands;

import com.arizon.client.command.Command;

public class BindCommand extends Command {
    
    public BindCommand() {
        super("bind", "Bind a module to a key", ".bind <add|list> [module] [key]", "b");
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            sendError("Usage: .bind <add|list> [module] [key]");
            sendMessage("§7Example: §3.bind add Aura R");
            return;
        }
        
        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length < 3) {
                    sendError("Usage: .bind add <module> <key>");
                    sendMessage("§7Example: §3.bind add Aura R");
                    return;
                }
                String moduleName = args[1];
                String key = args[2].toUpperCase();
                sendMessage("§aBound §3" + moduleName + " §ato key §3" + key);
                sendMessage("§7(Keybind system coming soon!)");
                break;
                
            case "list":
                sendMessage("§3§lKeybinds:");
                sendMessage("§7No keybinds set yet");
                sendMessage("§7(Keybind system coming soon!)");
                break;
                
            default:
                sendError("Usage: .bind <add|list> [module] [key]");
        }
    }
}
