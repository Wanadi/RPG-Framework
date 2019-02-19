package moe.plushie.rpgeconomy.client.gui;

import moe.plushie.rpgeconomy.client.lib.LibGuiResources;
import moe.plushie.rpgeconomy.common.inventory.ContainerWallet;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWallet extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WALLET);

    public GuiWallet(EntityPlayer entityPlayer) {
        super(new ContainerWallet(entityPlayer));
    }
    
    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = 168;
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, 176, 68);
        GuiHelper.renderPlayerInvTexture(getGuiLeft(), getGuiTop() + 70);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(fontRenderer, getXSize(), "wallet");
        GuiHelper.renderPlayerInvlabel(0, 70, fontRenderer);
        fontRenderer.drawString("              �235,235", 36, 25, 0x333333);
    }
}
