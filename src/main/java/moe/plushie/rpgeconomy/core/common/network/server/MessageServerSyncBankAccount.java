package moe.plushie.rpgeconomy.core.common.network.server;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;
import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.api.bank.IBankAccount;
import moe.plushie.rpgeconomy.api.bank.IBankCapability;
import moe.plushie.rpgeconomy.bank.common.capability.BankCapability;
import moe.plushie.rpgeconomy.bank.common.serialize.BankAccountSerializer;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageServerSyncBankAccount implements IMessage, IMessageHandler<MessageServerSyncBankAccount, IMessage> {

    private IBankAccount[] bankAccounts;

    public MessageServerSyncBankAccount() {
    }

    public MessageServerSyncBankAccount(IBankAccount... bankAccount) {
        this.bankAccounts = bankAccount;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(bankAccounts.length);
        for (int i = 0; i < bankAccounts.length; i++) {
            ByteBufUtils.writeUTF8String(buf, bankAccounts[i].getBank().getIdentifier());
            JsonElement json = BankAccountSerializer.serializeJson(bankAccounts[i], false);
            ByteBufUtils.writeUTF8String(buf, json.toString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        bankAccounts = new IBankAccount[buf.readInt()];
        for (int i = 0; i < bankAccounts.length; i++) {
            String bankIdentifier = ByteBufUtils.readUTF8String(buf);
            IBank bank = RpgEconomy.getProxy().getBankManager().getBank(bankIdentifier);
            String jsonString = ByteBufUtils.readUTF8String(buf);
            JsonElement json = SerializeHelper.stringToJson(jsonString);
            bankAccounts[i] = BankAccountSerializer.deserializeJson(json, bank);
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncBankAccount message, MessageContext ctx) {
        setBankAccounts(message.bankAccounts);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void setBankAccounts(IBankAccount[] bankAccounts) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = Minecraft.getMinecraft().player;
                IBankCapability capability = BankCapability.get(player);
                if (capability != null) {
                    for (int i = 0; i < bankAccounts.length; i++) {
                        capability.setBankAccount(bankAccounts[i]);
                    }
                }
            }
        });
    }
}
