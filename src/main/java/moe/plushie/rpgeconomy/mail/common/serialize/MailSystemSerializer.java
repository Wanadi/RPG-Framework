package moe.plushie.rpgeconomy.mail.common.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpgeconomy.currency.common.Wallet;
import moe.plushie.rpgeconomy.currency.serialize.WalletSerializer;
import moe.plushie.rpgeconomy.mail.common.MailSystem;

public class MailSystemSerializer {

    private static final String PROP_NAME = "name";
    private static final String PROP_CHARACTER_LIMIT = "characterLimit";
    private static final String PROP_MESSAGE_COST = "messageCost";
    private static final String PROP_ATTACHMENT_COST = "attachmentCost";
    private static final String PROP_INBOX_SIZE = "inboxSize";
    private static final String PROP_MAX_ATTACHMENTS = "maxAttachments";

    private MailSystemSerializer() {
    }

    public static JsonElement serializeJson(MailSystem mailSystem) {
        if (mailSystem == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(PROP_NAME, mailSystem.getName());
        jsonObject.addProperty(PROP_CHARACTER_LIMIT, mailSystem.getCharacterLimit());
        jsonObject.add(PROP_MESSAGE_COST, WalletSerializer.serializeJson(mailSystem.getMessageCost()));
        jsonObject.add(PROP_ATTACHMENT_COST, WalletSerializer.serializeJson(mailSystem.getAttachmentCost()));
        jsonObject.addProperty(PROP_INBOX_SIZE, mailSystem.getInboxSize());
        jsonObject.addProperty(PROP_MAX_ATTACHMENTS, mailSystem.getMaxAttachments());

        return jsonObject;
    }

    public static MailSystem deserializeJson(JsonElement json) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            String name = jsonObject.get(PROP_NAME).getAsString();
            int characterLimit = jsonObject.get(PROP_CHARACTER_LIMIT).getAsInt();
            Wallet messageCost = WalletSerializer.deserializeJson(jsonObject.get(PROP_MESSAGE_COST));
            Wallet attachmentCost = WalletSerializer.deserializeJson(jsonObject.get(PROP_ATTACHMENT_COST));
            int inboxSize = jsonObject.get(PROP_INBOX_SIZE).getAsInt();
            int maxAttachments = jsonObject.get(PROP_MAX_ATTACHMENTS).getAsInt();

            MailSystem mailSystem = new MailSystem(name)
                    .setCharacterLimit(characterLimit)
                    .setMessageCost(messageCost)
                    .setAttachmentCost(attachmentCost)
                    .setInboxSize(inboxSize)
                    .setMaxAttachments(maxAttachments);

            return mailSystem;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
