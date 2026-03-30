package com.artur.playtech.utils;

import com.artur.playtech.model.PlaytechResult;

public class PlaytechResultFormatter {

    public static String format(PlaytechResult result) {
        StringBuilder sb = new StringBuilder();

        sb.append("Playtech QA Assignment Results\n\n");

        sb.append("1. Teams\n");
        sb.append("Count: ").append(result.teamCount()).append("\n");

        for (String teamName : result.teamNames()) {
            sb.append("- ").append(teamName).append("\n");
        }

        sb.append("\n");
        sb.append("2. Research Areas\n");

        for (String researchArea : result.researchAreas()) {
            sb.append("- ").append(researchArea).append("\n");
        }

        sb.append("\n");
        sb.append("3. Estonia Job Link (available from both Tallinn and Tartu)\n");
        sb.append(result.tallinnTartuJobLink()).append("\n");

        return sb.toString();
    }
}
