package io.github.itzclient.modules.hud;

import io.github.itzclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.itzclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.itzclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.itzclient.modules.AbstractCommonModule;
import io.github.itzclient.util.ClientColors;
import lombok.Getter;

import java.util.Locale;

public class NametagHud extends AbstractCommonModule {

    @Getter
    private static final NametagHud instance = new NametagHud();
    
    // --- Settings for this module ---
    public final OptionCategory category = OptionCategory.create("nametag");
    public final BooleanOption enabled = new BooleanOption("enabled", true);
    public final BooleanOption showCustomTag = new BooleanOption("showCustomTag", true);
    public final StringOption customTagText = new StringOption("customTagText", "[ITZ]");
    public final ColorOption customTagColor = new ColorOption("customTagColor", ClientColors.SELECTOR_BLUE);
    public final EnumOption<TagPosition> tagPosition = new EnumOption<>("tagPosition", TagPosition.class, TagPosition.LEFT);

    @Override
    public void init() {
        // Add all settings to our new category
        category.add(enabled);
        category.add(showCustomTag);
        category.add(customTagText);
        category.add(customTagColor);
        category.add(tagPosition);
        
        // Add the "Nametag" category to the main config screen
        client.getConfig().addCategory(category);
    }

    public enum TagPosition {
        LEFT, RIGHT;
        // This makes the option display nicely in the settings menu
        @Override public String toString() { return "nametag.position." + super.toString().toLowerCase(Locale.ROOT); }
    }
}