package com.arizon.client.command;

import com.arizon.client.command.commands.*;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all commands
 */
public class CommandManager {
    
    private static CommandManager instance;
    private final List<Command> commands = new ArrayList<>();
    public static final String PREFIX = ".";
    
    public CommandManager() {
        instance = this;
        registerCommands();
    }
    
    private void registerCommands() {
        commands.add(new HelpCommand());
        commands.add(new ToggleCommand());
        commands.add(new BindCommand());
        commands.add(new ConfigCommand());
        commands.add(new FriendCommand());
        commands.add(new FakePlayerCommand());
    }
    
    public void executeCommand(String input) {
        if (!input.startsWith(PREFIX)) return;
        
        input = input.substring(PREFIX.length());
        String[] parts = input.split(" ");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        
        // Find command
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                command.execute(args);
                return;
            }
            
            // Check aliases
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(commandName)) {
                    command.execute(args);
                    return;
                }
            }
        }
        
        // Command not found
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal(
                "§6[Arizon] §cCommand not found. Use .help for list of commands."), false);
        }
    }
    
    public List<Command> getCommands() {
        return commands;
    }
    
    public static CommandManager getInstance() {
        return instance;
    }
}
