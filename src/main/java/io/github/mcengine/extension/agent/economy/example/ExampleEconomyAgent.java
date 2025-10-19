package io.github.mcengine.extension.agent.economy.example;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.extension.logger.MCEngineExtensionLogger;
import io.github.mcengine.api.economy.extension.agent.IMCEngineEconomyAgent;

import io.github.mcengine.extension.agent.economy.example.command.EconomyAgentCommand;
import io.github.mcengine.extension.agent.economy.example.listener.EconomyAgentListener;
import io.github.mcengine.extension.agent.economy.example.tabcompleter.EconomyAgentTabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;

/**
 * Main class for the Economy <b>Agent</b> example module.
 * <p>
 * Registers the {@code /economyagentexample} command and related event listeners.
 * Integrates with the {@link IMCEngineEconomyAgent} lifecycle.
 */
public class ExampleEconomyAgent implements IMCEngineEconomyAgent {

    /** Custom extension logger for this module, with contextual labeling. */
    private MCEngineExtensionLogger logger;

    /**
     * Initializes the Economy Agent example module.
     * Called automatically by the MCEngine core plugin.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onLoad(Plugin plugin) {
        // Initialize contextual logger once and keep it for later use.
        this.logger = new MCEngineExtensionLogger(plugin, "Agent", "EconomyExampleAgent");

        try {
            // Register event listener
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new EconomyAgentListener(plugin, this.logger), plugin);

            // Reflectively access Bukkit's CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Define the /economyagentexample command
            Command economyAgentExampleCommand = new Command("economyagentexample") {

                /** Handles command execution for /economyagentexample. */
                private final EconomyAgentCommand handler = new EconomyAgentCommand();

                /** Handles tab-completion for /economyagentexample. */
                private final EconomyAgentTabCompleter completer = new EconomyAgentTabCompleter();

                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return handler.onCommand(sender, this, label, args);
                }

                @Override
                public java.util.List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer.onTabComplete(sender, this, alias, args);
                }
            };

            economyAgentExampleCommand.setDescription("Economy Agent example command.");
            economyAgentExampleCommand.setUsage("/economyagentexample");

            // Dynamically register the /economyagentexample command
            commandMap.register(plugin.getName().toLowerCase(), economyAgentExampleCommand);

            this.logger.info("Enabled successfully.");
        } catch (Exception e) {
            this.logger.warning("Failed to initialize ExampleEconomyAgent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the Economy Agent example module is disabled/unloaded.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onDisload(Plugin plugin) {
        if (this.logger != null) {
            this.logger.info("Disabled.");
        }
    }

    /**
     * Sets the unique ID for this module.
     *
     * @param id the assigned identifier (ignored; a fixed ID is used for consistency)
     */
    @Override
    public void setId(String id) {
        MCEngineCoreApi.setId("mcengine-economy-agent-example");
    }
}
