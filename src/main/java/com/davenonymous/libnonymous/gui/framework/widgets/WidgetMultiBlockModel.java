package com.davenonymous.libnonymous.gui.framework.widgets;

import com.davenonymous.libnonymous.Libnonymous;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.render.MultiblockBlockModelRenderer;
import com.davenonymous.libnonymous.render.RenderTickCounter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.vector.Quaternion;

import org.lwjgl.opengl.GL11;

public class WidgetMultiBlockModel extends Widget {
    private MultiblockBlockModel model;

    public WidgetMultiBlockModel(MultiblockBlockModel model) {
        this.model = model;
    }

    @Override
    public void draw(MatrixStack stack, Screen screen) {
        float angle = RenderTickCounter.renderTicks * 45.0f / 128.0f;

        stack.pushPose();

        // Init RenderSystem
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.disableFog();
        RenderSystem.disableLighting();
        RenderHelper.turnOff();

        RenderSystem.enableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();

        if (Minecraft.useAmbientOcclusion()) {
            RenderSystem.shadeModel(GL11.GL_SMOOTH);
        } else {
            RenderSystem.shadeModel(GL11.GL_FLAT);
        }

        RenderSystem.disableRescaleNormal();

        // Bring to front
        stack.translate(0F, 0F, 216.5F);

        float scaledWidth = this.width * 1.4f;
        double scaledHeight = this.height * 1.4d;

        stack.translate(scaledWidth / 2.0f, scaledHeight / 2.0f, 0.0d);

        // Shift it a bit down so one can properly see 3d
        stack.mulPose(new Quaternion(-25.0f, 1.0f, 0.0f, 0.0f));

        // Rotate per our calculated time
        stack.mulPose(new Quaternion(angle, 0.0f, 1.0f, 0.0f));

        float scale = (float)model.getScaleRatio(true);
        stack.scale(scale, scale, scale);

        stack.scale(scaledWidth, scaledWidth, scaledWidth);


        stack.mulPose(new Quaternion(180.0f, 1.0f, 0.0f, 0.0f));

        stack.translate(
                (model.width + 1) / -2.0f,
                (model.height + 1) / -2.0f,
                (model.depth + 1) / -2.0f
        );

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS).setBlurMipmap(false, false);

        GL11.glFrontFace(GL11.GL_CW);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        IRenderTypeBuffer buffer = IRenderTypeBuffer.immediate(builder);

        // TODO: Do not render with players position
        MultiblockBlockModelRenderer.renderModel(this.model, new MatrixStack(), buffer, 15728880,  OverlayTexture.NO_OVERLAY, Libnonymous.proxy.getClientWorld(), Libnonymous.proxy.getClientPlayer().blockPosition());

        textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS).restoreLastBlurMipmap();

        ((IRenderTypeBuffer.Impl) buffer).endBatch();

        GL11.glFrontFace(GL11.GL_CCW);

        RenderSystem.disableBlend();

        stack.popPose();
    }
}
