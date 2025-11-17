package com.arizon.client.command;

/**
 * Base command class
 */
public abstract class Command {
    
    protected String name;
    protected String description;
    protected String usage;
    protected String[] aliases;
    
    public Command(String name, String description, String usage, String... aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }
    
    public abstract void execute(String[] args);
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getUsage() {
        return usage;
    }
    
    public String[] getAliases() {
        return aliases;
    }
    
    protected void sendMessage(String message) {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("§6[Arizon] §f" + message), false);
        }
    }
    
    protected void sendError(String message) {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("§6[Arizon] §c" + message), false);
        }
    }
}
