package com.arizon.client.command.commands;

import com.arizon.client.command.Command;

public class FriendCommand extends Command {
    
    public FriendCommand() {
        super("friend", "Manage friends", ".friend <add|remove|list> [name]", "f");
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            sendError("Usage: " + usage);
            return;
        }
        
        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length < 2) {
                    sendError("Usage: .friend add <name>");
                    return;
                }
                sendMessage("§aAdded §3" + args[1] + " §ato friends!");
                break;
            case "remove":
            case "del":
                if (args.length < 2) {
                    sendError("Usage: .friend remove <name>");
                    return;
                }
                sendMessage("§cRemoved §3" + args[1] + " §cfrom friends!");
                break;
            case "list":
                sendMessage("§3Friends list coming soon!");
                break;
            default:
                sendError("Usage: " + usage);
        }
    }
}
