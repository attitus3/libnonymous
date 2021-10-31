package com.davenonymous.libnonymous.gui.config.types;

import com.davenonymous.libnonymous.gui.framework.ColorHelper;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseEnterEvent;
import com.davenonymous.libnonymous.gui.framework.event.MouseExitEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetFontAwesome;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetInputField;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import com.davenonymous.libnonymous.utils.Logz;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListSettingListEntry extends SettingListEntry {
    WidgetInputField inputField;

    public ListSettingListEntry(String optionKey, String comment, ForgeConfigSpec.ConfigValue value, Object defaultValue, int columnWidth) {
        super(optionKey, comment, value, defaultValue, columnWidth);
    }

    @Override
    public void fillPanel(int entryHeight) {
        if(defaultValue == null || !(defaultValue instanceof List) || ((List)defaultValue).size() == 0 ) {
            this.addUnsupportedRow(entryHeight);

            Logz.warn("Config lists without default value are not supported. We don't know the type otherwise!");
            return;
        }

        this.setSize(columnWidth, entryHeight+28);

        WidgetTextBox textBox = new WidgetTextBox(I18n.get("libnonymous.config.gui.label.separate_with_commas"), 0xA0CCCCCC);
        textBox.setSize(columnWidth-10, 9);
        textBox.setPosition(2, entryHeight-4);
        this.add(textBox);

        Object firstDefaultValue = ((List) defaultValue).get(0);
        if(firstDefaultValue instanceof String) {
            List<String> val = (List<String>) value.get();

            inputField = new WidgetInputField();
            inputField.setValue(buildStringFromValue(val));
            inputField.setDimensions(3, 35, columnWidth-26, 14);
            this.add(inputField);

            WidgetFontAwesome save = new WidgetFontAwesome(SAVE_ICON, WidgetFontAwesome.IconSize.MEDIUM);
            save.setColor(ColorHelper.COLOR_DISABLED);
            save.setPosition(columnWidth-16, 34);
            save.addListener(MouseEnterEvent.class, (event, widget) -> {
                save.setColor(ColorHelper.COLOR_ENABLED);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            save.addListener(MouseExitEvent.class, (event, widget) -> {
                save.setColor(ColorHelper.COLOR_DISABLED);
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            save.addListener(MouseClickEvent.class, (event, widget) -> {
                String inputText = inputField.getText();
                if(inputText == null || inputText.length() == 0) {
                    value.set(Collections.EMPTY_LIST);
                } else {
                    value.set(Arrays.asList(inputText.split(",")));
                }
                value.save();
                updateDefaultIconState();
                return WidgetEventResult.CONTINUE_PROCESSING;
            });
            this.add(save);
        }
    }

    private String buildStringFromValue(List<String> val) {
        StringBuilder sb = new StringBuilder();

        for(String listEntry : val) {
            sb.append(listEntry);
            sb.append(",");
        }
        if(sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    @Override
    public void setValueInInputField(Object defaultValue) {
        if(inputField != null) {
            if(defaultValue == null) {
                inputField.setValue("");
            } else {
                inputField.setValue(buildStringFromValue((List<String>) defaultValue));
            }
        }
    }
}
