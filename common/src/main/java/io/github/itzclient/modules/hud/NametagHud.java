/*
 * Copyright © 2025 itzmojahide2 & Contributors
 *
 * This file is part of ItzClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */
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
    
    public final OptionCategory category = OptionCategory.create("nametag");
    public final BooleanOption enabled = new BooleanOption("enabled", true);
    public final BooleanOption showCustomTag = new BooleanOption("showCustomTag", true);
    public final StringOption customTagText = new StringOption("customTagText", "[ITZ]");
    public final ColorOption customTagColor = new ColorOption("customTagColor", ClientColors.SELECTOR_BLUE);
    public final EnumOption<TagPosition> tagPosition = new EnumOption<>("tagPosition", TagPosition.class, TagPosition.LEFT);

    @Override
    public void init() {
        category.add(enabled);
        category.add(showCustomTag);
        category.add(customTagText);
        category.add(customTagColor);
        category.add(tagPosition);
        
        client.getConfig().addCategory(category);
    }

    public enum TagPosition {
        LEFT, RIGHT;
        @Override public String toString() { return "nametag.position." + super.toString().toLowerCase(Locale.ROOT); }
    }
}