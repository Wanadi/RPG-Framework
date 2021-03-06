package moe.plushie.rpg_framework.shop.client.gui;

import java.util.ArrayList;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.GuiHelper;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiIconButton;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import moe.plushie.rpg_framework.core.common.ItemMatcherStack;
import moe.plushie.rpg_framework.core.common.inventory.slot.SlotHidable;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiShopUpdate;
import moe.plushie.rpg_framework.core.common.network.client.MessageClientGuiShopUpdate.ShopMessageType;
import moe.plushie.rpg_framework.shop.common.inventory.ContainerShop;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiShopDialogEditCostItems extends AbstractGuiDialog {

    private static final ResourceLocation TEXTURE_SHOP = new ResourceLocation(LibGuiResources.SHOP);

    private final int slotIndex;

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonEdit;
    private GuiIconButton[] buttonsMeta;
    private GuiIconButton[] buttonsNbt;

    private IItemMatcher[] cost;

    private boolean[] matchMeta;
    private boolean[] matchNbt;

    public GuiShopDialogEditCostItems(GuiScreen parent, String name, IDialogCallback callback, int width, int height, int slotIndex, IItemMatcher[] cost) {
        super(parent, name, callback, width, height);
        this.slotIndex = slotIndex;

        PacketHandler.NETWORK_WRAPPER.sendToServer(new MessageClientGuiShopUpdate(ShopMessageType.ITEM_COST_REQUEST).setSlotIndex(slotIndex));

        this.cost = cost;
        matchMeta = new boolean[5];
        matchNbt = new boolean[5];
        for (int i = 0; i < matchMeta.length; i++) {
            matchMeta[i] = true;
        }

        if (cost != null) {
            for (int i = 0; i < cost.length; i++) {
                if (cost[i] instanceof ItemMatcherStack & i < matchMeta.length) {
                    ItemMatcherStack matcherStack = (ItemMatcherStack) cost[i];
                    matchMeta[i] = matcherStack.isMatchMeta();
                    matchNbt[i] = matcherStack.isMatchNBT();
                }
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30 - 98, 80, 20, I18n.format("inventory.rpg_economy:common.button_close"));
        buttonEdit = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30 - 98, 80, 20, I18n.format("inventory.rpg_economy:common.button_edit"));
        buttonList.add(buttonClose);
        buttonList.add(buttonEdit);

        buttonsMeta = new GuiIconButton[5];
        buttonsNbt = new GuiIconButton[5];
        for (int i = 0; i < 5; i++) {
            buttonsMeta[i] = new GuiIconButton(parent, i, x + 10 + i * 38, y + 40, 18, 18, TEXTURE_SHOP).setDrawButtonBackground(false);
            if (matchMeta[i]) {
                buttonsMeta[i].setIconLocation(208, 0, 16, 16).setHoverText(I18n.format(name + ".button.meta.yes"));
            } else {
                buttonsMeta[i].setIconLocation(208, 16, 16, 16).setHoverText(I18n.format(name + ".button.meta.no"));
            }
            buttonsNbt[i] = new GuiIconButton(parent, i + 5, x + 10 + i * 38, y + 60, 18, 18, TEXTURE_SHOP).setDrawButtonBackground(false);
            if (matchNbt[i]) {
                buttonsNbt[i].setIconLocation(208, 32, 16, 16).setHoverText(I18n.format(name + ".button.nbt.yes"));
            } else {
                buttonsNbt[i].setIconLocation(208, 48, 16, 16).setHoverText(I18n.format(name + ".button.nbt.no"));
            }
            buttonList.add(buttonsMeta[i]);
            buttonList.add(buttonsNbt[i]);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonEdit) {
            returnDialogResult(DialogResult.OK);
        }
        if (button.id >= 0 & button.id < 5) {
            matchMeta[button.id] = !matchMeta[button.id];
            initGui();
        }
        if (button.id >= 5 & button.id < 10) {
            matchNbt[button.id - 5] = !matchNbt[button.id - 5];
            initGui();
        }
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        // drawParentCoverBackground();
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        mc.renderEngine.bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height - 98 - 1, textureWidth, textureHeight, borderSize, zLevel);

        // Render slots.
        mc.renderEngine.bindTexture(TEXTURE_SHOP);
        for (int i = 0; i < 5; i++) {
            drawTexturedModalRect(x + 10 + i * 38, y + 20, 100, 0, 18, 18);
        }

        GuiHelper.renderPlayerInvTexture(x, y + height - 98);
    }

    @Override
    protected void updateSlots(boolean restore) {
        ContainerShop containerShop = (ContainerShop) slotHandler.inventorySlots;
        GuiShop gui = (GuiShop) parent;
        if (!restore) {
            ArrayList<Slot> playerSlots = containerShop.getSlotsPlayer();
            int posX = x + 8 - gui.getGuiLeft();
            int posY = y + 138 - gui.getGuiTop();
            int playerInvY = posY;
            int hotBarY = playerInvY + 58;
            for (int ix = 0; ix < 9; ix++) {
                playerSlots.get(ix).xPos = posX + 18 * ix;
                playerSlots.get(ix).yPos = hotBarY;
            }
            for (int iy = 0; iy < 3; iy++) {
                for (int ix = 0; ix < 9; ix++) {
                    playerSlots.get(ix + iy * 9 + 9).xPos = posX + 18 * ix;
                    playerSlots.get(ix + iy * 9 + 9).yPos = playerInvY + iy * 18;
                }
            }
            for (Slot slot : containerShop.getSlotsShop()) {
                ((SlotHidable) slot).setVisible(false);
            }
            for (Slot slot : containerShop.getSlotsPrice()) {
                ((SlotHidable) slot).setVisible(true);
            }
        } else {
            ArrayList<Slot> playerSlots = containerShop.getSlotsPlayer();
            int posX = 24;
            int posY = 162;
            int playerInvY = posY;
            int hotBarY = playerInvY + 58;
            for (int ix = 0; ix < 9; ix++) {
                playerSlots.get(ix).xPos = posX + 18 * ix;
                playerSlots.get(ix).yPos = hotBarY;
            }
            for (int iy = 0; iy < 3; iy++) {
                for (int ix = 0; ix < 9; ix++) {
                    playerSlots.get(ix + iy * 9 + 9).xPos = posX + 18 * ix;
                    playerSlots.get(ix + iy * 9 + 9).yPos = playerInvY + iy * 18;
                }
            }
            for (Slot slot : containerShop.getSlotsShop()) {
                ((SlotHidable) slot).setVisible(true);
            }
            for (Slot slot : containerShop.getSlotsPrice()) {
                ((SlotHidable) slot).setVisible(false);
            }
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        drawTitle();
        GuiHelper.renderPlayerInvlabel(x, y + height - 98, fontRenderer);

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(ICONS);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);

        RenderHelper.enableGUIStandardItemLighting();
    }

    public IItemMatcher[] getCost() {
        ContainerShop containerShop = (ContainerShop) slotHandler.inventorySlots;
        ArrayList<IItemMatcher> matchers = new ArrayList<IItemMatcher>();
        ArrayList<Slot> slotsPrice = containerShop.getSlotsPrice();
        for (int i = 0; i < slotsPrice.size(); i++) {
            ItemStack stack = slotsPrice.get(i).getStack();
            if (!stack.isEmpty()) {
                matchers.add(new ItemMatcherStack(stack.copy(), matchMeta[i], matchNbt[i]));
            }
        }
        return matchers.toArray(new IItemMatcher[matchers.size()]);
    }
}
