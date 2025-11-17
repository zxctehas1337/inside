package com.arizon.client.command.commands;

import com.arizon.client.command.Command;
import com.arizon.client.command.CommandManager;

public class HelpCommand extends Command {
    
    public HelpCommand() {
        super("help", "Shows list of commands", ".help", "h", "?");
    }
    
    @Override
    public void execute(String[] args) {
        sendMessage("§b§lAvailable Commands:");
        for (Command cmd : CommandManager.getInstance().getCommands()) {
            sendMessage("§3" + CommandManager.PREFIX + cmd.getName() + " §7- §f" + cmd.getDescription());
        }
    }
}
