package com.arizon.client.command.commands;

import com.arizon.client.command.Command;
import com.arizon.client.fakeplayer.FakePlayer;

public class FakePlayerCommand extends Command {
    
    public FakePlayerCommand() {
        super("fakeplayer", "Spawn a fake player for PVP practice", 
              ".fakeplayer <totem|none|netherite|totemnetherite>", "fp");
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            if (FakePlayer.exists()) {
                sendMessage("§7Current mode: §3" + FakePlayer.getCurrentMode());
                sendMessage("§7Use §3.fakeplayer none §7to remove");
            } else {
                sendError("Usage: " + usage);
                sendMessage("§7Modes:");
                sendMessage("  §3totem §7- Player with totem");
                sendMessage("  §3netherite §7- Full netherite armor");
                sendMessage("  §3totemnetherite §7- Netherite + totem");
                sendMessage("  §3none §7- Remove fake player");
            }
            return;
        }
        
        String mode = args[0].toLowerCase();
        
        switch (mode) {
            case "none":
            case "remove":
                if (FakePlayer.exists()) {
                    FakePlayer.remove();
                    sendMessage("§cFake player removed");
                } else {
                    sendError("No fake player exists");
                }
                break;
                
            case "totem":
            case "netherite":
            case "totemnetherite":
                FakePlayer.spawn(mode);
                sendMessage("§aFake player spawned with mode: §3" + mode);
                sendMessage("§7You can practice PVP with Aura on this player!");
                sendMessage("§7Server cannot see this player (client-side only)");
                break;
                
            default:
                sendError("Invalid mode: " + mode);
                sendMessage("§7Valid modes: totem, netherite, totemnetherite, none");
        }
    }
}
