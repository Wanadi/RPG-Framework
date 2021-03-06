package moe.plushie.rpg_framework.currency.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.ICurrencyManager;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncCurrencies;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.currency.common.serialize.CurrencySerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CurrencyManager implements ICurrencyManager {

    private static final String DIRECTORY_NAME = "currency";

    private final File currencyDirectory;
    private final HashMap<String, Currency> currencyMap;

    public CurrencyManager(File modDirectory) {
        currencyDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!currencyDirectory.exists()) {
            currencyDirectory.mkdir();
        }
        currencyMap = new HashMap<String, Currency>();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void reload(boolean syncWithClients) {
        RPGFramework.getLogger().info("Loading Currencies");
        File[] files = currencyDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        currencyMap.clear();
        for (File file : files) {
            loadCurrency(file);
        }
        if (syncWithClients) {
            syncToAll();
        }
    }
    
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!event.player.getEntityWorld().isRemote) {
            syncToClient((EntityPlayerMP) event.player);
        }
    }
    
    public void syncToClient(EntityPlayerMP entityPlayer) {
        RPGFramework.getLogger().info("Sending " + currencyMap.size() + " currency list(s) to player " + entityPlayer.getName() + ".");
        PacketHandler.NETWORK_WRAPPER.sendTo(getSyncMessage(), entityPlayer);
    }
    
    private void syncToAll() {
        RPGFramework.getLogger().info("Sending " + currencyMap.size() + " currency list(s) to all players.");
        PacketHandler.NETWORK_WRAPPER.sendToAll(getSyncMessage());
    }
    
    private IMessage getSyncMessage() {
        return new MessageServerSyncCurrencies(getCurrencies());
    }
    
    public void gotCurrenciesFromServer(Currency[] currencies) {
        RPGFramework.getLogger().info("Got " + currencies.length + " currency list(s) from server.");
        currencyMap.clear();
        for (Currency currency : currencies) {
            currencyMap.put(currency.getIdentifier(), currency);
        }
    }
    
    private void loadCurrency(File currencyFile) {
        RPGFramework.getLogger().info("Loading currency: " + currencyFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(currencyFile);
        if (jsonElement != null) {
            Currency currency = CurrencySerializer.deserializeJson(jsonElement);
            if (currency != null) {
                currencyMap.put(currency.getIdentifier(), currency);
            }
        }
    }
    
    @Override
    public Currency getCurrency(String identifier) {
        return currencyMap.get(identifier);
    }

    @Override
    public Currency[] getCurrencies() {
        Currency[] currencies = currencyMap.values().toArray(new Currency[currencyMap.size()]);
        Arrays.sort(currencies);
        return currencies;
    }
    
    @Override
    public String[] getCurrencyNames() {
        return currencyMap.keySet().toArray(new String[currencyMap.size()]);
    }
    
    public int getCurrencyID(ICurrency currency) {
        Currency[] currencies = getCurrencies();
        for (int i = 0; i < currencies.length; i++) {
            if (currency.getName().equals(currencies[i].getName())) {
                return i;
            }
        }
        return -1;
    }
    
    public Currency getCurrencyFromID(int id) {
        Currency[] currencies = getCurrencies();
        if (id >= 0 & id < currencies.length) {
            return currencies[id];
        }
        return null;
    }
    
    public ICurrency getDefault() {
        ICurrency currency = getCurrency("common.json");
        if (currency != null) {
            return currency;
        }
        if (!currencyMap.isEmpty()) {
            return currencyMap.values().toArray(new ICurrency[currencyMap.size()])[0];
        }
        return null;
    }
}
