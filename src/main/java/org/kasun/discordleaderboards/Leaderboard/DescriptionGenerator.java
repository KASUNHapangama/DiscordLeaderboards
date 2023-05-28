package org.kasun.discordleaderboards.Leaderboard;

import org.kasun.discordleaderboards.Utils.TimeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.*;

public class DescriptionGenerator {
    private final Leaderboard leaderboard;
    private final List<String> embedDescriptionlist;
    private final TopList topList;
    private final long SECSFORHOUR = 3600;
    private final long SECSFORDAY = 86400;
    private final long SECSFORWEEK = 604800;
    private final long SECSSFORMONTH = 2629746;
    private int numberOfFloatingPoints;
    private boolean isHigherBetter;

    public DescriptionGenerator(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
        embedDescriptionlist = leaderboard.getConfig().getEmbedDescription();
        topList = leaderboard.getTopList();
        numberOfFloatingPoints = leaderboard.getConfig().getFloatingpoints();
        isHigherBetter = leaderboard.getConfig().isHigherisbetter();
        System.out.println("Debug " + numberOfFloatingPoints);
        System.out.println("Debug " + isHigherBetter);
    }

    public DescriptionGenerator(String leaderboardname) {
        this.leaderboard = new Leaderboard(leaderboardname);
        embedDescriptionlist = leaderboard.getConfig().getEmbedDescription();
        topList = leaderboard.getTopList();
    }

    public String getDescriptionForSrv(){
        String description = String.join("\n", embedDescriptionlist);

        //{toplist} placeholder
        String replacement1 = "```" + topList.getTopListAsString() + "```";
        if (replacement1.equals("``````") || replacement1 == null){
            description = description.replace("{toplist}", "```Leaderboard is empty!```");
        }else{
            description = description.replace("{toplist}", replacement1);
        }


        //{top-1-name} {top-1-score} placeholders
        Map<String, Double> playerScores = topList.getTopListAsMap();
        Pattern pattern = Pattern.compile("\\{top-(\\d+)-(name|score)\\}");
        Matcher matcher = pattern.matcher(description);
        while (matcher.find()) {
            int position = Integer.parseInt(matcher.group(1));
            String placeholderType = matcher.group(2);

            Comparator<Map.Entry<String, Double>> scoreComparator;

            if (isHigherBetter) {
                scoreComparator = Map.Entry.comparingByValue(Comparator.reverseOrder());
            } else {
                scoreComparator = Map.Entry.comparingByValue();
            }

            Map.Entry<String, Double> entry = playerScores.entrySet().stream()
                    .sorted(scoreComparator)
                    .skip(position - 1)
                    .findFirst()
                    .orElse(null);

            if (entry != null) {
                String placeholder = "{top-" + position + "-" + placeholderType + "}";
                int intvalue = entry.getValue().intValue();
                String replacement = placeholderType.equals("name") ? entry.getKey() : String.valueOf(intvalue);
                description = description.replace(placeholder, replacement);
            }
        }

        //{timestamp-now} placeholder
        description = description.replace("{timestamp-now}", "<t:" + TimeUtils.getCurrentUnixTimestamp() + ":R>");

        //{timestamp-next} placeholder
        String delay = leaderboard.getConfig().getDelay();
        switch (delay.toLowerCase()) {
            case "hourly":
                description = description.replace("{timestamp-next}", "<t:" + (TimeUtils.getCurrentUnixTimestamp() + SECSFORHOUR) + ":R>");
                break;
            case "daily":
                description = description.replace("{timestamp-next}", "<t:" + (TimeUtils.getCurrentUnixTimestamp() + SECSFORDAY) + ":R>");
                break;
            case "weekly":
                description = description.replace("{timestamp-next}", "<t:" + (TimeUtils.getCurrentUnixTimestamp() + SECSFORWEEK) + ":R>");
                break;
            case "monthly":
                description = description.replace("{timestamp-next}", "<t:" + (TimeUtils.getCurrentUnixTimestamp() + SECSSFORMONTH) + ":R>");
                break;
            default:
                break;
        }
        return description;
    }

    public String getDescriptionForWebhook(){
        String description = String.join("\\u000A", embedDescriptionlist);

        //{toplist} placeholder
        String replacement1 = "```" + topList.getTopListAsStringForWebhook() + "```";  //Currently Only Deference is this : more differences may accrue in future
        if (replacement1.equals("``````") || replacement1 == null){
            description = description.replace("{toplist}", "```Leaderboard is empty!```");
        }else{
            description = description.replace("{toplist}", replacement1);
        }


        //{top-1-name} {top-1-score} placeholders
        Map<String, Double> playerScores = topList.getTopListAsMap();
        Pattern pattern = Pattern.compile("\\{top-(\\d+)-(name|score)\\}");
        Matcher matcher = pattern.matcher(description);
        while (matcher.find()) {
            int position = Integer.parseInt(matcher.group(1));
            String placeholderType = matcher.group(2);

            Comparator<Map.Entry<String, Double>> scoreComparator;
            if (isHigherBetter){
                scoreComparator = Map.Entry.comparingByValue(Comparator.reverseOrder());
            }else {
                scoreComparator = Map.Entry.comparingByValue();
            }

            Map.Entry<String, Double> entry = playerScores.entrySet().stream()
                    .sorted(scoreComparator)
                    .skip(position - 1)
                    .findFirst()
                    .orElse(null);

            if (entry != null) {
                String placeholder = "{top-" + position + "-" + placeholderType + "}";
                int intvalue = entry.getValue().intValue();
                String replacement = placeholderType.equals("name") ? entry.getKey() : String.valueOf(intvalue);


                description = description.replace(placeholder, replacement);
            }
        }

        //{timestamp-now} placeholder
        description = description.replace("{timestamp-now}", "<t:" + TimeUtils.getCurrentUnixTimestamp() + ":R>");

        //{timestamp-next} placeholder
        String delay = leaderboard.getConfig().getDelay();
        switch (delay.toLowerCase()) {
            case "hourly":
                description = description.replace("{timestamp-next}", "<t:" + (TimeUtils.getCurrentUnixTimestamp() + SECSFORHOUR) + ":R>");
                break;
            case "daily":
                description = description.replace("{timestamp-next}", "<t:" + (TimeUtils.getCurrentUnixTimestamp() + SECSFORDAY) + ":R>");
                break;
            case "weekly":
                description = description.replace("{timestamp-next}", "<t:" + (TimeUtils.getCurrentUnixTimestamp() + SECSFORWEEK) + ":R>");
                break;
            case "monthly":
                description = description.replace("{timestamp-next}", "<t:" + (TimeUtils.getCurrentUnixTimestamp() + SECSSFORMONTH) + ":R>");
                break;
            default:
                break;
        }


        return description;
    }




}
