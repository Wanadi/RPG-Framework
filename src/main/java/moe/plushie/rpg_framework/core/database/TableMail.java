package moe.plushie.rpg_framework.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.JsonArray;

import moe.plushie.rpg_framework.api.mail.IMailSystem;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.mail.common.MailListItem;
import moe.plushie.rpg_framework.mail.common.MailMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public final class TableMail {

    private TableMail() {
    }

    private static final String SQL_CREATE_TABLE = 
                    "CREATE TABLE IF NOT EXISTS mail" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "mail_system VARCHAR(64) NOT NULL," +
                    "player_id_sender INTEGER NOT NULL," +
                    "player_id_receiver INTEGER NOT NULL," +
                    "subject VARCHAR(64) NOT NULL," +
                    "text TEXT NOT NULL," +
                    "attachments TEXT NOT NULL," +
                    "sent_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                    "read BOOLEAN NOT NULL)";

    public static void create() {
        DatabaseManager.executeUpdate(SQL_CREATE_TABLE);
    }

    private static final String SQL_MESSAGE_ADD = "INSERT INTO mail (id, mail_system, player_id_sender, player_id_receiver, subject, text, attachments, sent_date, read) VALUES (NULL, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

    public static boolean addMessage(MailMessage message) {
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_ADD)) {
            DBPlayer dbPlayerSender = TablePlayers.getPlayer(message.getSender());
            DBPlayer dbPlayerReceiver = TablePlayers.getPlayer(message.getReceiver());
            if (dbPlayerReceiver != DBPlayer.MISSING) {
                ps.setObject(1, message.getMailSystem().getIdentifier().getValue());
                ps.setInt(2, dbPlayerSender.getId());
                ps.setInt(3, dbPlayerReceiver.getId());
                ps.setString(4, message.getSubject());
                ps.setString(5, message.getMessageText());
                ps.setString(6, SerializeHelper.writeItemsToJson(message.getAttachments(), false).toString());
                ps.setBoolean(7, false);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static final String SQL_MESSAGE_LIST_GET = "SELECT id, subject, attachments, read FROM mail WHERE mail_system=?";

    public static ArrayList<MailListItem> getMessageList(EntityPlayer player, IMailSystem mailSystem) {
        ArrayList<MailListItem> listItems = new ArrayList<MailListItem>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_LIST_GET)) {
            ps.setObject(1, mailSystem.getIdentifier().getValue());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                listItems.add(new MailListItem(resultSet.getInt("id"), resultSet.getString("subject"), resultSet.getString("attachments").length() > 2, resultSet.getBoolean("read")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listItems;
    }

    private static final String SQL_MESSAGES_GET = "SELECT * FROM mail WHERE mail_system=? AND player_id_receiver=?";

    public static ArrayList<MailMessage> getMessages(EntityPlayer player, IMailSystem mailSystem) {
        ArrayList<MailMessage> mailMessages = new ArrayList<MailMessage>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGES_GET)) {
            DBPlayerInfo dbPlayerReceiver = TablePlayers.getPlayerInfo(player.getGameProfile());
            ps.setObject(1, mailSystem.getIdentifier().getValue());
            ps.setInt(2, dbPlayerReceiver.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                DBPlayerInfo dbPlayerSender = TablePlayers.getPlayer(resultSet.getInt("player_id_sender"));
                Date sendDateTime = resultSet.getDate("sent_date");
                String subject = resultSet.getString("subject");
                String messageText = resultSet.getString("text");
                JsonArray jsonArray = SerializeHelper.stringToJson(resultSet.getString("attachments")).getAsJsonArray();
                NonNullList<ItemStack> attachments = SerializeHelper.readItemsFromJson(jsonArray);
                boolean read = resultSet.getBoolean("read");
                mailMessages.add(new MailMessage(id, mailSystem, dbPlayerSender.getGameProfile(), dbPlayerReceiver.getGameProfile(), sendDateTime, subject, messageText, attachments, read));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mailMessages;
    }
    
    private static final String SQL_UNREAD_MESSAGES_GET = "SELECT COUNT(*) FROM mail WHERE mail_system=? AND player_id_receiver=? AND read=?";
    
    public static int getUnreadMessagesCount(EntityPlayer entityPlayer, IMailSystem mailSystem) {
        int count = 0;
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_UNREAD_MESSAGES_GET)) {
            DBPlayerInfo dbPlayerReceiver = TablePlayers.getPlayerInfo(entityPlayer.getGameProfile());
            ps.setObject(1, mailSystem.getIdentifier().getValue());
            ps.setInt(2, dbPlayerReceiver.getId());
            ps.setBoolean(3, false);
            ResultSet resultSet =  ps.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static final String SQL_MESSAGE_GET = "SELECT * FROM mail WHERE id=?";

    public static MailMessage getMessage(int id) {
        MailMessage message = null;
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_GET)) {
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                IMailSystem mailSystem = RPGFramework.getProxy().getMailSystemManager().getMailSystem(new IdentifierString(resultSet.getString("mail_system")));
                DBPlayerInfo dbPlayerSender = TablePlayers.getPlayer(conn, resultSet.getInt("player_id_sender"));
                DBPlayerInfo dbPlayerReceiver = TablePlayers.getPlayer(conn, resultSet.getInt("player_id_receiver"));
                Date sendDateTime = resultSet.getDate("sent_date");
                String subject = resultSet.getString("subject");
                String messageText = resultSet.getString("text");
                JsonArray jsonArray = SerializeHelper.stringToJson(resultSet.getString("attachments")).getAsJsonArray();
                NonNullList<ItemStack> attachments = SerializeHelper.readItemsFromJson(jsonArray);
                boolean read = resultSet.getBoolean("read");
                message = new MailMessage(id, mailSystem, dbPlayerSender.getGameProfile(), dbPlayerReceiver.getGameProfile(), sendDateTime, subject, messageText, attachments, read);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    private static final String SQL_DELETE_MESSAGE = "DELETE FROM mail WHERE id=?";

    public static void deleteMessage(int messageId) {
        create();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_DELETE_MESSAGE)) {
            ps.setInt(1, messageId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static final String SQL_MESSAGE_MARK_READ = "UPDATE mail SET read=? WHERE id=?";

    public static void markMessageasRead(int messageId) {
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MESSAGE_MARK_READ)) {
            ps.setBoolean(1, true);
            ps.setInt(2, messageId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String SQL_CLEAR_MESSAGE_ITEMS = "UPDATE mail SET attachments=? WHERE id=?";
    
    public static void clearMessageItems(int messageId) {
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_CLEAR_MESSAGE_ITEMS)) {
            ps.setString(1, "[]");
            ps.setInt(2, messageId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
