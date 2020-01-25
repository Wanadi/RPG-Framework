package moe.plushie.rpg_framework.mail.common.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.currency.common.serialize.CostSerializer;
import moe.plushie.rpg_framework.mail.common.MailSystem;

public class MailSystemSerializer {

    private static final String PROP_NAME = "name";
    private static final String PROP_CHARACTER_LIMIT = "characterLimit";
    private static final String PROP_MESSAGE_COST = "messageCost";
    private static final String PROP_ATTACHMENT_COST = "attachmentCost";
    private static final String PROP_INBOX_SIZE = "inboxSize";
    private static final String PROP_MAX_ATTACHMENTS = "maxAttachments";
    private static final String PROP_ALLOW_SENDING_TO_SELF = "allowSendingToSelf";

    private MailSystemSerializer() {
    }

    public static JsonElement serializeJson(MailSystem mailSystem) {
        if (mailSystem == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(PROP_NAME, mailSystem.getName());
        jsonObject.addProperty(PROP_CHARACTER_LIMIT, mailSystem.getCharacterLimit());
        jsonObject.add(PROP_MESSAGE_COST, CostSerializer.serializeJson(mailSystem.getMessageCost(), false));
        jsonObject.add(PROP_ATTACHMENT_COST, CostSerializer.serializeJson(mailSystem.getAttachmentCost(), false));
        jsonObject.addProperty(PROP_INBOX_SIZE, mailSystem.getInboxSize());
        jsonObject.addProperty(PROP_MAX_ATTACHMENTS, mailSystem.getMaxAttachments());
        jsonObject.addProperty(PROP_ALLOW_SENDING_TO_SELF, mailSystem.getAllowSendToSelf());

        return jsonObject;
    }

    public static MailSystem deserializeJson(JsonElement json, IIdentifier identifier) {
        try {
            JsonObject jsonObject = json.getAsJsonObject();

            String name = jsonObject.get(PROP_NAME).getAsString();
            int characterLimit = jsonObject.get(PROP_CHARACTER_LIMIT).getAsInt();
            ICost messageCost = CostSerializer.deserializeJson(jsonObject.get(PROP_MESSAGE_COST));
            ICost attachmentCost = CostSerializer.deserializeJson(jsonObject.get(PROP_ATTACHMENT_COST));
            int inboxSize = jsonObject.get(PROP_INBOX_SIZE).getAsInt();
            int maxAttachments = jsonObject.get(PROP_MAX_ATTACHMENTS).getAsInt();
            boolean allowSendingToSelf = jsonObject.get(PROP_ALLOW_SENDING_TO_SELF).getAsBoolean();

            MailSystem mailSystem = new MailSystem(identifier, name)
                    .setCharacterLimit(characterLimit)
                    .setMessageCost(messageCost)
                    .setAttachmentCost(attachmentCost)
                    .setInboxSize(inboxSize)
                    .setMaxAttachments(maxAttachments)
                    .setAllowSendToSelf(allowSendingToSelf);

            return mailSystem;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
