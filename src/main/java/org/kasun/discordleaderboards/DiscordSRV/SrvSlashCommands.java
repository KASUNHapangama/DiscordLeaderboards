package org.kasun.discordleaderboards.DiscordSRV;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import org.bukkit.event.Listener;
import org.kasun.discordleaderboards.DiscordLeaderboards;
import org.kasun.discordleaderboards.Leaderboard.Leaderboard;
import org.kasun.discordleaderboards.Configs.MainConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SrvSlashCommands implements Listener, SlashCommandProvider {
    private final DiscordLeaderboards plugin  = DiscordLeaderboards.getInstance();
    MainConfig mainConfig = new MainConfig();
    List<String> leaderboardList = mainConfig.getLeaderboardsList();
    private final String slashCommand = mainConfig.getSlashcommand();

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        CommandData commandData = new CommandData(slashCommand, "view leaderboards");
        List<OptionData> options = new ArrayList<>();
        OptionData dropdownOption = new OptionData(OptionType.INTEGER, "leaderboard", "Dropdown Option Description", true);

        if (leaderboardList.size() != 0) {
            for (String subcommandName : leaderboardList) {
                dropdownOption.addChoice(subcommandName, leaderboardList.indexOf(subcommandName));
            }
        }
        options.add(dropdownOption);
        commandData.addOptions(options);




        return new HashSet<>(Arrays.asList(

                // ping pong
                new PluginSlashCommand(plugin, new CommandData("ping", "A classic match of ping pong")),

                // bests
                new PluginSlashCommand(plugin, commandData)

        ));


    }


    @SlashCommand(path = "*", deferReply = true)
    public void bestPlugin(SlashCommandEvent event) {
        if (!event.getCommandPath().equals(slashCommand)){
            //ignored
        } else{
            CompletableFuture.runAsync(() -> {
                int option = (int) event.getOption("leaderboard").getAsDouble();
                Leaderboard leaderboard = new Leaderboard(leaderboardList.get(option));
                LeaderboardDiscordSrvEmbed leaderboardDiscordSrvEmbed = new LeaderboardDiscordSrvEmbed(leaderboard);
                MessageEmbed messageEmbed = leaderboardDiscordSrvEmbed.getDiscordsrvEmbed().getMessageEmbed();

                event.getHook().sendMessageEmbeds(messageEmbed).queue();
            });
        }
    }

    @SlashCommand(path = "ping")
    public void pingCommand(SlashCommandEvent event) {
        event.reply("Pong!").queue();
    }
}
