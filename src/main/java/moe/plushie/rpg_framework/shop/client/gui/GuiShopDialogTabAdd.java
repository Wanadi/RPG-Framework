package moe.plushie.rpg_framework.shop.client.gui;

import moe.plushie.rpg_framework.api.shop.IShop.IShopTab.TabType;
import moe.plushie.rpg_framework.core.client.gui.AbstractGuiDialog;
import moe.plushie.rpg_framework.core.client.gui.IDialogCallback;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiDropDownList;
import moe.plushie.rpg_framework.core.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.rpg_framework.core.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiShopDialogTabAdd extends AbstractGuiDialog implements IDropDownListCallback {

    private final static ResourceLocation ICONS = new ResourceLocation(LibGuiResources.ICONS);

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonAdd;
    private GuiButtonExt buttonIconPre;
    private GuiButtonExt buttonIconNext;
    private GuiTextField textFieldName;
    private GuiDropDownList dropDownTabTye;

    private int iconIndex;
    private TabType tabType = TabType.BUY;

    public GuiShopDialogTabAdd(GuiScreen parent, String name, IDialogCallback callback) {
        super(parent, name, callback, 190, 150);
        textFieldName = new GuiTextField(0, fontRenderer, 0, 0, width - 20, 12);
        slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, I18n.format("inventory.rpg_economy:common.button_close"));
        buttonAdd = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, I18n.format(name + ".button.add"));
        buttonIconPre = new GuiButtonExt(-1, x + 20 - 10, y + 35, 20, 20, "<");
        buttonIconNext = new GuiButtonExt(-1, x + width - 30, y + 35, 20, 20, ">");
        dropDownTabTye = new GuiDropDownList(-1, x + 10, y + 72, width - 20, "", this);
        for (TabType tabType : TabType.values()) {
            dropDownTabTye.addListItem(I18n.format("inventory.rpg_economy:common.tab_type." + tabType.toString().toLowerCase()), tabType.toString(), tabType == TabType.BUY);
        }
        dropDownTabTye.setListSelectedIndex(0);

        textFieldName.x = x + 10;
        textFieldName.y = y + 18;

        buttonList.add(buttonClose);
        buttonList.add(buttonAdd);
        buttonList.add(buttonIconPre);
        buttonList.add(buttonIconNext);
        buttonList.add(dropDownTabTye);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonAdd) {
            returnDialogResult(DialogResult.OK);
        }
        if (button == buttonIconPre) {
            iconIndex = MathHelper.clamp(iconIndex - 1, 0, 255);
        }
        if (button == buttonIconNext) {
            iconIndex = MathHelper.clamp(iconIndex + 1, 0, 255);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        textFieldName.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textFieldName.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return super.keyTyped(c, keycode);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        textFieldName.drawTextBox();

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(ICONS);
        int iconY = MathHelper.floor(iconIndex / 16);
        int iconX = iconIndex - (y * 16);

        String iconText = iconIndex + "/255";
        int textWidth = fontRenderer.getStringWidth(iconText);
        drawTexturedModalRect(x + width / 2 - 8, y + 35, 16 * iconX, 16 * iconY, 16, 16);
        fontRenderer.drawString(iconText, x + width / 2 - textWidth / 2, y + 52, 0x333333);

        fontRenderer.drawString(I18n.format(name + ".label.tab_type"), x + 10, y + 62, 0x333333);

        dropDownTabTye.drawForeground(mc, mouseX, mouseY, partialTickTime);
        drawTitle();
    }

    public String getTabName() {
        return textFieldName.getText().trim();
    }

    public int getTabIconIndex() {
        return iconIndex;
    }

    public TabType getTabType() {
        return tabType;
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        tabType = TabType.valueOf(dropDownList.getListSelectedItem().tag);
    }
}
