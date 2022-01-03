package com.tundraclimate.github.i_am_a_tnt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.tundraclimate.github.i_am_a_tnt.I_am_a_TNT.config;

public final class I_am_a_TNT extends JavaPlugin {
    public static ConfigOf config;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        config = new ConfigOf();
        new self_bomb(this);
        new reloadCommand(this);
        config.load();
    }
}
class self_bomb implements Listener{
    public self_bomb(I_am_a_TNT plugin){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void Bomb(PlayerDeathEvent e) {
        if (config.Enable) {
            Location deathLocation = e.getEntity().getLocation();
            deathLocation.getWorld().createExplosion(deathLocation, config.power, config.Fire, config.breakBlock);
        }
    }
}
class reloadCommand implements CommandExecutor, TabCompleter {
    public reloadCommand(I_am_a_TNT plugin){
        plugin.getCommand("selfbomb").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("selfbomb")) {
            if (sender.isOp()) {
                if (args.length == 0) {
                    if (config.Enable) {
                        config.setEnable(false);
                        config.load();
                        sender.sendMessage(ChatColor.GREEN + "SelfBomb is Disabled!");
                    } else {
                        config.setEnable(true);
                        config.load();
                        sender.sendMessage(ChatColor.GREEN + "SelfBomb is Enabled!");
                    }
                    return true;
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("power")) {
                        try {
                            config.setPower(Double.parseDouble(args[1]));
                        }
                        catch (NumberFormatException e){
                            return false;
                        }
                        config.load();
                        sender.sendMessage(ChatColor.GREEN+"Power set "+args[1]);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("dobreakblock")) {
                        config.setBreakBlock(Boolean.parseBoolean(args[1]));
                        config.load();
                        sender.sendMessage(ChatColor.GREEN+"CanBreakBlock set "+args[1]);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("dofire")) {
                        config.setFire(Boolean.parseBoolean(args[1]));
                        config.load();
                        sender.sendMessage(ChatColor.GREEN+"doFire set "+args[1]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("selfbomb")){
            if (args[0].length()==0) return Arrays.asList("Power","doBreakBlock","doFire");
            else {
                if ("power".startsWith(args[0])) return Collections.singletonList("Power");
                if ("dobreakblock".startsWith(args[0])) return Collections.singletonList("doBreakBlock");
                if ("dofire".startsWith(args[0])) return Collections.singletonList("doFire");
            }
        }
        return null;
    }
}
class ConfigOf {
    private FileConfiguration config = null;
    private final Plugin plugin = I_am_a_TNT.plugin;

    public boolean Enable;
    public float power;
    public boolean breakBlock;
    public boolean Fire;

    public void setEnable(boolean enable) {
        Enable = enable;
        config.set("Enable",enable);
        plugin.saveConfig();
    }
    public void setPower(double power){
        this.power = (float) power;
        config.set("breakPower",power);
        plugin.saveConfig();
    }
    public void setBreakBlock(boolean breakBlock){
        this.breakBlock = breakBlock;
        config.set("breakBlock",breakBlock);
        plugin.saveConfig();
    }
    public void setFire(boolean fire){
        this.Fire = fire;
        config.set("Fire",fire);
        plugin.saveConfig();
    }

    public void load(){
        if (config != null){
            plugin.reloadConfig();
        }
        else {
            plugin.saveDefaultConfig();
        }
        config = plugin.getConfig();
        Enable = config.getBoolean("Enable");
        power = (float) config.getDouble("breakPower");
        breakBlock = config.getBoolean("breakBlock");
        Fire = config.getBoolean("Fire");
    }
}
