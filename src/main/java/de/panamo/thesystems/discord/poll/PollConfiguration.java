package de.panamo.thesystems.discord.poll;


import de.panamo.thesystems.discord.configuration.GeneralConfiguration;

public class PollConfiguration implements GeneralConfiguration {
    private long positiveEmoteId, negativeEmoteId;
    private String pollTitle, multiAnswerPollTitle;

    public long getPositiveEmoteId() {
        return positiveEmoteId;
    }

    public void setPositiveEmoteId(long positiveEmoteId) {
        this.positiveEmoteId = positiveEmoteId;
    }

    public long getNegativeEmoteId() {
        return negativeEmoteId;
    }

    public void setNegativeEmoteId(long negativeEmoteId) {
        this.negativeEmoteId = negativeEmoteId;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public String getMultiAnswerPollTitle() {
        return multiAnswerPollTitle;
    }

    public void setMultiAnswerPollTitle(String multiAnswerPollTitle) {
        this.multiAnswerPollTitle = multiAnswerPollTitle;
    }
}
