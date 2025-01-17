package com.davenonymous.libnonymous.gui.framework;


import com.davenonymous.libnonymous.gui.framework.event.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

public abstract class WidgetContainerScreen<T extends WidgetContainer> extends ContainerScreen<T> {
    protected GUI gui;

    private int previousMouseX = Integer.MAX_VALUE;
    private int previousMouseY = Integer.MAX_VALUE;
    public boolean dataUpdated = false;

    public WidgetContainerScreen(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);

        this.gui = createGUI();
        this.gui.setVisible(true);
    }

    protected abstract GUI createGUI();

    @Override
    public void tick() {
        super.tick();
        gui.fireEvent(new UpdateScreenEvent());
        this.resetMousePositions();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(gui.fireEvent(new MouseClickEvent(mouseX, mouseY, mouseButton)) == WidgetEventResult.CONTINUE_PROCESSING) {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        Slot slot = this.getSlotUnderMouse();
        //if(slot instanceof WidgetSlot) {
            if (gui.fireEvent(new MouseReleasedEvent(mouseX, mouseY, mouseButton)) == WidgetEventResult.CONTINUE_PROCESSING) {
                return super.mouseReleased(mouseX, mouseY, mouseButton);
            }
        //}

        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(gui.fireEvent(new KeyReleasedEvent(keyCode, scanCode, modifiers)) == WidgetEventResult.CONTINUE_PROCESSING) {
            return super.keyReleased(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int code) {
        if(gui.fireEvent(new CharTypedEvent(chr, code)) == WidgetEventResult.CONTINUE_PROCESSING) {
            return super.charTyped(chr, code);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(gui.fireEvent(new KeyPressedEvent(keyCode, scanCode, modifiers)) == WidgetEventResult.CONTINUE_PROCESSING) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if(gui.fireEvent(new MouseScrollEvent(mouseX, mouseY, scrollAmount)) == WidgetEventResult.CONTINUE_PROCESSING) {
            return super.mouseScrolled(mouseX, mouseY, scrollAmount);
        }
        return false;
    }

    /*
    // TODO: Mouse drag and scroll events
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if(gui.fireEvent(new MouseClickMoveEvent(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) == WidgetEventResult.CONTINUE_PROCESSING) {
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }
    */

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(dataUpdated) {
            dataUpdated = false;
            gui.fireEvent(new GuiDataUpdatedEvent());
        }

        if(mouseX != previousMouseX || mouseY != previousMouseY) {
            gui.fireEvent(new MouseMoveEvent(mouseX, mouseY));

            previousMouseX = mouseX;
            previousMouseY = mouseY;
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);

        matrixStack.pushPose();
        matrixStack.translate(-leftPos, -topPos+18, 0.0f);
        gui.drawTooltips(matrixStack, this, mouseX, mouseY);
        renderTooltip(matrixStack, mouseX, mouseY);
        matrixStack.popPose();
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        gui.drawGUI(matrixStack, this);

        if(this.menu != null && this.menu.slots != null) {
            for(Slot slot : this.menu.slots) {
                if(!slot.isActive()) {
                    continue;
                }

                gui.drawSlot(matrixStack, this, slot, leftPos, topPos);
            }
        }
    }

    protected void resetMousePositions() {
        this.previousMouseX = Integer.MIN_VALUE;
        this.previousMouseY = Integer.MIN_VALUE;
    }

    public void fireDataUpdateEvent() {
        dataUpdated = true;
    }
}